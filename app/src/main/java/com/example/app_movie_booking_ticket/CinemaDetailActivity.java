package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_movie_booking_ticket.adapter.CinemaMovieAdapter;
import com.example.app_movie_booking_ticket.extra.MovieCacheManager;
import com.example.app_movie_booking_ticket.model.Movie;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Activity to display detailed information about a cinema.
 * Bao gồm thông tin rạp và danh sách phim đang chiếu/sắp chiếu tại rạp.
 */
public class CinemaDetailActivity extends AppCompatActivity implements CinemaMovieAdapter.OnMovieClickListener {

    private static final String TAG = "CinemaDetailActivity";
    public static final String EXTRA_CINEMA = "extra_cinema";

    // Views - Thông tin rạp
    private ImageView imgCinema;
    private TextView tvCinemaName;
    private TextView tvDistance;
    private TextView tvRating;
    private TextView tvRatingCount;
    private TextView tvStatus;
    private TextView tvAddress;
    private TextView tvPhone;
    private TextView tvWorkingHours;
    private TextView tvScreens;
    private ChipGroup chipGroupAmenities;
    private LinearLayout layoutPhone;
    private LinearLayout layoutHours;
    private LinearLayout layoutScreens;
    private MaterialButton btnCall;
    private MaterialButton btnDirections;
    private MaterialToolbar toolbar;

    // Views - Danh sách phim
    private LinearLayout layoutNowShowingMovies;
    private LinearLayout layoutUpcomingMovies;
    private RecyclerView recyclerNowShowingMovies;
    private RecyclerView recyclerUpcomingMovies;
    private TextView tvNowShowingCount;
    private TextView tvUpcomingCount;
    private ProgressBar progressBarMovies;
    private TextView tvNoMovies;

    // Data
    private Cinema cinema;
    private String cinemaId;
    private DatabaseReference bookingsRef;
    private MovieCacheManager movieCacheManager;

