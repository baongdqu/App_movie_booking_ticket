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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Singleton class để cache dữ liệu phim.
 * Giảm thời gian tải bằng cách:
 * 1. Cache dữ liệu phim sau lần đầu
 * 2. Tải trước khi cần
 * 3. Cập nhật realtime khi có thay đổi
 */
public class MovieCacheManager {
    private static final String TAG = "MovieCacheManager";

    // Singleton instance
    private static MovieCacheManager instance;

    // Cached data
    private List<Movie> cachedAllMovies = new ArrayList<>();
    private List<Movie> cachedNowShowing = new ArrayList<>();
    private List<Movie> cachedUpcoming = new ArrayList<>();
    private List<Movie> cachedTrending = new ArrayList<>();
    private List<Movie> cachedExpired = new ArrayList<>(); // Phim đã chiếu (không còn suất chiếu trong tương lai)
    private Map<String, Double> cachedRatings = new HashMap<>();
    private Map<String, Date> cachedEarliestShowtimes = new HashMap<>();
    private Set<String> expiredMovieIds = new HashSet<>(); // Set các movieID đã chiếu

    // State flags
    private boolean isLoading = false;
    private boolean isDataLoaded = false;
    private long lastLoadTime = 0;
    private static final long CACHE_EXPIRY_MS = 5 * 60 * 1000; // 5 phút

    // Listeners đang chờ data
    private List<OnDataReadyCallback> pendingCallbacks = new ArrayList<>();

    // Firebase references
    private final DatabaseReference moviesRef;
    private final DatabaseReference bookingsRef;
    private final DatabaseReference reviewsRef;

    // Listeners để lắng nghe thay đổi realtime
    private ValueEventListener moviesListener;
    private ValueEventListener bookingsListener;
    private ValueEventListener reviewsListener;

    public interface OnDataReadyCallback {
        void onDataReady(List<Movie> nowShowing, List<Movie> upcoming, List<Movie> trending, List<Movie> allMovies);
    }

    private MovieCacheManager() {
        moviesRef = FirebaseDatabase.getInstance().getReference("Movies");
        bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");
        reviewsRef = FirebaseDatabase.getInstance().getReference("Reviews");
    }

    public static synchronized MovieCacheManager getInstance() {
        if (instance == null) {
            instance = new MovieCacheManager();
        }
        return instance;
    }

    /**
     * Bắt đầu preload dữ liệu (gọi sớm, ví dụ trong Application hoặc SplashScreen)
     */
    public void preloadData() {
        if (!isLoading && !isDataLoaded) {
            Log.d(TAG, "Starting preload...");
            loadAllData(null);
        }
    }

    /**
     * Lấy dữ liệu đã cache hoặc tải mới nếu cần
     */
    public void getFilteredMovies(OnDataReadyCallback callback) {
        // Nếu cache còn hạn, trả về ngay
        if (isDataLoaded && !isCacheExpired()) {
            Log.d(TAG, "Returning cached data");
            callback.onDataReady(
                    new ArrayList<>(cachedNowShowing),
                    new ArrayList<>(cachedUpcoming),
                    new ArrayList<>(cachedTrending),
                    new ArrayList<>(cachedAllMovies));
            return;
        }

        // Nếu đang loading, thêm callback vào queue
        if (isLoading) {
            Log.d(TAG, "Loading in progress, queueing callback");
            pendingCallbacks.add(callback);
            return;
        }

        // Bắt đầu load mới
        loadAllData(callback);
    }

    /**
     * Force refresh cache
     */
    public void refreshCache(OnDataReadyCallback callback) {
        isDataLoaded = false;
        loadAllData(callback);
    }

    private boolean isCacheExpired() {
        return System.currentTimeMillis() - lastLoadTime > CACHE_EXPIRY_MS;
    }

    private void loadAllData(OnDataReadyCallback callback) {
        if (callback != null) {
            pendingCallbacks.add(callback);
        }

        isLoading = true;
        Log.d(TAG, "Loading all data...");

        // Load movies first
        moviesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Movie> allMovies = new ArrayList<>();
                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    Movie movie = itemSnap.getValue(Movie.class);
                    if (movie != null) {
                        allMovies.add(movie);
                    }
                }

