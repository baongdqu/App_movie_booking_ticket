package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_movie_booking_ticket.adapter.MovieImageAdapter;
import com.example.app_movie_booking_ticket.databinding.Activity4MovieDetailsBinding;
import com.example.app_movie_booking_ticket.model.Movie;

import com.example.app_movie_booking_ticket.adapter.CastListAdapter;

import java.util.ArrayList;
import java.util.List;

public class activities_4_movie_detail extends AppCompatActivity {

    private RecyclerView recyclerCastView;
    private RecyclerView recyclerImageView;
    private CastListAdapter adapter;
    private MovieImageAdapter ImagesAdapter;
    private Activity4MovieDetailsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = Activity4MovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recyclerCastView = binding.recyclerCast;
        recyclerImageView = binding.recyclerImages;
        Intent intent = getIntent();
        Movie movie = (Movie) intent.getSerializableExtra("movie");
        if (movie == null) return;

        // Load movie data into layout
        binding.textTitle.setText(movie.getTitle());
        binding.textGenre.setText(String.join(", ", movie.getGenre()));
        binding.textYearDuration.setText(movie.getYear() + " • " + movie.getTime());
        binding.textDescription.setText(movie.getDescription());


        Glide.with(this)
                .load(movie.getPoster())
                .into(binding.imagePoster);

        List<String> imagesList = movie.getPicture();
        if (imagesList == null) imagesList = new ArrayList<>();


        List<Movie.Cast> castList = movie.getCasts();
        adapter = new CastListAdapter(this, castList);
        binding.recyclerCast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerCast.setAdapter(adapter);

        ImagesAdapter = new MovieImageAdapter(this,imagesList);
        binding.recyclerImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerImages.setAdapter(ImagesAdapter);

        // Back button
        binding.btnBack.setOnClickListener(v -> finish());
    }
}