    // Adapters
    private CinemaMovieAdapter nowShowingAdapter;
    private CinemaMovieAdapter upcomingAdapter;
    private List<CinemaMovieAdapter.CinemaMovie> nowShowingMovies = new ArrayList<>();
    private List<CinemaMovieAdapter.CinemaMovie> upcomingMovies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layouts_cinema_detail);

        // Get cinema from intent
        cinema = (Cinema) getIntent().getSerializableExtra(EXTRA_CINEMA);
        if (cinema == null) {
            finish();
            return;
        }

        // Tạo cinemaId từ tên rạp (sanitize)
        cinemaId = sanitizeCinemaName(cinema.getName());

        initViews();
        setupToolbar();
        bindData();
        setupClickListeners();
        setupRecyclerViews();

        // Load phim từ Firebase
        movieCacheManager = MovieCacheManager.getInstance();
        bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");
        loadMoviesForCinema();
    }

    private void initViews() {
        // Thông tin rạp
        imgCinema = findViewById(R.id.imgCinema);
        tvCinemaName = findViewById(R.id.tvCinemaName);
        tvDistance = findViewById(R.id.tvDistance);
        tvRating = findViewById(R.id.tvRating);
        tvRatingCount = findViewById(R.id.tvRatingCount);
        tvStatus = findViewById(R.id.tvStatus);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvWorkingHours = findViewById(R.id.tvWorkingHours);
        tvScreens = findViewById(R.id.tvScreens);
        chipGroupAmenities = findViewById(R.id.chipGroupAmenities);
        layoutPhone = findViewById(R.id.layoutPhone);
        layoutHours = findViewById(R.id.layoutHours);
        layoutScreens = findViewById(R.id.layoutScreens);
        btnCall = findViewById(R.id.btnCall);
        btnDirections = findViewById(R.id.btnDirections);
        toolbar = findViewById(R.id.toolbar);

        // Danh sách phim
        layoutNowShowingMovies = findViewById(R.id.layoutNowShowingMovies);
        layoutUpcomingMovies = findViewById(R.id.layoutUpcomingMovies);
        recyclerNowShowingMovies = findViewById(R.id.recyclerNowShowingMovies);
        recyclerUpcomingMovies = findViewById(R.id.recyclerUpcomingMovies);
        tvNowShowingCount = findViewById(R.id.tvNowShowingCount);
        tvUpcomingCount = findViewById(R.id.tvUpcomingCount);
        progressBarMovies = findViewById(R.id.progressBarMovies);
        tvNoMovies = findViewById(R.id.tvNoMovies);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerViews() {
        // Now Showing RecyclerView
        nowShowingAdapter = new CinemaMovieAdapter(this, nowShowingMovies,
                cinema.getName(), cinemaId, this);
        recyclerNowShowingMovies.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerNowShowingMovies.setAdapter(nowShowingAdapter);

        // Upcoming RecyclerView
        upcomingAdapter = new CinemaMovieAdapter(this, upcomingMovies,
                cinema.getName(), cinemaId, this);
        recyclerUpcomingMovies.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerUpcomingMovies.setAdapter(upcomingAdapter);
    }

    private void bindData() {
        // Cinema image
        String photoUrl = cinema.getPhotoReference();
        if (photoUrl != null && !photoUrl.isEmpty()) {
            Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_cinema)
                    .error(R.drawable.ic_cinema)
                    .centerCrop()
                    .into(imgCinema);
        }

        // Basic info
        tvCinemaName.setText(cinema.getName());
        tvDistance.setText(cinema.getFormattedDistance());
        tvAddress.setText(cinema.getAddress());

        // Rating
        if (cinema.getRating() > 0) {
            tvRating.setText(String.format("%.1f", cinema.getRating()));
            tvRatingCount.setText("(" + cinema.getUserRatingsTotal() + " đánh giá)");
        } else {
            tvRating.setText("N/A");
            tvRatingCount.setVisibility(View.GONE);
        }

        // Status
        if (cinema.hasOpeningHours()) {
            tvStatus.setVisibility(View.VISIBLE);
            if (cinema.isOpenNow()) {
                tvStatus.setText(R.string.cinema_open_now);
                tvStatus.setBackgroundResource(R.drawable.bg_status_open);
            } else {
                tvStatus.setText(R.string.cinema_closed);
                tvStatus.setBackgroundResource(R.drawable.bg_status_closed);
            }
        } else {
            tvStatus.setVisibility(View.GONE);
        }

        // Phone
        String phone = cinema.getPhone();
        if (phone != null && !phone.isEmpty()) {
            layoutPhone.setVisibility(View.VISIBLE);
            tvPhone.setText(phone);
        } else {
            layoutPhone.setVisibility(View.GONE);
            btnCall.setVisibility(View.GONE);
        }

        // Working hours
        String workingHours = cinema.getWorkingHours();
        if (workingHours != null && !workingHours.isEmpty()) {
            layoutHours.setVisibility(View.VISIBLE);
            tvWorkingHours.setText(workingHours);
        } else {
            layoutHours.setVisibility(View.GONE);
        }

        // Screens
        int screens = cinema.getScreens();
        if (screens > 0) {
            layoutScreens.setVisibility(View.VISIBLE);
            tvScreens.setText(screens + " phòng chiếu");
        } else {
            layoutScreens.setVisibility(View.GONE);
        }

        // Amenities
        if (cinema.getAmenities() != null && !cinema.getAmenities().isEmpty()) {
            chipGroupAmenities.removeAllViews();
            for (String amenity : cinema.getAmenities()) {
                Chip chip = new Chip(this);
                chip.setText(amenity);
                chip.setClickable(false);
                chip.setChipBackgroundColorResource(R.color.profile_accent);
                chip.setTextColor(getResources().getColor(android.R.color.white, null));
                chipGroupAmenities.addView(chip);
            }
        } else {
            chipGroupAmenities.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        // Call button
        btnCall.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            String phone = cinema.getPhone();
            if (phone != null && !phone.isEmpty()) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone.replaceAll("\\s+", "")));
                startActivity(callIntent);
            }
        });

        // Phone row click
        layoutPhone.setOnClickListener(v -> btnCall.performClick());

        // Directions button
        btnDirections.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            String uri = "google.navigation:q=" + cinema.getLatitude() + "," + cinema.getLongitude();
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Fallback to browser
                String browserUri = "https://www.google.com/maps/dir/?api=1&destination="
                        + cinema.getLatitude() + "," + cinema.getLongitude();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(browserUri)));
            }
        });
    }

    /**
     * Load danh sách phim có suất chiếu tại rạp này từ Firebase Bookings
     */
    private void loadMoviesForCinema() {
        progressBarMovies.setVisibility(View.VISIBLE);
        tvNoMovies.setVisibility(View.GONE);

        Log.d(TAG, "Loading movies for cinema: " + cinema.getName() + " (ID: " + cinemaId + ")");

        bookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBarMovies.setVisibility(View.GONE);

                // Map lưu movieId -> số suất chiếu, earliest date
                Map<String, Integer> nowShowingShowtimes = new HashMap<>();
                Map<String, Date> nowShowingEarliest = new HashMap<>();

                Map<String, Integer> upcomingShowtimes = new HashMap<>();
                Map<String, Date> upcomingEarliest = new HashMap<>();

                Set<String> movieIdsAtCinema = new HashSet<>();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm", Locale.getDefault());
                SimpleDateFormat displaySdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                Date now = new Date();

                // Tính ngày giới hạn:
                // - ĐANG CHIẾU: suất chiếu từ NOW đến hết ngày thứ 7
                // - SẮP CHIẾU: suất chiếu từ ngày thứ 8 trở đi
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, 7);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                Date upcomingThreshold = cal.getTime();

                Log.d(TAG, "=== PHÂN LOẠI SUẤT CHIẾU ===");
                Log.d(TAG, "Thời điểm hiện tại: " + displaySdf.format(now));
                Log.d(TAG, "Ngưỡng 7 ngày: " + displaySdf.format(upcomingThreshold));
                Log.d(TAG, "ĐANG CHIẾU: " + displaySdf.format(now) + " -> " + displaySdf.format(upcomingThreshold));
                Log.d(TAG, "SẮP CHIẾU: sau " + displaySdf.format(upcomingThreshold));

                // Duyệt qua tất cả phim trong Bookings
                for (DataSnapshot movieSnap : snapshot.getChildren()) {
                    String movieId = movieSnap.getKey();

                    // Duyệt qua các suất chiếu
                    for (DataSnapshot showtimeSnap : movieSnap.getChildren()) {
                        String showtimeKey = showtimeSnap.getKey();

                        try {
                            Date showtimeDate = sdf.parse(showtimeKey);
                            if (showtimeDate == null || showtimeDate.before(now)) {
                                continue; // Bỏ qua suất chiếu đã qua
                            }

                            // Kiểm tra xem rạp này có trong suất chiếu không
                            DataSnapshot cinemasSnap = showtimeSnap.child("cinemas");
                            boolean matchFound = false;

                            for (DataSnapshot cinemaSnap : cinemasSnap.getChildren()) {
                                String cinemaKey = cinemaSnap.getKey();
                                String cinemaName = cinemaSnap.child("name").getValue(String.class);

                                // Debug: Log các cinemaKey được tìm thấy (chỉ log 1 lần cho mỗi movie)
                                if (movieSnap.getKey().equals("1990") && showtimeKey.startsWith("2025")) {
                                    Log.v(TAG, "Checking cinema: key=" + cinemaKey + ", name=" + cinemaName);
                                }

                                // So sánh với cinemaId hoặc tên rạp
                                if (matchesCinema(cinemaKey, cinemaName)) {
                                    matchFound = true;
                                    break;
                                }
                            }

                            if (matchFound) {
                                movieIdsAtCinema.add(movieId);

                                // Phân loại: đang chiếu (trong 7 ngày) hay sắp chiếu
                                if (showtimeDate.before(upcomingThreshold)) {
                                    // Đang chiếu
                                    int count = nowShowingShowtimes.getOrDefault(movieId, 0);
                                    nowShowingShowtimes.put(movieId, count + 1);

                                    // Update earliest date
                                    Date currentEarliest = nowShowingEarliest.get(movieId);
                                    if (currentEarliest == null || showtimeDate.before(currentEarliest)) {
                                        nowShowingEarliest.put(movieId, showtimeDate);
                                    }
                                } else {
                                    // Sắp chiếu
                                    int count = upcomingShowtimes.getOrDefault(movieId, 0);
                                    upcomingShowtimes.put(movieId, count + 1);

                                    // Update earliest date
                                    Date currentEarliest = upcomingEarliest.get(movieId);
                                    if (currentEarliest == null || showtimeDate.before(currentEarliest)) {
                                        upcomingEarliest.put(movieId, showtimeDate);
                                    }
                                }
                            }
                        } catch (ParseException e) {
                            Log.w(TAG, "Invalid showtime format: " + showtimeKey);
                        }
                    }
                }

                Log.d(TAG, "Found " + movieIdsAtCinema.size() + " movies at this cinema");
                Log.d(TAG, "Now showing: " + nowShowingShowtimes.size() + ", Upcoming: " + upcomingShowtimes.size());

                // Lấy thông tin phim từ cache
                loadMovieDetailsFromCache(nowShowingShowtimes, nowShowingEarliest, upcomingShowtimes, upcomingEarliest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBarMovies.setVisibility(View.GONE);
                tvNoMovies.setVisibility(View.VISIBLE);
                tvNoMovies.setText("Không thể tải danh sách phim");
                Log.e(TAG, "Error loading bookings: " + error.getMessage());
            }
        });
    }

    /**
     * Kiểm tra xem cinema key/name có match với rạp hiện tại không
     */
    private boolean matchesCinema(String cinemaKey, String cinemaNameFromBooking) {
        String currentCinemaName = cinema.getName();

        // So sánh bằng key (snake_case) - exact match
        if (cinemaKey != null && cinemaKey.equalsIgnoreCase(cinemaId)) {
            Log.d(TAG, "MATCH by exact key: " + cinemaKey + " == " + cinemaId);
            return true;
        }

        // So sánh bằng key - prefix/contains match
        // Key trong Bookings (vd: galaxy_linh_trung) có thể ngắn hơn cinemaId (vd:
        // galaxy_linh_trung_thu_duc)
        if (cinemaKey != null && cinemaId != null) {
            String normalizedKey = cinemaKey.toLowerCase().replace("-", "_");
            String normalizedCurrentId = cinemaId.toLowerCase().replace("-", "_");

            // Kiểm tra startsWith hoặc contains
            if (normalizedCurrentId.startsWith(normalizedKey) || normalizedKey.startsWith(normalizedCurrentId)) {
                Log.d(TAG, "MATCH by key prefix: " + normalizedKey + " <-> " + normalizedCurrentId);
                return true;
            }

            // Kiểm tra contains
            if (normalizedCurrentId.contains(normalizedKey) || normalizedKey.contains(normalizedCurrentId)) {
                Log.d(TAG, "MATCH by key contains: " + normalizedKey + " <-> " + normalizedCurrentId);
                return true;
            }
        }

        // So sánh bằng tên (case insensitive và bỏ dấu)
        if (cinemaNameFromBooking != null && currentCinemaName != null) {
            String normalizedBookingName = normalizeString(cinemaNameFromBooking);
            String normalizedCurrentName = normalizeString(currentCinemaName);

            // So sánh chứa
            if (normalizedBookingName.contains(normalizedCurrentName)
                    || normalizedCurrentName.contains(normalizedBookingName)) {
                Log.d(TAG, "MATCH by contains: '" + normalizedBookingName + "' <-> '" + normalizedCurrentName + "'");
                return true;
            }

            // So sánh dựa trên brand + location keywords
            String[] brands = { "cgv", "galaxy", "lotte", "bhd", "cinestar", "mega", "beta" };
            String[] locations = { "thuduc", "gigamall", "linhtrung", "cantavil", "grandpark", "pearl", "plaza" };

            String matchedBrand = null;
            String matchedLocation = null;

            // Tìm brand chung
            for (String brand : brands) {
                if (normalizedBookingName.contains(brand) && normalizedCurrentName.contains(brand)) {
                    matchedBrand = brand;
                    break;
                }
            }

            // Tìm location chung
            for (String location : locations) {
                if (normalizedBookingName.contains(location) && normalizedCurrentName.contains(location)) {
                    matchedLocation = location;
                    break;
                }
            }

            // Nếu cả brand và location đều match -> cùng rạp
            if (matchedBrand != null && matchedLocation != null) {
                Log.d(TAG, "MATCH by brand+location: " + matchedBrand + " + " + matchedLocation);
                return true;
            }

            // Nếu chỉ có brand match và tên ngắn -> có thể cùng rạp
            // So sánh thêm bằng độ tương đồng
            if (matchedBrand != null) {
                // Tính số ký tự chung
                int commonChars = countCommonChars(normalizedBookingName, normalizedCurrentName);
                int minLength = Math.min(normalizedBookingName.length(), normalizedCurrentName.length());
                double similarity = (double) commonChars / minLength;

                if (similarity >= 0.7) { // 70% tương đồng
                    Log.d(TAG, "MATCH by brand + similarity: " + matchedBrand + ", similarity=" + similarity);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Đếm số ký tự chung giữa hai chuỗi
     */
    private int countCommonChars(String s1, String s2) {
        int count = 0;
        boolean[] used = new boolean[s2.length()];

        for (char c : s1.toCharArray()) {
            for (int i = 0; i < s2.length(); i++) {
                if (!used[i] && s2.charAt(i) == c) {
                    count++;
                    used[i] = true;
                    break;
                }
            }
        }
        return count;
    }

    /**
     * Chuẩn hóa chuỗi để so sánh (bỏ dấu, lowercase, bỏ khoảng trắng)
     */
    private String normalizeString(String str) {
        if (str == null)
            return "";
        return str.toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[^a-z0-9]", "");
    }

    /**
     * Load chi tiết phim từ MovieCacheManager
     */
    private void loadMovieDetailsFromCache(Map<String, Integer> nowShowingShowtimes,
            Map<String, Date> nowShowingEarliest,
            Map<String, Integer> upcomingShowtimes,
            Map<String, Date> upcomingEarliest) {
        nowShowingMovies.clear();
        upcomingMovies.clear();

        movieCacheManager.getFilteredMovies((nowShowing, upcoming, trending, allMovies) -> {
            // Tạo map movieId -> Movie cho tìm kiếm nhanh
            Map<String, Movie> movieMap = new HashMap<>();
            for (Movie movie : allMovies) {
                movieMap.put(movie.getMovieID(), movie);
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            // Phim đang chiếu tại rạp
            for (Map.Entry<String, Integer> entry : nowShowingShowtimes.entrySet()) {
                String movieId = entry.getKey();
                int showtimeCount = entry.getValue();
                Movie movie = movieMap.get(movieId);

                if (movie != null) {
                    double rating = movie.getImdb();

                    // Lấy ngày sớm nhất
                    String dateStr = "";
                    Date earliest = nowShowingEarliest.get(movieId);
                    if (earliest != null) {
                        // Nếu là hôm nay, hiện giờ. Nếu ngày khác, hiện dd/MM
                        if (android.text.format.DateUtils.isToday(earliest.getTime())) {
                            dateStr = "Hôm nay " + timeFormat.format(earliest);
                        } else {
                            dateStr = dateFormat.format(earliest);
                        }
                    }

                    nowShowingMovies.add(new CinemaMovieAdapter.CinemaMovie(
                            movie, showtimeCount, false, rating, dateStr));
                }
            }

            // Phim sắp chiếu tại rạp
            for (Map.Entry<String, Integer> entry : upcomingShowtimes.entrySet()) {
                String movieId = entry.getKey();
                int showtimeCount = entry.getValue();
                Movie movie = movieMap.get(movieId);

                if (movie != null) {
                    double rating = movie.getImdb();

                    String dateStr = "";
                    Date earliest = upcomingEarliest.get(movieId);
                    if (earliest != null) {
                        dateStr = dateFormat.format(earliest);
                    }

                    upcomingMovies.add(new CinemaMovieAdapter.CinemaMovie(
                            movie, showtimeCount, true, rating, dateStr));
                }
            }

            // Cập nhật UI
            updateMoviesUI();
        });
    }

    /**
     * Cập nhật UI hiển thị phim
     */
    private void updateMoviesUI() {
        // Phim đang chiếu
        if (!nowShowingMovies.isEmpty()) {
            layoutNowShowingMovies.setVisibility(View.VISIBLE);
            tvNowShowingCount.setText(nowShowingMovies.size() + " phim");
            nowShowingAdapter.updateList(nowShowingMovies);
        } else {
            layoutNowShowingMovies.setVisibility(View.GONE);
        }

        // Phim sắp chiếu
        if (!upcomingMovies.isEmpty()) {
            layoutUpcomingMovies.setVisibility(View.VISIBLE);
            tvUpcomingCount.setText(upcomingMovies.size() + " phim");
            upcomingAdapter.updateList(upcomingMovies);
        } else {
            layoutUpcomingMovies.setVisibility(View.GONE);
        }

        // Hiện thông báo nếu không có phim nào
        if (nowShowingMovies.isEmpty() && upcomingMovies.isEmpty()) {
            tvNoMovies.setVisibility(View.VISIBLE);
        } else {
            tvNoMovies.setVisibility(View.GONE);
        }

        Log.d(TAG, "Updated UI - Now Showing: " + nowShowingMovies.size()
                + ", Upcoming: " + upcomingMovies.size());
    }

    /**
     * Sanitize tên rạp thành ID (snake_case, không dấu)
     */
    private String sanitizeCinemaName(String name) {
        if (name == null)
            return "";
        return name.toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_|_$", "");
    }

    /**
     * Callback khi click vào phim
     * Chuyển sang màn hình chi tiết phim với thông tin rạp
     */
    @Override
    public void onMovieClick(Movie movie, String cinemaName, String cinemaIdParam, int showtimeCount) {
        extra_sound_manager.playUiClick(this);

        Intent intent = new Intent(this, parthome_movie_detail.class);
        intent.putExtra("movie", movie);
        intent.putExtra("fromCinema", true);
        intent.putExtra("cinemaName", cinemaName);
        intent.putExtra("cinemaId", cinemaIdParam);
        intent.putExtra("showtimeCount", showtimeCount);
        startActivity(intent);
    }
}
