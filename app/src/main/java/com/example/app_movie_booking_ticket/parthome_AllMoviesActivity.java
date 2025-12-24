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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class parthome_AllMoviesActivity extends AppCompatActivity {

    private RecyclerView recyclerAllMovies;
    private AllMoviesAdapter adapter;
    private ImageView btnBack;
    private List<Movie> movieList;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parthome_all_movies);

        recyclerAllMovies = findViewById(R.id.recyclerAllMovies);
        btnBack = findViewById(R.id.btnBack);
        movieList = new ArrayList<>();

        // Use GridLayoutManager with 2 columns
        recyclerAllMovies.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));
        adapter = new AllMoviesAdapter(this, movieList);
        recyclerAllMovies.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference("Movies");
        loadMoviesFromFirebase();

        btnBack.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            finish();
        });
    }

    private void loadMoviesFromFirebase() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movieList.clear();
                for (DataSnapshot movieSnap : snapshot.getChildren()) {
                    Movie movie = movieSnap.getValue(Movie.class);
                    // Only add trending movies (Phim thịnh hành)
                    if (movie != null && movie.isTrendingMovie()) {
                        movieList.add(movie);
                    }
                }
                // Shuffle ngẫu nhiên
                java.util.Collections.shuffle(movieList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                extra_sound_manager.playError(parthome_AllMoviesActivity.this);
                Toast.makeText(parthome_AllMoviesActivity.this, getString(R.string.toast_load_data_error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}