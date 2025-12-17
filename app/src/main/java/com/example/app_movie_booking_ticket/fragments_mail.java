package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_movie_booking_ticket.adapter.TicketAdapter;
import com.example.app_movie_booking_ticket.model.Movie;
import com.example.app_movie_booking_ticket.model.TicketSimple;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        adapter = new TicketAdapter(requireContext(), ticketList);
        rvTickets.setAdapter(adapter);

        loadMoviesThenTickets(); // 🔥 QUAN TRỌNG

        return view;
    }

    /**
     * 1️⃣ Load Items 1 lần duy nhất
     */
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

                        loadTickets(); // 🔥 sau khi có movie cache
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    /**
     * 2️⃣ Load vé của user
     */
    private void loadTickets() {
        db.child("tickets")
                .orderByChild("userId")
                .equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ticketList.clear();

                        for (DataSnapshot t : snapshot.getChildren()) {

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
                            for (DataSnapshot s : t.child("seats").getChildren()) {
                                seats.add(s.getValue(String.class));
                            }

                            ticketList.add(new TicketSimple(
                                    movie,
                                    movie.getTitle(),
                                    date + " • " + time,
                                    "Seats: " + String.join(", ", seats),
                                    posterUrl
                            ));
                        }

                        adapter.notifyDataSetChanged(); // 🔥 1 lần duy nhất
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}
