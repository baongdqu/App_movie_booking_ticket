package com.example.app_movie_booking_ticket;

import android.content.Intent; // ✅ thêm dòng này
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class activities_2_menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layouts_2_menu);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        FloatingActionButton btnTrailer = findViewById(R.id.btnTrailer);

        // Khi nhấn các nút điều hướng
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(this, "Trang chủ", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_mail) {
                Toast.makeText(this, "Hộp thư", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_notifications) {
                Toast.makeText(this, "Thông báo", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_user) {
                Intent intent = new Intent(this, activities_3_user.class);
                startActivity(intent); // dùng hàm thật
            }
            return true;
        });

        // Nút trailer ở giữa
        btnTrailer.setOnClickListener(v -> {
            Toast.makeText(this, "Giới thiệu phim mới!", Toast.LENGTH_SHORT).show();
            // TODO: mở Activity trailer, hoặc phát video
        });
    }
}
