package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Activity 'Menu Manage Fragments'
 * Đây là màn hình chính của ứng dụng chứa Bottom Navigation.
 * Quản lý việc hiển thị và chuyển đổi giữa các Fragment chính:
 * - Home (Trang chủ)
 * - Mail (Thông báo/Tin nhắn)
 * - Chatbot (Trợ lý AI) (Mở activity riêng)
 * - Notifications (Thông báo)
 * - User (Thông tin cá nhân)
 */
public class activities_2_a_menu_manage_fragments extends extra_manager_language {

    private BottomNavigationView bottomNavigationView;
    private Fragment fragmentHome, fragmentCinema, fragmentMail, fragmentNotifications, fragmentUser;
    private Fragment activeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra_themeutils.applySavedTheme(this);
        setContentView(R.layout.layouts_2_a_menu_manage_fragments);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        initFragments();
        // Load fragment mặc định (Home)
        // Xử lý Intent điều hướng Fragment
        if (getIntent().hasExtra("OPEN_FRAGMENT")) {
            handleNavigationIntent(getIntent());
        }
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // ================== 🌐 KIỂM TRA KẾT QUẢ MẠNG TỪ LOADING SCREEN
        // ==================
        checkNoInternetFromLoading();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            extra_sound_manager.playMenuClick(this);

            int id = item.getItemId();
            if (id == R.id.nav_home) {
                switchFragment(fragmentHome);
                return true;
            } else if (id == R.id.nav_cinema) {
                // Mở tab Rạp phim gần đây
                switchFragment(fragmentCinema);
                return true;
            } else if (id == R.id.nav_mail) {
                switchFragment(fragmentMail);
                return true;
            } else if (id == R.id.nav_chat_bot) {
                // Mở ChatbotActivity thay vì hiển thị toast
                extra_sound_manager.playUiClick(this);
                startActivity(new Intent(this, activities_2_chatbot.class));
                return false; // Không thay đổi tab được chọn
            } else if (id == R.id.nav_notifications) {
                switchFragment(fragmentNotifications);
                return true;
            } else if (id == R.id.nav_user) {
                switchFragment(fragmentUser);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Quan trọng để getIntent() sau này lấy được data mới
        handleNavigationIntent(intent);
    }

    // Hàm điều hướng tab dựa trên dữ liệu từ TicketDetailActivity
    private void handleNavigationIntent(Intent intent) {
        String target = intent.getStringExtra("OPEN_FRAGMENT");
        if ("TICKET_FRAGMENT".equals(target)) {
            switchFragment(fragmentMail); // Dùng switchFragment
            bottomNavigationView.setSelectedItemId(R.id.nav_mail);
        }
    }

    /**
     * Kiểm tra xem có thông báo "không có mạng" từ Loading screen không
     * Nếu có thì hiển thị dialog trên màn hình Home (đẹp hơn)
     */
    private void checkNoInternetFromLoading() {
        boolean noInternet = getIntent().getBooleanExtra(activities_0_loading.EXTRA_NO_INTERNET, false);

        if (noInternet) {
            showNoInternetDialog();
        }
    }

    /**
     * Hiển thị hộp thoại thông báo không có kết nối mạng
     */
    private void showNoInternetDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_no_internet_title))
                .setMessage(getString(R.string.dialog_no_internet_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.retry), (dialog, which) -> {
                    extra_sound_manager.playUiClick(this);
                    dialog.dismiss();
                    // Khởi động lại app từ Loading screen để kiểm tra lại mạng
                    Intent intent = new Intent(this, activities_0_loading.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(getString(R.string.exit), (dialog, which) -> {
                    extra_sound_manager.playUiClick(this);
                    dialog.dismiss();
                    finishAffinity();
                })
                .show();
    }

    // Hàm tiện ích để load fragment
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
    private void initFragments() {
        fragmentHome = new fragments_home();
        fragmentCinema = new fragments_cinema();
        fragmentMail = new fragments_mail();
        fragmentNotifications = new fragments_notifications();
        fragmentUser = fragments_user.newInstance();

        // Add tất cả nhưng chỉ show Home
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragmentUser, "5").hide(fragmentUser).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragmentNotifications, "4").hide(fragmentNotifications).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragmentMail, "3").hide(fragmentMail).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragmentCinema, "2").hide(fragmentCinema).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragmentHome, "1").commit();

        activeFragment = fragmentHome;
    }

    // Hàm switch fragment thay thế cho loadFragment cũ
    public void switchFragment(Fragment target) {
        if (activeFragment == target) return;

        getSupportFragmentManager().beginTransaction()
                .hide(activeFragment)
                .show(target)
                .commit();
        activeFragment = target;
    }

    // Cho phép fragment chọn lại nav item
    public void selectBottomNavItem(int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);
    }

    @Override
    public void onBackPressed() {
        extra_sound_manager.playUiClick(this);
        super.onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        extra_sound_manager.playUiClick(this);
    }
}