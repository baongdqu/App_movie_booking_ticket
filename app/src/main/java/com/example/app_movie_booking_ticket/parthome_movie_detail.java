package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.app_movie_booking_ticket.adapter.CastListAdapter;
import com.example.app_movie_booking_ticket.adapter.MovieImageAdapter;
import com.example.app_movie_booking_ticket.databinding.ParthomeMovieDetailsBinding;
import com.example.app_movie_booking_ticket.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity Chi tiết phim
 * Hiển thị đầy đủ thông tin về bộ phim: Poster, Trailer, Diễn viên, Nội dung.
 * Cung cấp chức năng xem trailer và chuyển sang màn hình đặt vé.
 */
public class parthome_movie_detail extends AppCompatActivity {

    private CastListAdapter castAdapter;
    private MovieImageAdapter imageAdapter;
    private ParthomeMovieDetailsBinding binding;

    /**
     * Khởi tạo giao diện chi tiết phim.
     * Nhận dữ liệu Movie từ Intent và hiển thị lên các view.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ParthomeMovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ================= GET DATA =================
        Intent intent = getIntent();
        Movie movie = (Movie) intent.getSerializableExtra("movie");
        if (movie == null)
            return;

        // ================= SET DATA =================
        binding.textTitle.setText(movie.getTitle());
        binding.textGenre.setText(String.join(", ", movie.getGenre()));
        binding.textYearDuration.setText(movie.getYear() + " • " + movie.getTime());
        binding.textDescription.setText(movie.getDescription());

        Glide.with(this)
                .load(movie.getPoster())
                .into(binding.imagePoster);

        // ================= CAST =================
        List<Movie.Cast> castList = movie.getCasts();
        castAdapter = new CastListAdapter(this, castList);
        binding.recyclerCast.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerCast.setAdapter(castAdapter);

        // ================= IMAGES =================
        List<String> imagesList = movie.getPicture();
        if (imagesList == null)
            imagesList = new ArrayList<>();

        imageAdapter = new MovieImageAdapter(this, imagesList);
        binding.recyclerImages.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerImages.setAdapter(imageAdapter);

        // ================= BACK =================
        binding.btnBack.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            finish();
        });

        // ================= TRAILER (CHROME CUSTOM TABS) =================
        binding.buttonTrailer.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);

            String trailerUrl = movie.getTrailer();
            if (trailerUrl == null || trailerUrl.trim().isEmpty()) {
                Toast.makeText(this, "Trailer chưa có sẵn", Toast.LENGTH_SHORT).show();
                return;
            }

            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .build();

            // 🔴 ÉP DÙNG CHROME → KHÔNG MỞ APP YOUTUBE
            customTabsIntent.intent.setPackage("com.android.chrome");

            try {
                customTabsIntent.launchUrl(this, Uri.parse(trailerUrl));
            } catch (Exception e) {
                // Fallback nếu máy không có Chrome
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl)));
            }
        });

        // ================= BUY TICKET =================
        binding.button2.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);

            Intent buyIntent = new Intent(
                    parthome_movie_detail.this,
                    parthome_SeatSelectionActivity.class);
            buyIntent.putExtra("movieID", movie.getMovieID());
            buyIntent.putExtra("posterUrl", movie.getPoster());
            buyIntent.putExtra("movieTitle", movie.getTitle());
            buyIntent.putExtra("price", movie.getPrice());
            startActivity(buyIntent);
        });
    }
}
