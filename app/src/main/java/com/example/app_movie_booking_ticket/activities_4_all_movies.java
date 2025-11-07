package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_movie_booking_ticket.adapter.AllMoviesAdapter;
import com.example.app_movie_booking_ticket.model.extra_Movie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.ImageView;


import java.util.ArrayList;
import java.util.List;

public class activities_4_all_movies extends AppCompatActivity {

    private RecyclerView recyclerAllMovies;
    private AllMoviesAdapter adapter;
    private List<extra_Movie> allMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_movies);



        recyclerAllMovies = findViewById(R.id.recyclerAllMovies);
        recyclerAllMovies.setLayoutManager(new LinearLayoutManager(this)); // hiển thị 2 cột

        allMovies = new ArrayList<>();
        adapter = new AllMoviesAdapter(this, allMovies);
        recyclerAllMovies.setAdapter(adapter);

        // Lấy dữ liệu từ Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Items");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allMovies.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    extra_Movie movie = item.getValue(extra_Movie.class);
                    if (movie != null) {
                        allMovies.add(movie);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });


    }
}

