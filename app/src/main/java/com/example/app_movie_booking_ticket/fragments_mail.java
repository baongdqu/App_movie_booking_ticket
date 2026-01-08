package com.example.app_movie_booking_ticket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_movie_booking_ticket.adapter.TicketAdapter;
import com.example.app_movie_booking_ticket.model.Movie;
import com.example.app_movie_booking_ticket.model.TicketSimple;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.*;

public class fragments_mail extends Fragment {

        private RecyclerView rvTickets;
        private LinearLayout emptyStateLayout;
        private ProgressBar progressBar;
        private TextView tvTicketCount;

        private TicketAdapter adapter;
        private final List<TicketSimple> ticketList = new ArrayList<>();

        private final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        private String currentUserId;

        private final Map<String, Movie> movieMap = new HashMap<>();

        // ✅ nhận kết quả từ TicketDetailActivity để reload list
        private final ActivityResultLauncher<Intent> ticketDetailLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                                loadTickets(); // ✅ reload sau khi hoàn vé
                        }
                });

        @Nullable
        @Override
        public View onCreateView(
                @NonNull LayoutInflater inflater,
                @Nullable ViewGroup container,
                @Nullable Bundle savedInstanceState) {

                View view = inflater.inflate(R.layout.layouts_fragments_mail, container, false);

                rvTickets = view.findViewById(R.id.rvTickets);
                emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
                progressBar = view.findViewById(R.id.progressBar);
                tvTicketCount = view.findViewById(R.id.tvTicketCount);

                rvTickets.setLayoutManager(new LinearLayoutManager(getContext()));

                adapter = new TicketAdapter(
                        requireContext(),
                        ticketList,
                        this::refundTicket,
                        this::openTicketDetail
                );
                rvTickets.setAdapter(adapter);

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        loadMoviesThenTickets();
                } else {
                        showEmptyState();
                }

                return view;
        }

        private void showLoading() {
                progressBar.setVisibility(View.VISIBLE);
                rvTickets.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.GONE);
        }

        private void showEmptyState() {
                progressBar.setVisibility(View.GONE);
                rvTickets.setVisibility(View.GONE);
                emptyStateLayout.setVisibility(View.VISIBLE);
                tvTicketCount.setVisibility(View.GONE);
        }

        private void showTickets() {
                progressBar.setVisibility(View.GONE);
                rvTickets.setVisibility(View.VISIBLE);
                emptyStateLayout.setVisibility(View.GONE);

                int count = ticketList.size();
                tvTicketCount.setText(count + " vé");
                tvTicketCount.setVisibility(View.VISIBLE);
        }

        private void loadMoviesThenTickets() {
                showLoading();

                db.child("Movies")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        movieMap.clear();

                                        for (DataSnapshot item : snapshot.getChildren()) {
                                                Movie movie = item.getValue(Movie.class);
                                                if (movie != null && movie.getTitle() != null) {
                                                        movieMap.put(movie.getTitle().toLowerCase(), movie);
                                                }
                                        }

                                        loadTickets();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                        showEmptyState();
                                }
                        });
        }

        private void loadTickets() {
                if (currentUserId == null) {
                        showEmptyState();
                        return;
                }

                db.child("tickets")
                        .orderByChild("userId")
                        .equalTo(currentUserId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        ticketList.clear();

                                        for (DataSnapshot t : snapshot.getChildren()) {

                                                String status = t.child("status").getValue(String.class);
                                                if (status != null && !"PAID".equals(status)) continue; // ✅ REFUNDED sẽ bị lọc

                                                String movieTitle = t.child("movieTitle").getValue(String.class);
                                                if (movieTitle == null) continue;

                                                Movie movie = movieMap.get(movieTitle.toLowerCase());
                                                if (movie == null) continue;

                                                String posterUrl = t.child("posterUrl").getValue(String.class);
                                                String date = t.child("date").getValue(String.class);
                                                String time = t.child("time").getValue(String.class);
                                                String cinemaName = t.child("cinemaName").getValue(String.class); // Lấy tên rạp
                                                Long totalPrice = t.child("totalPrice").getValue(Long.class);
                                                TicketSimple.PaymentInfo paymentInfo = null;
                                                DataSnapshot pSnap = t.child("payment");
                                                if (pSnap.exists()) {
                                                        paymentInfo = new TicketSimple.PaymentInfo();
                                                        paymentInfo.method = pSnap.child("method").getValue(String.class);
                                                        paymentInfo.paidAt = pSnap.child("paidAt").getValue(Long.class);
                                                }
                                                List<String> seats = new ArrayList<>();
                                                if (t.child("seats").exists()) {
                                                        for (DataSnapshot s : t.child("seats").getChildren()) {
                                                                seats.add(s.getValue(String.class));
                                                        }
                                                }

                                                // Khởi tạo TicketSimple với đầy đủ thông tin mới
                                                TicketSimple ticket = new TicketSimple();
                                                ticket.setTicketId(t.getKey());
                                                ticket.movieTitle = movieTitle;
                                                ticket.posterUrl = posterUrl;
                                                ticket.date = date;
                                                ticket.time = time;
                                                ticket.cinemaName = cinemaName;
                                                ticket.totalPrice = (totalPrice != null) ? totalPrice : 0L;
                                                ticket.seats = seats;
                                                ticket.payment = paymentInfo;

                                                ticketList.add(ticket);
                                        }
                                        Collections.sort(ticketList, (a, b) -> {
                                                long timeA = (a.payment != null) ? a.payment.paidAt : 0;
                                                long timeB = (b.payment != null) ? b.payment.paidAt : 0;
                                                return Long.compare(timeB, timeA); // Sắp xếp giảm dần theo thời gian thanh toán
                                        });
                                        adapter.notifyDataSetChanged();

                                        if (ticketList.isEmpty()) showEmptyState();
                                        else showTickets();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                        showEmptyState();
                                }
                        });
        }

        private void openTicketDetail(TicketSimple ticket) {
                if (ticket == null) return;
                String ticketId = ticket.getTicketId();
                if (ticketId == null || ticketId.trim().isEmpty()) return;

                Intent i = new Intent(requireContext(), TicketDetailActivity.class);
                i.putExtra(TicketDetailActivity.EXTRA_TICKET_ID, ticketId);

                ticketDetailLauncher.launch(i);
        }

        // ================== REFUND (FIX: release seat) ==================
        private void refundTicket(TicketSimple ticket) {

                String ticketId = ticket.getTicketId();
                if (ticketId == null || ticketId.trim().isEmpty()) return;

                DatabaseReference ticketRef = db.child("tickets").child(ticketId);

                ticketRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshotTicket) {

                                if (!snapshotTicket.exists()) return;

                                String status = snapshotTicket.child("status").getValue(String.class);
                                if (status == null) status = "PAID";

                                if (!"PAID".equals(status)) {
                                        Toast.makeText(getContext(), "Vé đã được hoàn", Toast.LENGTH_SHORT).show();
                                        return;
                                }

                                // ===== LẤY DATA =====
                                String movieTitle = snapshotTicket.child("movieTitle").getValue(String.class);
                                String date = snapshotTicket.child("date").getValue(String.class);
                                String time = snapshotTicket.child("time").getValue(String.class);
                                String cinemaId = snapshotTicket.child("cinemaId").getValue(String.class); // ✅ luôn có

                                List<String> seats = new ArrayList<>();
                                if (snapshotTicket.child("seats").exists()) {
                                        for (DataSnapshot s : snapshotTicket.child("seats").getChildren()) {
                                                String seat = s.getValue(String.class);
                                                if (seat != null) seats.add(seat);
                                        }
                                }

                                Long totalPriceObj = snapshotTicket.child("totalPrice").getValue(Long.class);
                                final long totalPrice = (totalPriceObj != null) ? totalPriceObj : 0L;

                                if (movieTitle == null || date == null || time == null || cinemaId == null || seats.isEmpty()) {
                                        Toast.makeText(getContext(), "Thiếu dữ liệu vé để hoàn", Toast.LENGTH_SHORT).show();
                                        return;
                                }

                                DatabaseReference balanceRef = db.child("users")
                                        .child(currentUserId)
                                        .child("balance");

                                // 1) CỘNG TIỀN
                                balanceRef.runTransaction(new Transaction.Handler() {
                                        @NonNull
                                        @Override
                                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                Long current = currentData.getValue(Long.class);
                                                if (current == null) current = 0L;
                                                currentData.setValue(current + totalPrice);
                                                return Transaction.success(currentData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {

                                                if (error != null || !committed) {
                                                        Toast.makeText(getContext(), "Hoàn tiền thất bại", Toast.LENGTH_SHORT).show();
                                                        return;
                                                }

                                                // 2) UPDATE STATUS + TRẢ GHẾ VỀ "available" (KHÔNG XÓA KEY)
                                                Map<String, Object> updates = new HashMap<>();
                                                updates.put("tickets/" + ticketId + "/status", "REFUNDED");
                                                updates.put("tickets/" + ticketId + "/refundedAt", System.currentTimeMillis());

                                                String showtimeKey = date + "_" + time;

                                                String baseSeatPath = "Bookings/" + movieTitle + "/" + showtimeKey
                                                        + "/cinemas/" + cinemaId + "/seats/";

                                                for (String seat : seats) {
                                                        updates.put(baseSeatPath + seat, "available");
                                                }

                                                db.updateChildren(updates)
                                                        .addOnSuccessListener(unused -> {
                                                                Toast.makeText(getContext(), "Hoàn tiền thành công!", Toast.LENGTH_SHORT).show();
                                                                loadTickets(); // reload list (REFUNDED sẽ bị lọc)
                                                        })
                                                        .addOnFailureListener(e -> {
                                                                Toast.makeText(getContext(), "Hoàn tiền OK nhưng trả ghế lỗi", Toast.LENGTH_SHORT).show();
                                                                loadTickets();
                                                        });
                                        }
                                });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { }
                });
        }

}
