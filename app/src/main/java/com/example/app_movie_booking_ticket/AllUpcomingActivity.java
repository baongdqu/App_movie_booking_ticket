package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_movie_booking_ticket.adapter.AllMoviesAdapter;
import com.example.app_movie_booking_ticket.model.Movie;
import com.example.app_movie_booking_ticket.model.MovieTest; // ✅ dùng Movie thay vì extra_Movie
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllUpcomingActivity extends AppCompatActivity {

    private RecyclerView recyclerAllUpcoming;
    private AllMoviesAdapter adapter;
    private List<Movie> movieList;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_movies); // Dùng chung layout "All Movies"

        // Ánh xạ view
        recyclerAllUpcoming = findViewById(R.id.recyclerAllMovies);
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Khởi tạo danh sách và adapter
        movieList = new ArrayList<>();
        adapter = new AllMoviesAdapter(this, movieList);
        recyclerAllUpcoming.setLayoutManager(new LinearLayoutManager(this));
        recyclerAllUpcoming.setAdapter(adapter);

        // Lấy dữ liệu từ node "Upcomming"
        dbRef = FirebaseDatabase.getInstance().getReference("Upcomming");
        loadUpcomingMovies();
    }

    private void loadUpcomingMovies() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movieList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Movie movie = data.getValue(Movie.class);
                    if (movie != null) movieList.add(movie);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Có thể thêm Toast báo lỗi ở đây nếu cần
            }
        });
    }
}