                cachedAllMovies.clear();
                cachedAllMovies.addAll(allMovies);
                Log.d(TAG, "Loaded " + allMovies.size() + " movies");

                // Load bookings and ratings in parallel
                loadBookingsAndRatings();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading movies: " + error.getMessage());
                notifyError();
            }
        });
    }

    private void loadBookingsAndRatings() {
        final boolean[] bookingsLoaded = { false };
        final boolean[] ratingsLoaded = { false };

        // Load bookings
        bookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                parseBookings(snapshot);
                bookingsLoaded[0] = true;
                if (ratingsLoaded[0]) {
                    finishLoading();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading bookings: " + error.getMessage());
                bookingsLoaded[0] = true;
                if (ratingsLoaded[0]) {
                    finishLoading();
                }
            }
        });

        // Load ratings
        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                parseRatings(snapshot);
                ratingsLoaded[0] = true;
                if (bookingsLoaded[0]) {
                    finishLoading();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading ratings: " + error.getMessage());
                ratingsLoaded[0] = true;
                if (bookingsLoaded[0]) {
                    finishLoading();
                }
            }
        });
    }

    private void parseBookings(DataSnapshot snapshot) {
        cachedEarliestShowtimes.clear();
        expiredMovieIds.clear(); // Clear trước khi parse lại

        // Set để track các phim có suất chiếu trong quá khứ nhưng không còn suất chiếu
        // tương lai
        Set<String> moviesWithPastShowtimesOnly = new HashSet<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm", Locale.getDefault());
        Date now = new Date();

        for (DataSnapshot movieSnap : snapshot.getChildren()) {
            String movieId = movieSnap.getKey();
            Date earliestFuture = null;
            boolean hasPastShowtimes = false;
            boolean hasFutureShowtimes = false;

            for (DataSnapshot showtimeSnap : movieSnap.getChildren()) {
                String showtimeKey = showtimeSnap.getKey();
                try {
                    Date showtimeDate = sdf.parse(showtimeKey);
                    if (showtimeDate != null) {
                        if (showtimeDate.after(now)) {
                            // Suất chiếu trong tương lai
                            hasFutureShowtimes = true;
                            if (earliestFuture == null || showtimeDate.before(earliestFuture)) {
                                earliestFuture = showtimeDate;
                            }
                        } else {
                            // Suất chiếu trong quá khứ
                            hasPastShowtimes = true;
                        }
                    }
                } catch (ParseException e) {
                    // Ignore invalid format
                }
            }

            if (earliestFuture != null) {
                // Có suất chiếu trong tương lai
                cachedEarliestShowtimes.put(movieId, earliestFuture);
            } else if (hasPastShowtimes && !hasFutureShowtimes) {
                // Có suất chiếu quá khứ nhưng KHÔNG CÒN suất chiếu tương lai -> ĐÃ CHIẾU
                moviesWithPastShowtimesOnly.add(movieId);
                expiredMovieIds.add(movieId);
                Log.d(TAG, "Phim đã chiếu (chỉ có suất quá khứ): " + movieId);
            }
        }

        Log.d(TAG, "Phim có suất chiếu tương lai: " + cachedEarliestShowtimes.size());
        Log.d(TAG, "Phim đã chiếu (quá khứ): " + moviesWithPastShowtimesOnly.size());
    }

    private void parseRatings(DataSnapshot snapshot) {
        cachedRatings.clear();

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
                cachedRatings.put(movieId, totalScore / count);
            }
        }
        Log.d(TAG, "Parsed ratings for " + cachedRatings.size() + " movies");
    }

    private void finishLoading() {
        // Phân loại phim
        filterMovies();

        // Cập nhật state
        isLoading = false;
        isDataLoaded = true;
        lastLoadTime = System.currentTimeMillis();

        Log.d(TAG, "Data ready! NowShowing=" + cachedNowShowing.size() +
                ", Upcoming=" + cachedUpcoming.size() +
                ", Trending=" + cachedTrending.size());

        // Notify all pending callbacks
        notifyCallbacks();
    }

    private void filterMovies() {
        cachedNowShowing.clear();
        cachedUpcoming.clear();
        cachedTrending.clear();
        cachedExpired.clear();
        // KHÔNG xóa expiredMovieIds vì đã được populate trong parseBookings()

        // Tính ngày giới hạn:
        // - Phim đã chiếu: có suất chiếu trong QUÁ KHỨ nhưng KHÔNG CÒN suất trong tương
        // lai
        // - Phim đang chiếu: suất chiếu từ HÔM NAY đến hết ngày thứ 7
        // - Phim sắp chiếu: suất chiếu từ ngày thứ 8 trở đi
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();

        Log.d(TAG, "=== PHÂN LOẠI PHIM ===");
        Log.d(TAG, "Thời điểm hiện tại: " + now.toString());

        // Ngày thứ 8 (bắt đầu của "sắp chiếu")
        cal.add(Calendar.DAY_OF_YEAR, 8);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date nowShowingStartDate = cal.getTime(); // 00:00 ngày thứ 8

        Log.d(TAG, "Ngày bắt đầu đang chiếu (ngày 8): " + nowShowingStartDate.toString());
        Log.d(TAG, "Phạm vi SẮP CHIẾU: " + now.toString() + " đến trước " + nowShowingStartDate.toString());
        Log.d(TAG, "Phạm vi ĐANG CHIẾU: từ " + nowShowingStartDate.toString() + " trở đi");

        // Phân loại theo suất chiếu
        for (Movie movie : cachedAllMovies) {
            String movieId = movie.getMovieID();

            // Kiểm tra xem phim có phải đã chiếu không (đã được đánh dấu trong
            // parseBookings)
            if (expiredMovieIds.contains(movieId)) {
                // Phim đã chiếu - thêm vào expired và bỏ qua các danh sách khác
                cachedExpired.add(movie);
                Log.d(TAG, "[ĐÃ CHIẾU] " + movie.getTitle() + " (có suất quá khứ, không còn suất tương lai)");
                continue;
            }

            Date earliestShowtime = cachedEarliestShowtimes.get(movieId);

            if (earliestShowtime != null) {
                // Có suất chiếu trong tương lai
                if (earliestShowtime.before(nowShowingStartDate)) {
                    // Suất chiếu trong 7 ngày -> SẮP CHIẾU (phim sắp ra mắt)
                    cachedUpcoming.add(movie);
                    Log.d(TAG, "[SẮP CHIẾU] " + movie.getTitle() + " - Suất sớm nhất: " + earliestShowtime.toString());
                } else {
                    // Suất chiếu từ ngày 8 trở đi -> ĐANG CHIẾU (phim đã công chiếu)
                    cachedNowShowing.add(movie);
                    Log.d(TAG, "[ĐANG CHIẾU] " + movie.getTitle() + " - Suất sớm nhất: " + earliestShowtime.toString());
                }
            } else {
                // Không có suất chiếu trong database -> dựa vào field isUpcoming
                if (movie.isUpcomingMovie()) {
                    cachedUpcoming.add(movie);
                    Log.d(TAG, "[SẮP CHIẾU - fallback] " + movie.getTitle() + " (isUpcoming=true)");
                } else {
                    cachedNowShowing.add(movie);
                    Log.d(TAG, "[ĐANG CHIẾU - fallback] " + movie.getTitle() + " (isUpcoming=false)");
                }
            }
        }

        // Top 10% trending (không bao gồm phim đã chiếu - đã được xử lý trong
        // getTop10PercentByRating)
        cachedTrending.addAll(getTop10PercentByRating());

        // Shuffle để đa dạng
        Collections.shuffle(cachedNowShowing);
        Collections.shuffle(cachedUpcoming);
        Collections.shuffle(cachedExpired);

        Log.d(TAG, "=== KẾT QUẢ ===");
        Log.d(TAG, "Phim đang chiếu: " + cachedNowShowing.size());
        Log.d(TAG, "Phim sắp chiếu: " + cachedUpcoming.size());
        Log.d(TAG, "Phim đã chiếu: " + cachedExpired.size());
        Log.d(TAG, "Phim thịnh hành: " + cachedTrending.size());
    }

    private List<Movie> getTop10PercentByRating() {
        List<Movie> moviesWithRatings = new ArrayList<>();

        // Chỉ lấy phim có đánh giá VÀ chưa hết hạn (còn đang chiếu hoặc sắp chiếu)
        for (Movie movie : cachedAllMovies) {
            String movieId = movie.getMovieID();
            // Bỏ qua phim đã chiếu
            if (expiredMovieIds.contains(movieId)) {
                continue;
            }
            if (cachedRatings.containsKey(movieId)) {
                moviesWithRatings.add(movie);
            }
        }

        // Sort by rating descending
        moviesWithRatings.sort((m1, m2) -> {
            Double r1 = cachedRatings.getOrDefault(m1.getMovieID(), 0.0);
            Double r2 = cachedRatings.getOrDefault(m2.getMovieID(), 0.0);
            return r2.compareTo(r1);
        });

        int top10PercentCount = Math.max(1, (int) Math.ceil(moviesWithRatings.size() * 0.1));

        // Fallback: dùng IMDb nếu không có ratings (cũng loại trừ phim đã chiếu)
        if (moviesWithRatings.isEmpty()) {
            List<Movie> allCopy = new ArrayList<>();
            for (Movie movie : cachedAllMovies) {
                if (!expiredMovieIds.contains(movie.getMovieID())) {
                    allCopy.add(movie);
                }
            }
            allCopy.sort((m1, m2) -> Double.compare(m2.getImdb(), m1.getImdb()));
            top10PercentCount = Math.max(1, (int) Math.ceil(allCopy.size() * 0.1));
            return new ArrayList<>(allCopy.subList(0, Math.min(top10PercentCount, allCopy.size())));
        }

        return new ArrayList<>(moviesWithRatings.subList(0, Math.min(top10PercentCount, moviesWithRatings.size())));
    }

    private void notifyCallbacks() {
        for (OnDataReadyCallback callback : pendingCallbacks) {
            callback.onDataReady(
                    new ArrayList<>(cachedNowShowing),
                    new ArrayList<>(cachedUpcoming),
                    new ArrayList<>(cachedTrending),
                    new ArrayList<>(cachedAllMovies));
        }
        pendingCallbacks.clear();
    }

    private void notifyError() {
        isLoading = false;
        // Trả về cached data nếu có, hoặc empty lists
        notifyCallbacks();
    }

    /**
     * Bắt đầu lắng nghe thay đổi realtime
     */
    public void startRealtimeUpdates() {
        if (moviesListener != null)
            return; // Đã listening

        moviesListener = moviesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isDataLoaded) {
                    Log.d(TAG, "Movies updated, refreshing cache...");
                    refreshCache(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /**
     * Dừng lắng nghe realtime
     */
    public void stopRealtimeUpdates() {
        if (moviesListener != null) {
            moviesRef.removeEventListener(moviesListener);
            moviesListener = null;
        }
    }

    /**
     * Clear cache
     */
    public void clearCache() {
        cachedAllMovies.clear();
        cachedNowShowing.clear();
        cachedUpcoming.clear();
        cachedTrending.clear();
        cachedExpired.clear();
        cachedRatings.clear();
        cachedEarliestShowtimes.clear();
        expiredMovieIds.clear();
        isDataLoaded = false;
        lastLoadTime = 0;
    }

    // Getters cho quick access
    public List<Movie> getCachedNowShowing() {
        return new ArrayList<>(cachedNowShowing);
    }

    public List<Movie> getCachedUpcoming() {
        return new ArrayList<>(cachedUpcoming);
    }

    public List<Movie> getCachedTrending() {
        return new ArrayList<>(cachedTrending);
    }

    public List<Movie> getCachedAllMovies() {
        return new ArrayList<>(cachedAllMovies);
    }

    public List<Movie> getCachedExpired() {
        return new ArrayList<>(cachedExpired);
    }

    /**
     * Kiểm tra xem phim có phải đã chiếu không (không còn suất chiếu trong tương
     * lai)
     * 
     * @param movieId ID của phim cần kiểm tra
     * @return true nếu phim đã chiếu, false nếu còn suất chiếu hoặc sắp chiếu
     */
    public boolean isMovieExpired(String movieId) {
        return expiredMovieIds.contains(movieId);
    }

    /**
     * Kiểm tra xem phim có suất chiếu trong tương lai không
     * 
     * @param movieId ID của phim
     * @return true nếu có suất chiếu, false nếu không có
     */
    public boolean hasUpcomingShowtimes(String movieId) {
        return cachedEarliestShowtimes.containsKey(movieId);
    }

    public boolean isDataReady() {
        return isDataLoaded && !isCacheExpired();
    }
}
