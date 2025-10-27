package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.Toast;

public class activities_2_menu_manage_fragments extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layouts_2_menu_manage_fragments);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        FloatingActionButton btnTrailer = findViewById(R.id.btnTrailer);

        // Load fragment mặc định (Home)
        loadFragment(new fragments_home());
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadFragment(new fragments_home());
            } else if (id == R.id.nav_mail) {
                loadFragment(new fragments_mail());
            } else if (id == R.id.nav_notifications) {
                loadFragment(new fragments_notifications());
            } else if (id == R.id.nav_user) {
                loadFragment(fragments_user.newInstance());
            }
            return true;
        });

        btnTrailer.setOnClickListener(v -> {
            Toast.makeText(this, "Giới thiệu phim mới!", Toast.LENGTH_SHORT).show();
            // TODO: mở Activity trailer, hoặc phát video
        });
    }

    // Hàm tiện ích để load fragment
    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    // Hàm để cho fragment gọi lại khi cần chọn lại nav item
    public void selectBottomNavItem(int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);
    }
}
