package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.widget.Toast;

public class activities_2_menu_manage_fragments extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra_themeutils.applySavedTheme(this);
        setContentView(R.layout.layouts_2_menu_manage_fragments);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load fragment mặc định (Home)
        loadFragment(new fragments_home());
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            extra_sound_manager.playMenuClick(this);

            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadFragment(new fragments_home());
                return true;
            } else if (id == R.id.nav_mail) {
                loadFragment(new fragments_mail());
                return true;
            } else if (id == R.id.nav_chat_bot) {
                Toast.makeText(this, "Tính năng chatbot sắp xuất hiện!", Toast.LENGTH_SHORT).show();
                return false; // Không thay đổi tab được chọn
            } else if (id == R.id.nav_notifications) {
                loadFragment(new fragments_notifications());
                return true;
            } else if (id == R.id.nav_user) {
                loadFragment(fragments_user.newInstance());
                return true;
            }
            return false;
        });
    }

    // Hàm tiện ích để load fragment
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    // Cho phép fragment chọn lại nav item
    public void selectBottomNavItem(int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);
    }
}