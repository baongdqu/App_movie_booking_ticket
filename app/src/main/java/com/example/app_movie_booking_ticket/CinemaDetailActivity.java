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
public class CinemaDetailActivity extends extra_manager_language implements CinemaMovieAdapter.OnMovieClickListener {

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
        extra_themeutils.applySavedTheme(this);
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
            tvRatingCount.setText(getString(R.string.reviews_count, cinema.getUserRatingsTotal()));
        } else {
            tvRating.setText(getString(R.string.rating_not_available));
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
            tvScreens.setText(getString(R.string.format_screens_count, screens));
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

        bookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nowShowingMovies.clear();
                upcomingMovies.clear();

                Map<String, Integer> nowShowingShowtimes = new HashMap<>();
                Map<String, Date> nowShowingEarliest = new HashMap<>();
                Map<String, Integer> upcomingShowtimes = new HashMap<>();
                Map<String, Date> upcomingEarliest = new HashMap<>();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm", Locale.getDefault());
                Date now = new Date();

                // Ngưỡng 7 ngày để phân loại phim sắp chiếu
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, 7);
                Date upcomingThreshold = cal.getTime();

                for (DataSnapshot movieSnap : snapshot.getChildren()) {
                    String movieTitle = movieSnap.getKey();

                    for (DataSnapshot showtimeSnap : movieSnap.getChildren()) {
                        String showtimeKey = showtimeSnap.getKey();
                        try {
                            Date showtimeDate = sdf.parse(showtimeKey);
                            if (showtimeDate == null || showtimeDate.before(now)) {
                                continue;
                            }

                            // --- ĐOẠN QUAN TRỌNG NHẤT ---
                            DataSnapshot cinemasSnap = showtimeSnap.child("cinemas");
                            boolean matchFound = false;

                            for (DataSnapshot cinemaInDb : cinemasSnap.getChildren()) {
                                String dbCinemaKey = cinemaInDb.getKey();
                                String dbCinemaName = cinemaInDb.child("name").getValue(String.class);

                                if (matchesCinema(dbCinemaKey, dbCinemaName)) {
                                    matchFound = true;
                                    break;
                                }
                            }

                            // CHỈ KHI MATCH MỚI PUT VÀO MAP
                            if (matchFound) {
                                if (showtimeDate.before(upcomingThreshold)) {
                                    updateShowtimeMap(nowShowingShowtimes, nowShowingEarliest, movieTitle,
                                            showtimeDate);
                                } else {
                                    updateShowtimeMap(upcomingShowtimes, upcomingEarliest, movieTitle, showtimeDate);
                                }
                            }
                            // ---------------------------

                        } catch (ParseException e) {
                            Log.e(TAG, "Lỗi định dạng ngày: " + showtimeKey);
                        }
                    }
                }
                // Sau khi lọc xong mới load chi tiết phim từ Cache
                loadMovieDetailsFromCache(nowShowingShowtimes, nowShowingEarliest, upcomingShowtimes, upcomingEarliest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBarMovies.setVisibility(View.GONE);
            }
        });
    }

    // Hàm hỗ trợ cập nhật Map suất chiếu
    private void updateShowtimeMap(Map<String, Integer> counts, Map<String, Date> earliest, String title, Date date) {
        counts.put(title, counts.getOrDefault(title, 0) + 1);
        if (!earliest.containsKey(title) || date.before(earliest.get(title))) {
            earliest.put(title, date);
        }
    }

    /**
     * Kiểm tra xem cinema key/name có match với rạp hiện tại không
     */
    private boolean matchesCinema(String cinemaKey, String cinemaNameFromBooking) {
        if (cinemaKey == null || cinemaId == null)
            return false;

        // Chuẩn hóa cả hai ID về cùng một định dạng (chữ thường, thay gạch nối bằng
        // gạch dưới)
        String normalizedAppId = cinemaId.toLowerCase().replace("-", "_").trim();
        String normalizedDbKey = cinemaKey.toLowerCase().replace("-", "_").trim();

        // KIỂM TRA 1: So khớp ID (Chứa nhau)
        if (normalizedAppId.contains(normalizedDbKey) || normalizedDbKey.contains(normalizedAppId)) {
            Log.d(TAG, "MATCH SUCCESS by ID: " + normalizedAppId + " <-> " + normalizedDbKey);
            return true;
        }

        // KIỂM TRA 2: So khớp tên rạp đã chuẩn hóa (Dự phòng)
        if (cinemaNameFromBooking != null) {
            String appName = normalizeString(cinema.getName());
            String dbName = normalizeString(cinemaNameFromBooking);

            if (appName.contains(dbName) || dbName.contains(appName)) {
                Log.d(TAG, "MATCH SUCCESS by Name: " + appName + " <-> " + dbName);
                return true;
            }
        }

        return false;
    }
    // private boolean matchesCinema(String cinemaKey, String cinemaNameFromBooking)
    // {
    // String currentCinemaName = cinema.getName();
    //
    // // So sánh bằng key (snake_case) - exact match
    // if (cinemaKey != null && cinemaKey.equalsIgnoreCase(cinemaId)) {
    // Log.d(TAG, "MATCH by exact key: " + cinemaKey + " == " + cinemaId);
    // return true;
    // }
    //
    // // So sánh bằng key - prefix/contains match
    // // Key trong Bookings (vd: galaxy_linh_trung) có thể ngắn hơn cinemaId (vd:
    // // galaxy_linh_trung_thu_duc)
    // if (cinemaKey != null && cinemaId != null) {
    // String normalizedKey = cinemaKey.toLowerCase().replace("-", "_");
    // String normalizedCurrentId = cinemaId.toLowerCase().replace("-", "_");
    //
    // // Kiểm tra startsWith hoặc contains
    // if (normalizedCurrentId.startsWith(normalizedKey) ||
    // normalizedKey.startsWith(normalizedCurrentId)) {
    // Log.d(TAG, "MATCH by key prefix: " + normalizedKey + " <-> " +
    // normalizedCurrentId);
    // return true;
    // }
    //
    // // Kiểm tra contains
    // if (normalizedCurrentId.contains(normalizedKey) ||
    // normalizedKey.contains(normalizedCurrentId)) {
    // Log.d(TAG, "MATCH by key contains: " + normalizedKey + " <-> " +
    // normalizedCurrentId);
    // return true;
    // }
    // }
    //
    // // So sánh bằng tên (case insensitive và bỏ dấu)
    // if (cinemaNameFromBooking != null && currentCinemaName != null) {
    // String normalizedBookingName = normalizeString(cinemaNameFromBooking);
    // String normalizedCurrentName = normalizeString(currentCinemaName);
    //
    // // So sánh chứa
    // if (normalizedBookingName.contains(normalizedCurrentName)
    // || normalizedCurrentName.contains(normalizedBookingName)) {
    // Log.d(TAG, "MATCH by contains: '" + normalizedBookingName + "' <-> '" +
    // normalizedCurrentName + "'");
    // return true;
    // }
    //
    // // So sánh dựa trên brand + location keywords
    // String[] brands = { "cgv", "galaxy", "lotte", "bhd", "cinestar", "mega",
    // "beta" };
    // String[] locations = { "thuduc", "gigamall", "linhtrung", "cantavil",
    // "grandpark", "pearl", "plaza" };
    //
    // String matchedBrand = null;
    // String matchedLocation = null;
    //
    // // Tìm brand chung
    // for (String brand : brands) {
    // if (normalizedBookingName.contains(brand) &&
    // normalizedCurrentName.contains(brand)) {
    // matchedBrand = brand;
    // break;
    // }
    // }
    //
    // // Tìm location chung
    // for (String location : locations) {
    // if (normalizedBookingName.contains(location) &&
    // normalizedCurrentName.contains(location)) {
    // matchedLocation = location;
    // break;
    // }
    // }
    //
    // // Nếu cả brand và location đều match -> cùng rạp
    // if (matchedBrand != null && matchedLocation != null) {
    // Log.d(TAG, "MATCH by brand+location: " + matchedBrand + " + " +
    // matchedLocation);
    // return true;
    // }
    //
    // // Nếu chỉ có brand match và tên ngắn -> có thể cùng rạp
    // // So sánh thêm bằng độ tương đồng
    // if (matchedBrand != null) {
    // // Tính số ký tự chung
    // int commonChars = countCommonChars(normalizedBookingName,
    // normalizedCurrentName);
    // int minLength = Math.min(normalizedBookingName.length(),
    // normalizedCurrentName.length());
    // double similarity = (double) commonChars / minLength;
    //
    // if (similarity >= 0.7) { // 70% tương đồng
    // Log.d(TAG, "MATCH by brand + similarity: " + matchedBrand + ", similarity=" +
    // similarity);
    // return true;
    // }
    // }
    // }
    //
    // return false;
    // }

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
            // 1. Tạo map dùng Title đã viết thường làm Key
            Map<String, Movie> movieMap = new HashMap<>();
            for (Movie movie : allMovies) {
                if (movie.getTitle() != null) {
                    movieMap.put(movie.getTitle().toLowerCase().trim(), movie);
                }
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            // 2. Xử lý phim Đang chiếu
            for (Map.Entry<String, Integer> entry : nowShowingShowtimes.entrySet()) {
                // movieId ở đây thực chất là Tên phim từ Key của Firebase (vd: "Atlas")
                String movieTitleFromFb = entry.getKey();
                int showtimeCount = entry.getValue();

                // SỬA TẠI ĐÂY: Truy vấn bằng tên đã viết thường
                Movie movie = movieMap.get(movieTitleFromFb.toLowerCase().trim());

                if (movie != null) {
                    double rating = movie.getImdb();
                    String dateStr = "";
                    Date earliest = nowShowingEarliest.get(movieTitleFromFb);
                    if (earliest != null) {
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

            // 3. Xử lý phim Sắp chiếu
            for (Map.Entry<String, Integer> entry : upcomingShowtimes.entrySet()) {
                String movieTitleFromFb = entry.getKey();
                int showtimeCount = entry.getValue();

                // SỬA TẠI ĐÂY: Truy vấn bằng tên đã viết thường
                Movie movie = movieMap.get(movieTitleFromFb.toLowerCase().trim());

                if (movie != null) {
                    double rating = movie.getImdb();
                    String dateStr = "";
                    Date earliest = upcomingEarliest.get(movieTitleFromFb);
                    if (earliest != null) {
                        dateStr = dateFormat.format(earliest);
                    }
                    upcomingMovies.add(new CinemaMovieAdapter.CinemaMovie(
                            movie, showtimeCount, true, rating, dateStr));
                }
            }

            runOnUiThread(() -> {
                progressBarMovies.setVisibility(View.GONE);
                updateMoviesUI();
            });
        });
    }

    /**
     * Cập nhật UI hiển thị phim
     */
    private void updateMoviesUI() {
        // Luôn ẩn ProgressBar khi đã nạp xong dữ liệu
        progressBarMovies.setVisibility(View.GONE);

        if (nowShowingMovies.isEmpty() && upcomingMovies.isEmpty()) {
            tvNoMovies.setVisibility(View.VISIBLE);
            layoutNowShowingMovies.setVisibility(View.GONE);
            layoutUpcomingMovies.setVisibility(View.GONE);
        } else {
            tvNoMovies.setVisibility(View.GONE);

            // Hiển thị và cập nhật số lượng phim ĐANG CHIẾU
            if (!nowShowingMovies.isEmpty()) {
                layoutNowShowingMovies.setVisibility(View.VISIBLE);
                // THÊM DÒNG NÀY để hiện số lượng phim
                tvNowShowingCount.setText(getString(R.string.format_movies_count, nowShowingMovies.size()));
                nowShowingAdapter.updateList(nowShowingMovies);
            } else {
                layoutNowShowingMovies.setVisibility(View.GONE);
            }

            // Hiển thị và cập nhật số lượng phim SẮP CHIẾU
            if (!upcomingMovies.isEmpty()) {
                layoutUpcomingMovies.setVisibility(View.VISIBLE);
                // THÊM DÒNG NÀY để hiện số lượng phim
                tvUpcomingCount.setText(getString(R.string.format_movies_count, upcomingMovies.size()));
                upcomingAdapter.updateList(upcomingMovies);
            } else {
                layoutUpcomingMovies.setVisibility(View.GONE);
            }
        }

        Log.d(TAG, "Cập nhật UI thành công: " + nowShowingMovies.size() + " đang chiếu, "
                + upcomingMovies.size() + " sắp chiếu");
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
