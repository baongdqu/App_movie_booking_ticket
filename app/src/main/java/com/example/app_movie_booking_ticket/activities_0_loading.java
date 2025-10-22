package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.content.pm.ActivityInfo;   // ← thêm dòng import này
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_movie_booking_ticket.activities_1_login;
import com.example.app_movie_booking_ticket.R;

public class activities_0_loading extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.layouts_0_loading);

        // Giả lập thời gian loading (3 giây)
        new Handler().postDelayed(() -> {
            // Sau khi loading xong, chuyển sang màn hình đăng nhập
            startActivity(new Intent(this, activities_1_login.class));
            // Đoạn code thêm chút hiệu ứng
            // overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, 2000);
    }
}
