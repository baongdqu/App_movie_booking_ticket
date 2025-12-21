package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Fragment Hộp thư (Mail) / Vé của tôi
 * Hiển thị danh sách vé đã đặt của người dùng.
 * Hỗ trợ chức năng xem chi tiết vé và hoàn vé (Refund).
 */
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

        /**
         * Tạo View cho fragment.
         * Khởi tạo RecyclerView và Adapter.
         */
        @Nullable
        @Override
        public View onCreateView(
                        @NonNull LayoutInflater inflater,
                        @Nullable ViewGroup container,
                        @Nullable Bundle savedInstanceState) {

                View view = inflater.inflate(
                                R.layout.layouts_fragments_mail, container, false);

                // Initialize views
                rvTickets = view.findViewById(R.id.rvTickets);
                emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
                progressBar = view.findViewById(R.id.progressBar);
                tvTicketCount = view.findViewById(R.id.tvTicketCount);

                rvTickets.setLayoutManager(new LinearLayoutManager(getContext()));

                adapter = new TicketAdapter(
                                requireContext(),
                                ticketList,
                                this::refundTicket);

                rvTickets.setAdapter(adapter);

                // Check if user is logged in
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        loadMoviesThenTickets();
                } else {
                        showEmptyState();
                }

                return view;
        }

        // ================= SHOW/HIDE STATES =================
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

                // Update ticket count badge
                int count = ticketList.size();
                tvTicketCount.setText(count + " vé");
                tvTicketCount.setVisibility(View.VISIBLE);
        }

        // ================= LOAD MOVIES =================
        private void loadMoviesThenTickets() {
                showLoading();

                db.child("Items")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                movieMap.clear();

                                                for (DataSnapshot item : snapshot.getChildren()) {
                                                        Movie movie = item.getValue(Movie.class);
                                                        if (movie != null && movie.getTitle() != null) {
                                                                movieMap.put(
                                                                                movie.getTitle().toLowerCase(),
                                                                                movie);
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

        // ================= LOAD TICKETS =================
        private void loadTickets() {
                db.child("tickets")
                                .orderByChild("userId")
                                .equalTo(currentUserId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                ticketList.clear();

                                                for (DataSnapshot t : snapshot.getChildren()) {

                                                        String status = t.child("status").getValue(String.class);
                                                        if (status != null && !"PAID".equals(status))
                                                                continue;

                                                        String movieTitle = t.child("movieTitle")
                                                                        .getValue(String.class);
                                                        if (movieTitle == null)
                                                                continue;

                                                        Movie movie = movieMap.get(movieTitle.toLowerCase());
                                                        if (movie == null)
                                                                continue;

                                                        String posterUrl = t.child("posterUrl").getValue(String.class);
                                                        String date = t.child("date").getValue(String.class);
                                                        String time = t.child("time").getValue(String.class);

                                                        List<String> seats = new ArrayList<>();
                                                        if (t.child("seats").exists()) {
                                                                for (DataSnapshot s : t.child("seats").getChildren()) {
                                                                        String seat = s.getValue(String.class);
                                                                        if (seat != null)
                                                                                seats.add(seat);
                                                                }
                                                        }

                                                        ticketList.add(new TicketSimple(
                                                                        t.getKey(),
                                                                        movie,
                                                                        movie.getTitle(),
                                                                        date + " • " + time,
                                                                        "Ghế: " + String.join(", ", seats),
                                                                        posterUrl));
                                                }

                                                adapter.notifyDataSetChanged();

                                                // Show appropriate state
                                                if (ticketList.isEmpty()) {
                                                        showEmptyState();
                                                } else {
                                                        showTickets();
                                                }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                                showEmptyState();
                                        }
                                });
        }

        // ================= REFUND =================
        private void refundTicket(TicketSimple ticket) {

                String ticketId = ticket.getTicketId();
                DatabaseReference ticketRef = db.child("tickets").child(ticketId);

                ticketRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshotTicket) {

                                if (!snapshotTicket.exists())
                                        return;

                                String status = snapshotTicket.child("status").getValue(String.class);
                                if (status == null)
                                        status = "PAID";

                                if (!"PAID".equals(status)) {
                                        Toast.makeText(getContext(),
                                                        "Vé đã được hoàn",
                                                        Toast.LENGTH_SHORT).show();
                                        return;
                                }

                                Long totalPrice = snapshotTicket.child("totalPrice").getValue(Long.class);
                                if (totalPrice == null)
                                        return;

                                String movieTitle = snapshotTicket.child("movieTitle").getValue(String.class);

                                DatabaseReference balanceRef = db.child("users")
                                                .child(currentUserId)
                                                .child("balance");

                                balanceRef.runTransaction(new Transaction.Handler() {
                                        @NonNull
                                        @Override
                                        public Transaction.Result doTransaction(
                                                        @NonNull MutableData currentData) {

                                                Long current = currentData.getValue(Long.class);
                                                if (current == null)
                                                        current = 0L;

                                                currentData.setValue(current + totalPrice);
                                                return Transaction.success(currentData);
                                        }

                                        @Override
                                        public void onComplete(DatabaseError error,
                                                        boolean committed,
                                                        DataSnapshot snapshot) {

                                                if (committed) {
                                                        ticketRef.child("status")
                                                                        .setValue("REFUNDED");

                                                        createRefundNotification(
                                                                        ticketId,
                                                                        movieTitle,
                                                                        totalPrice);

                                                        Toast.makeText(getContext(),
                                                                        "Hoàn tiền thành công!",
                                                                        Toast.LENGTH_SHORT).show();

                                                        loadTickets();
                                                }
                                        }
                                });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                });
        }

        // ================= NOTIFICATION =================
        private void createRefundNotification(String ticketId,
                        String movieTitle,
                        long amount) {

                String uid = FirebaseAuth.getInstance().getUid();
                if (uid == null)
                        return;

                DatabaseReference notiRef = FirebaseDatabase.getInstance()
                                .getReference("notifications")
                                .child(uid);

                String notiId = notiRef.push().getKey();
                if (notiId == null)
                        return;

                Map<String, Object> data = new HashMap<>();
                data.put("title", "Hoàn tiền thành công");
                data.put("message",
                                "Bạn đã được hoàn " + String.format("%,d", amount) + "đ cho vé " + movieTitle);
                data.put("type", "REFUND");
                data.put("ticketId", ticketId);
                data.put("timestamp", System.currentTimeMillis());
                data.put("read", false);

                notiRef.child(notiId).setValue(data);
        }
}
