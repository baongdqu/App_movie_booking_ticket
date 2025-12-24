package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.app_movie_booking_ticket.adapter.MovieImageAdapter;
import com.example.app_movie_booking_ticket.databinding.ParthomeMovieDetailsBinding;
import com.example.app_movie_booking_ticket.model.Movie;
import com.example.app_movie_booking_ticket.adapter.CastListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class parthome_movie_detail extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private CastListAdapter castAdapter;
    private MovieImageAdapter imageAdapter;
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
        mDatabase = FirebaseDatabase.getInstance().getReference();
        boolean isUpcoming = movie.isUpcomingMovie();
        Log.d("DEBUG_STATE", "Phim: " + movie.getTitle() + " | isUpcoming: " + isUpcoming);

        // Hiển thị đầy đủ cho tất cả phim (kể cả sắp chiếu)
        binding.button2.setVisibility(View.VISIBLE);
        binding.llStarRatingInfo.setVisibility(View.VISIBLE);
        binding.cvSummaryRatingInfo.setVisibility(View.VISIBLE);
        binding.llImagesSection.setVisibility(View.VISIBLE);
        loadMovieRatings(movie.getMovieID());

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

            // ÉP DÙNG CHROME → KHÔNG MỞ APP YOUTUBE
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
        binding.llToRatingLists.setOnClickListener(v -> {
            Intent ratingIntent = new Intent(parthome_movie_detail.this, parthome_RatingListActivity.class);
            ratingIntent.putExtra("movieID", movie.getMovieID());
            ratingIntent.putExtra("movieTitle", movie.getTitle());
            startActivity(ratingIntent);
        });

    }

    private void loadMovieRatings(String movieID) {
        // Lắng nghe dữ liệu tại node Reviews/[movieID]
        mDatabase.child("Reviews").child(movieID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalReviews = (int) snapshot.getChildrenCount();
                int[] starCounts = new int[6]; // index từ 1-5
                double totalScore = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    // Lấy giá trị rating từ ReviewModel hoặc trực tiếp từ Firebase
                    Integer r = ds.child("rating").getValue(Integer.class);
                    if (r != null && r >= 1 && r <= 5) {
                        starCounts[r]++;
                        totalScore += r;
                    }
                }

                if (totalReviews > 0) {
                    double average = totalScore / totalReviews;

                    // 1. Cập nhật điểm trung bình và tổng số
                    binding.tvAverageScore.setText(String.format("%.1f", average));
                    binding.tvRating.setText("(" + totalReviews + " Đánh giá)");
                    binding.tvAmountBought.setText(totalReviews + " đánh giá từ khán giả Việt đã mua vé");

                    // 2. Cập nhật Trạng thái cảm xúc dựa trên điểm số
                    updateSentiment(average);

                    // 3. Cập nhật các thanh ProgressBar (tính %)
                    binding.pb5StarRatingBar.setProgress((starCounts[5] * 100) / totalReviews);
                    binding.pb4StarRatingBar.setProgress((starCounts[4] * 100) / totalReviews);
                    binding.pb3StarRatingBar.setProgress((starCounts[3] * 100) / totalReviews);
                    binding.pb2StarRatingBar.setProgress((starCounts[2] * 100) / totalReviews);
                    binding.pb1StarRatingBar.setProgress((starCounts[1] * 100) / totalReviews);
                } else {
                    // Reset UI nếu chưa có đánh giá nào
                    binding.tvAverageScore.setText("0");
                    binding.tvRating.setText("(0 Đánh giá)");
                    binding.tvViewSentiment.setText("Chưa có đánh giá");
                    resetProgressBars();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DEBUG_RATING", "Lỗi Firebase: " + error.getMessage());
            }
        });
    }

    private void updateSentiment(double average) {
        if (average >= 4.5) {
            binding.tvViewSentiment.setText("Cực phẩm");
        } else if (average >= 3.5) {
            binding.tvViewSentiment.setText("Đáng xem");
        } else {
            binding.tvViewSentiment.setText("Kén người mê");
        }
    }

    private void resetProgressBars() {
        binding.pb5StarRatingBar.setProgress(0);
        binding.pb4StarRatingBar.setProgress(0);
        binding.pb3StarRatingBar.setProgress(0);
        binding.pb2StarRatingBar.setProgress(0);
        binding.pb1StarRatingBar.setProgress(0);
    }
}