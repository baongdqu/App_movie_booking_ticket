package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_movie_booking_ticket.adapter.AllMoviesAdapter;
import com.example.app_movie_booking_ticket.model.Movie;
import com.example.app_movie_booking_ticket.model.MovieTest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class AllMoviesActivity extends AppCompatActivity {

    private RecyclerView recyclerAllMovies;
    private AllMoviesAdapter adapter;
    private ImageView btnBack;
    private List<Movie> movieList;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_movies);

        recyclerAllMovies = findViewById(R.id.recyclerAllMovies);
        btnBack = findViewById(R.id.btnBack);
        movieList = new ArrayList<>();

        recyclerAllMovies.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AllMoviesAdapter(this, movieList);
        recyclerAllMovies.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference("Items");


        loadMoviesFromFirebase();

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadMoviesFromFirebase() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movieList.clear();
                for (DataSnapshot movieSnap : snapshot.getChildren()) {
                    Movie movie = movieSnap.getValue(Movie.class);
                    if (movie != null) {
                        movieList.add(movie);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllMoviesActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


