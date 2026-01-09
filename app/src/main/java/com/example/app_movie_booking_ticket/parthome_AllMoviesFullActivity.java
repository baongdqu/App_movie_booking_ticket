package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

/**
 * Activity hiển thị TẤT CẢ phim (bao gồm cả thịnh hành và sắp chiếu)
 */
public class parthome_AllMoviesFullActivity extends AppCompatActivity {

    private RecyclerView recyclerAllMovies;
    private AllMoviesAdapter adapter;
    private ImageView btnBack;
    private TextView tvTitle;
    private List<Movie> movieList;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra_themeutils.applySavedTheme(this);
        setContentView(R.layout.parthome_all_movies);

        recyclerAllMovies = findViewById(R.id.recyclerAllMovies);
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        movieList = new ArrayList<>();

        // Set title
        if (tvTitle != null) {
            tvTitle.setText(R.string.all_movies);
        }

        // Use GridLayoutManager with 2 columns
        recyclerAllMovies.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));
        adapter = new AllMoviesAdapter(this, movieList);
        recyclerAllMovies.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference("Movies");
        loadAllMoviesFromFirebase();

        btnBack.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            finish();
        });
    }

    private void loadAllMoviesFromFirebase() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movieList.clear();
                for (DataSnapshot movieSnap : snapshot.getChildren()) {
                    Movie movie = movieSnap.getValue(Movie.class);
                    // Add ALL movies (no filter)
                    if (movie != null) {
                        movieList.add(movie);
                    }
                }
                // Shuffle ngẫu nhiên
                java.util.Collections.shuffle(movieList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                extra_sound_manager.playError(parthome_AllMoviesFullActivity.this);
                Toast.makeText(parthome_AllMoviesFullActivity.this, getString(R.string.toast_load_data_error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
