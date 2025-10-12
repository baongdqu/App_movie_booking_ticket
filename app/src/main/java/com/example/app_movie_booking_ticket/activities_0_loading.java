package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activities_0_loading extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.layouts_0_loading);

        // Áp dụng padding cho edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 👇 Đợi 3 giây rồi chuyển sang màn hình login
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(activities_0_loading.this, activities_1_login.class);
            startActivity(intent);
            finish(); // Kết thúc màn hình loading để không quay lại được
        }, 3000); // 3000ms = 3 giây
    }
}