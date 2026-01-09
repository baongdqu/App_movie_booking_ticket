package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
 * Activity hiển thị tất cả phim đang chiếu (Now Showing Movies).
 * Phim đang chiếu là những phim có isUpcoming = false.
 */
public class parthome_AllNowShowingActivity extends AppCompatActivity {

    private RecyclerView recyclerNowShowingMovies;
    private AllMoviesAdapter adapter;
    private ImageView btnBack;
    private List<Movie> movieList;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra_themeutils.applySavedTheme(this);
        setContentView(R.layout.parthome_all_now_showing);

        recyclerNowShowingMovies = findViewById(R.id.recyclerNowShowingMovies);
        btnBack = findViewById(R.id.btnBack);
        movieList = new ArrayList<>();

        // Sử dụng GridLayoutManager với 2 cột
        recyclerNowShowingMovies.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new AllMoviesAdapter(this, movieList);
        recyclerNowShowingMovies.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference("Movies");
        loadNowShowingMoviesFromFirebase();

        btnBack.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            finish();
        });
    }

    /**
     * Load danh sách phim đang chiếu từ Firebase.
     * Phim đang chiếu: isUpcoming = false (bao gồm cả null và false)
     */
    private void loadNowShowingMoviesFromFirebase() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movieList.clear();
                for (DataSnapshot movieSnap : snapshot.getChildren()) {
                    Movie movie = movieSnap.getValue(Movie.class);
                    // Phim đang chiếu: isUpcoming = false hoặc null
                    if (movie != null && !movie.isUpcomingMovie()) {
                        movieList.add(movie);
                    }
                }
                // Shuffle ngẫu nhiên để đa dạng hiển thị
                java.util.Collections.shuffle(movieList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                extra_sound_manager.playError(parthome_AllNowShowingActivity.this);
                Toast.makeText(parthome_AllNowShowingActivity.this, getString(R.string.toast_load_data_error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
