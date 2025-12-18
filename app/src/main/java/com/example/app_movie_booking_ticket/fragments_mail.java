package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class fragments_mail extends Fragment {

    private RecyclerView rvTickets;
    private TicketAdapter adapter;
    private final List<TicketSimple> ticketList = new ArrayList<>();

    private final DatabaseReference db =
            FirebaseDatabase.getInstance().getReference();

    private final String currentUserId =
            FirebaseAuth.getInstance().getCurrentUser().getUid();

    // 🔥 CACHE MOVIE
    private final Map<String, Movie> movieMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.layouts_fragments_mail, container, false);

        rvTickets = view.findViewById(R.id.rvTickets);
        rvTickets.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TicketAdapter(
                requireContext(),
                ticketList,
                this::refundTicket   // 🔥 CALLBACK
        );

        rvTickets.setAdapter(adapter);

        loadMoviesThenTickets();
        return view;
    }

    // ---------- LOAD MOVIES ----------
    private void loadMoviesThenTickets() {
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
                                        movie
                                );
                            }
                        }

                        loadTickets();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    // ---------- LOAD TICKETS (PAID + NULL) ----------
    private void loadTickets() {
        db.child("tickets")
                .orderByChild("userId")
                .equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ticketList.clear();

                        for (DataSnapshot t : snapshot.getChildren()) {

                            // 🔥 STATUS FILTER
                            String status =
                                    t.child("status").getValue(String.class);
                            if (status != null && !"PAID".equals(status)) {
                                continue; // REFUNDED / CANCELLED
                            }

                            String movieTitle =
                                    t.child("movieTitle").getValue(String.class);
                            if (movieTitle == null) continue;

                            Movie movie =
                                    movieMap.get(movieTitle.toLowerCase());
                            if (movie == null) continue;

                            String posterUrl =
                                    t.child("posterUrl").getValue(String.class);
                            String date =
                                    t.child("date").getValue(String.class);
                            String time =
                                    t.child("time").getValue(String.class);

                            List<String> seats = new ArrayList<>();
                            if (t.child("seats").exists()) {
                                for (DataSnapshot s : t.child("seats").getChildren()) {
                                    String seat = s.getValue(String.class);
                                    if (seat != null) seats.add(seat);
                                }
                            }

                            ticketList.add(new TicketSimple(
                                    t.getKey(),                     // 🔥 ticketId
                                    movie,
                                    movie.getTitle(),
                                    date + " • " + time,
                                    "Seats: " + String.join(", ", seats),
                                    posterUrl
                            ));
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    // ---------- REFUND ----------
    private void refundTicket(TicketSimple ticket) {

        String ticketId = ticket.getTicketId();

        DatabaseReference ticketRef =
                db.child("tickets").child(ticketId);

        ticketRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) return;

                // 🔥 STATUS NULL = PAID
                String status =
                        snapshot.child("status").getValue(String.class);
                if (status == null) status = "PAID";

                if (!"PAID".equals(status)) {
                    Toast.makeText(getContext(),
                            "Vé đã được hoàn",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Long totalPrice =
                        snapshot.child("totalPrice").getValue(Long.class);
                if (totalPrice == null) return;

                DatabaseReference balanceRef =
                        db.child("users")
                                .child(currentUserId)
                                .child("balance");

                // 🔥 BALANCE NULL = 0
                balanceRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(
                            @NonNull MutableData currentData) {

                        Long current =
                                currentData.getValue(Long.class);
                        if (current == null) current = 0L;

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

                            Toast.makeText(getContext(),
                                    "Hoàn tiền thành công",
                                    Toast.LENGTH_SHORT).show();

                            loadTickets(); // reload list
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}

