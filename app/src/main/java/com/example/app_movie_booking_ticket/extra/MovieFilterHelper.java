package com.example.app_movie_booking_ticket.extra;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.app_movie_booking_ticket.model.Movie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Helper class để phân loại phim dựa trên:
 * 1. Suất chiếu trong 7 ngày tới -> Đang chiếu
 * 2. Suất chiếu sau 7 ngày -> Sắp chiếu
 * 3. Top 10% phim có đánh giá cao nhất -> Thịnh hành
 */
public class MovieFilterHelper {

    private static final String TAG = "MovieFilterHelper";

    // Callback interface
    public interface OnMoviesFilteredCallback {
        void onFiltered(List<Movie> nowShowing, List<Movie> upcoming, List<Movie> trending, List<Movie> allMovies);
    }

    // Callback interface cho đánh giá phim
    public interface OnMovieRatingsLoadedCallback {
        void onLoaded(Map<String, Double> ratings);
    }

    private final DatabaseReference bookingsRef;
    private final DatabaseReference reviewsRef;
    private final DatabaseReference moviesRef;

    public MovieFilterHelper() {
        bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");
        reviewsRef = FirebaseDatabase.getInstance().getReference("Reviews");
        moviesRef = FirebaseDatabase.getInstance().getReference("Movies");
    }

    /**
     * Load và phân loại tất cả phim
     */
    public void loadAndFilterMovies(OnMoviesFilteredCallback callback) {
        moviesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Movie> allMovies = new ArrayList<>();

                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    Movie movie = itemSnap.getValue(Movie.class);
                    if (movie != null) {
                        allMovies.add(movie);
                    }
                }

