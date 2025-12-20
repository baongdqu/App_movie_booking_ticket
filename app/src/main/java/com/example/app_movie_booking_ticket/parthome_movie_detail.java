package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.app_movie_booking_ticket.adapter.MovieImageAdapter;
import com.example.app_movie_booking_ticket.databinding.ParthomeMovieDetailsBinding;
import com.example.app_movie_booking_ticket.model.Movie;
import com.example.app_movie_booking_ticket.adapter.CastListAdapter;

import java.util.ArrayList;
import java.util.List;

public class parthome_movie_detail extends AppCompatActivity {

    private CastListAdapter adapter;
    private MovieImageAdapter ImagesAdapter;
    private ParthomeMovieDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ParthomeMovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        Movie movie = (Movie) intent.getSerializableExtra("movie");
        if (movie == null)
            return;

        // Load movie data into layout
        binding.textTitle.setText(movie.getTitle());
        binding.textGenre.setText(String.join(", ", movie.getGenre()));
        binding.textYearDuration.setText(movie.getYear() + " • " + movie.getTime());
        binding.textDescription.setText(movie.getDescription());

        Glide.with(this)
                .load(movie.getPoster())
                .into(binding.imagePoster);

        List<String> imagesList = movie.getPicture();
        if (imagesList == null)
            imagesList = new ArrayList<>();

        List<Movie.Cast> castList = movie.getCasts();
        adapter = new CastListAdapter(this, castList);
        binding.recyclerCast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerCast.setAdapter(adapter);

        ImagesAdapter = new MovieImageAdapter(this, imagesList);
        binding.recyclerImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerImages.setAdapter(ImagesAdapter);

        // Back button
        binding.btnBack.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            finish();
        });

        // Mua vé
        binding.button2.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            Intent intent2 = new Intent(parthome_movie_detail.this, parthome_SeatSelectionActivity.class);
            intent2.putExtra("movieID", movie.getMovieID());
            intent2.putExtra("posterUrl", movie.getPoster());
            intent2.putExtra("movieTitle", movie.getTitle());
            intent2.putExtra("price", movie.getPrice());
            startActivity(intent2);
        });
    }
}