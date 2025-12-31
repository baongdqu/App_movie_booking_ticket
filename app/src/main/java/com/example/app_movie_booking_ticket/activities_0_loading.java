package com.example.app_movie_booking_ticket;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.example.app_movie_booking_ticket.extra.MovieCacheManager;

/**
 * Activity Loading (Màn hình chờ)
 * Màn hình đầu tiên xuất hiện khi mở ứng dụng.
 * Thực hiện các tác vụ:
 * 1. Kiểm tra kết nối mạng.
 * 2. Hiển thị animation logo (Fade in).
 * 3. Kiểm tra trạng thái đăng nhập (Firebase Auth).
 * 4. Chuyển hướng người dùng đến màn hình phù hợp (Login hoặc Home).
 */
public class activities_0_loading extends extra_manager_language {

    private FirebaseAuth mAuth;
    private ExecutorService executorService;
    private Handler mainHandler;

    // Biến lưu kết quả kiểm tra mạng (thread-safe)
    private AtomicBoolean internetCheckCompleted = new AtomicBoolean(false);
    private AtomicBoolean hasInternetResult = new AtomicBoolean(false);

    // Flag để đánh dấu loading đã xong chưa
    private AtomicBoolean loadingCompleted = new AtomicBoolean(false);

    // Thời gian loading tối thiểu (ms) - tối ưu xuống 5 giây
    private static final long MIN_LOADING_TIME = 5000;

    // Key để truyền kết quả kiểm tra mạng qua Intent
    public static final String EXTRA_NO_INTERNET = "extra_no_internet";

    /**
     * Phương thức khởi tạo.
     * Bắt đầu kiểm tra mạng và chạy animation.
     *
     * @param savedInstanceState Bundle chứa trạng thái
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.layouts_0_loading);

        // Khởi tạo executor và handler cho background task
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // ================== 🌐 BẮT ĐẦU KIỂM TRA MẠNG NGAY LẬP TỨC (SONG SONG)
        // ==================
        startInternetCheck();

        // ================== 🔊 PHÁT ÂM THANH SAU 500MS ==================
        new Handler().postDelayed(() -> {
            extra_sound_manager.playOpening(activities_0_loading.this);
        }, 500);

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // 📦 PRELOAD DỮ LIỆU PHIM ĐỂ GIẢM THỜI GIAN TẢI TRANG CHỦ
        // Bắt đầu tải dữ liệu phim song song với kiểm tra mạng
        MovieCacheManager.getInstance().preloadData();

        // 🎬 Netflix-style fade-in animation cho logo
        ImageView imgLogo = findViewById(R.id.imgLogo);
        android.view.animation.Animation scaleIn = android.view.animation.AnimationUtils.loadAnimation(this,
                R.anim.scale_fade_in);
        imgLogo.startAnimation(scaleIn);

        // ================== SAU KHI LOADING XONG (7 GIÂY) ==================
        new Handler().postDelayed(() -> {
            loadingCompleted.set(true);
            tryProceed();
        }, MIN_LOADING_TIME);
    }

    /**
     * Bắt đầu kiểm tra Internet NGAY LẬP TỨC (chạy song song với loading animation)
     */
    private void startInternetCheck() {
        executorService.execute(() -> {
            boolean hasInternet = hasActualInternetAccess();

            hasInternetResult.set(hasInternet);
            internetCheckCompleted.set(true);

            mainHandler.post(this::tryProceed);
        });
    }

    /**
     * Thử tiến hành vào app nếu cả loading VÀ kiểm tra mạng đều đã xong
     */
    private void tryProceed() {
        if (loadingCompleted.get() && internetCheckCompleted.get()) {
            // Luôn chuyển sang màn hình tiếp theo và truyền kết quả kiểm tra mạng
            proceedToNextScreen(!hasInternetResult.get()); // true = không có mạng
        }
    }

    /**
     * Kiểm tra xem có Internet thực sự hay không
     */
    private boolean hasActualInternetAccess() {
        if (!isNetworkAvailable()) {
            return false;
        }

        String[] testUrls = {
                "https://clients3.google.com/generate_204",
                "https://connectivitycheck.gstatic.com/generate_204",
                "https://www.google.com",
                "https://www.cloudflare.com"
        };

        for (String testUrl : testUrls) {
            if (canReachUrl(testUrl)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Thử kết nối đến một URL để kiểm tra có Internet thực sự
     */
    private boolean canReachUrl(String urlString) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.setUseCaches(false);
            connection.connect();

            int responseCode = connection.getResponseCode();
            return responseCode == 200 || responseCode == 204;
        } catch (IOException e) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Kiểm tra kết nối mạng vật lý
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.net.Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) {
                return false;
            }
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
        } else {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    /**
     * Tiếp tục vào màn hình tiếp theo và truyền kết quả kiểm tra mạng
     * 
     * @param noInternet true nếu không có mạng, false nếu có mạng
     */
    private void proceedToNextScreen(boolean noInternet) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Intent targetIntent;

        if (currentUser != null && currentUser.isEmailVerified()) {
            // Đã đăng nhập -> vào Menu
            targetIntent = new Intent(activities_0_loading.this, activities_2_a_menu_manage_fragments.class);
        } else {
            // Chưa đăng nhập -> vào Login
            if (currentUser != null && !currentUser.isEmailVerified()) {
                mAuth.signOut();
            }
            targetIntent = new Intent(activities_0_loading.this, activities_1_login.class);
        }

        // Truyền kết quả kiểm tra mạng qua Intent gốc
        targetIntent.putExtra(EXTRA_NO_INTERNET, noInternet);

        // KIỂM TRA MÃ PIN (Chỉ kiểm tra khi người dùng ĐÃ ĐĂNG NHẬP)
        if (currentUser != null && currentUser.isEmailVerified()) {
            android.content.SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
            boolean isPinEnabled = prefs.getBoolean("pin_enabled", false);

            if (isPinEnabled) {
                // Nếu bật PIN -> Chuyển hướng sang Lock Screen, truyền Intent đích theo
                Intent lockIntent = new Intent(activities_0_loading.this, activities_2_a_lock_screen.class);
                lockIntent.putExtra("target_intent", targetIntent);
                startActivity(lockIntent);
            } else {
                // Đã đăng nhập nhưng không bật PIN -> Vào thẳng
                startActivity(targetIntent);
            }
        } else {
            // Chưa đăng nhập (hoặc chưa verify email) -> Bỏ qua PIN -> Vào thẳng (Login
            // hoặc màn hình tương ứng)
            // Lưu ý: Nếu user chưa đăng nhập thì việc hỏi PIN là vô nghĩa và có thể gây
            // kẹt.
            // Có thể cân nhắc reset pin_enabled về false ở đây để dọn dẹp, nhưng tốt hơn là
            // xử lý lúc Logout.
            startActivity(targetIntent);
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