                // Load showtimes và ratings song song
                loadShowtimesAndRatings(allMovies, callback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading movies: " + error.getMessage());
            }
        });
    }

    private void loadShowtimesAndRatings(List<Movie> allMovies, OnMoviesFilteredCallback callback) {
        // Load showtimes cho mỗi phim
        Map<String, Date> earliestShowtimes = new HashMap<>();

        bookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm", Locale.getDefault());
                Date now = new Date();

                for (DataSnapshot movieSnap : snapshot.getChildren()) {
                    String movieId = movieSnap.getKey();
                    Date earliest = null;

                    for (DataSnapshot showtimeSnap : movieSnap.getChildren()) {
                        String showtimeKey = showtimeSnap.getKey();
                        try {
                            Date showtimeDate = sdf.parse(showtimeKey);
                            if (showtimeDate != null && showtimeDate.after(now)) {
                                if (earliest == null || showtimeDate.before(earliest)) {
                                    earliest = showtimeDate;
                                }
                            }
                        } catch (ParseException e) {
                            Log.w(TAG, "Error parsing showtime: " + showtimeKey);
                        }
                    }

                    if (earliest != null) {
                        earliestShowtimes.put(movieId, earliest);
                    }
                }

                // Load ratings
                loadMovieRatings(ratings -> {
                    // Phân loại phim
                    filterMovies(allMovies, earliestShowtimes, ratings, callback);
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading bookings: " + error.getMessage());
            }
        });
    }

    private void loadMovieRatings(OnMovieRatingsLoadedCallback callback) {
        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Double> ratings = new HashMap<>();

                for (DataSnapshot movieSnap : snapshot.getChildren()) {
                    String movieId = movieSnap.getKey();
                    double totalScore = 0;
                    int count = 0;

                    for (DataSnapshot reviewSnap : movieSnap.getChildren()) {
                        Integer rating = reviewSnap.child("rating").getValue(Integer.class);
                        if (rating != null) {
                            totalScore += rating;
                            count++;
                        }
                    }

                    if (count > 0) {
                        ratings.put(movieId, totalScore / count);
                    }
                }

                callback.onLoaded(ratings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading reviews: " + error.getMessage());
                callback.onLoaded(new HashMap<>());
            }
        });
    }

    private void filterMovies(List<Movie> allMovies,
            Map<String, Date> earliestShowtimes,
            Map<String, Double> ratings,
            OnMoviesFilteredCallback callback) {

        List<Movie> nowShowing = new ArrayList<>();
        List<Movie> upcoming = new ArrayList<>();
        List<Movie> trending = new ArrayList<>();

        // Tính ngày giới hạn:
        // - Phim đang chiếu: suất chiếu trong 7 ngày tới (từ hôm nay đến hết ngày thứ
        // 7)
        // - Phim sắp chiếu: suất chiếu từ ngày thứ 8 trở đi
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();

        // Ngày thứ 8 (bắt đầu của "sắp chiếu")
        cal.add(Calendar.DAY_OF_YEAR, 8);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date upcomingStartDate = cal.getTime(); // 00:00 ngày thứ 8

        // Phân loại phim theo suất chiếu
        for (Movie movie : allMovies) {
            String movieId = movie.getMovieID();
            Date earliestShowtime = earliestShowtimes.get(movieId);

            if (earliestShowtime != null) {
                // Nếu suất chiếu sớm nhất trước ngày thứ 8 -> Đang chiếu (trong 7 ngày)
                // Nếu suất chiếu sớm nhất từ ngày thứ 8 trở đi -> Sắp chiếu
                if (earliestShowtime.before(upcomingStartDate)) {
                    nowShowing.add(movie);
                } else {
                    upcoming.add(movie);
                }
            } else {
                // Không có suất chiếu -> dựa vào field isUpcoming
                if (movie.isUpcomingMovie()) {
                    upcoming.add(movie);
                } else {
                    nowShowing.add(movie);
                }
            }
        }

        // Tính Top 10% phim có đánh giá cao nhất
        trending.addAll(getTop10PercentByRating(allMovies, ratings));

        // Shuffle các danh sách để có sự đa dạng
        Collections.shuffle(nowShowing);
        Collections.shuffle(upcoming);

        Log.d(TAG, "Phim đang chiếu (trong 7 ngày): " + nowShowing.size());
        Log.d(TAG, "Phim sắp chiếu (từ ngày 8 trở đi): " + upcoming.size());
        Log.d(TAG, "Phim thịnh hành: " + trending.size());

        callback.onFiltered(nowShowing, upcoming, trending, allMovies);
    }

    private List<Movie> getTop10PercentByRating(List<Movie> allMovies, Map<String, Double> ratings) {
        // Tạo danh sách các phim có đánh giá
        List<Movie> moviesWithRatings = new ArrayList<>();

        for (Movie movie : allMovies) {
            if (ratings.containsKey(movie.getMovieID())) {
                moviesWithRatings.add(movie);
            }
        }

        // Sắp xếp theo điểm đánh giá giảm dần
        moviesWithRatings.sort((m1, m2) -> {
            Double r1 = ratings.getOrDefault(m1.getMovieID(), 0.0);
            Double r2 = ratings.getOrDefault(m2.getMovieID(), 0.0);
            return r2.compareTo(r1); // Giảm dần
        });

        // Lấy top 10%
        int top10PercentCount = Math.max(1, (int) Math.ceil(moviesWithRatings.size() * 0.1));

        // Nếu không có phim nào có đánh giá, lấy phim có Imdb cao nhất
        if (moviesWithRatings.isEmpty()) {
            List<Movie> allCopy = new ArrayList<>(allMovies);
            allCopy.sort((m1, m2) -> Double.compare(m2.getImdb(), m1.getImdb()));
            top10PercentCount = Math.max(1, (int) Math.ceil(allCopy.size() * 0.1));
            return new ArrayList<>(allCopy.subList(0, Math.min(top10PercentCount, allCopy.size())));
        }

        return new ArrayList<>(moviesWithRatings.subList(0, Math.min(top10PercentCount, moviesWithRatings.size())));
    }

    /**
     * Kiểm tra xem phim có suất chiếu trong X ngày tới không
     */
    public void hasShowtimesWithinDays(String movieId, int days, OnShowtimeCheckCallback callback) {
        bookingsRef.child(movieId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm", Locale.getDefault());
                Calendar cal = Calendar.getInstance();
                Date now = cal.getTime();
                cal.add(Calendar.DAY_OF_YEAR, days);
                Date targetDate = cal.getTime();

                for (DataSnapshot showtimeSnap : snapshot.getChildren()) {
                    try {
                        Date showtimeDate = sdf.parse(showtimeSnap.getKey());
                        if (showtimeDate != null && showtimeDate.after(now) && showtimeDate.before(targetDate)) {
                            callback.onResult(true);
                            return;
                        }
                    } catch (ParseException e) {
                        // Ignore
                    }
                }
                callback.onResult(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onResult(false);
            }
        });
    }

    public interface OnShowtimeCheckCallback {
        void onResult(boolean hasShowtimes);
    }
}
