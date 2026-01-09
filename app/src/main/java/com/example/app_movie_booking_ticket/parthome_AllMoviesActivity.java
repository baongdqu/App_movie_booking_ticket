package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_movie_booking_ticket.adapter.AllMoviesAdapter;
import com.example.app_movie_booking_ticket.extra.MovieCacheManager;
import com.example.app_movie_booking_ticket.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity hiển thị tất cả phim THỊNH HÀNH (Top rated)
 */
public class parthome_AllMoviesActivity extends extra_manager_language {

    private RecyclerView recyclerAllMovies;
    private AllMoviesAdapter adapter;
    private ImageView btnBack;
    private List<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra_themeutils.applySavedTheme(this);
        setContentView(R.layout.parthome_all_movies);

        recyclerAllMovies = findViewById(R.id.recyclerAllMovies);
        btnBack = findViewById(R.id.btnBack);
        movieList = new ArrayList<>();

        // Use GridLayoutManager with 2 columns
        recyclerAllMovies.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));
        adapter = new AllMoviesAdapter(this, movieList);
        recyclerAllMovies.setAdapter(adapter);

        loadTrendingMovies();

        btnBack.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            finish();
        });
    }

    private void loadTrendingMovies() {
        // Sử dụng MovieCacheManager để lấy danh sách phim thịnh hành (đã được tính toán
        // dựa trên ratings)
        MovieCacheManager.getInstance().getFilteredMovies((nowShowing, upcoming, trending, allMovies) -> {
            movieList.clear();
            movieList.addAll(trending);

            // Nếu trending ít quá, thêm các phim đang chiếu có đánh giá cao
            if (movieList.size() < 5) {
                for (Movie movie : nowShowing) {
                    if (!movieList.contains(movie)) {
                        movieList.add(movie);
                    }
                    if (movieList.size() >= 10)
                        break;
                }
            }

            adapter.notifyDataSetChanged();

            if (movieList.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_no_trending_movies), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
