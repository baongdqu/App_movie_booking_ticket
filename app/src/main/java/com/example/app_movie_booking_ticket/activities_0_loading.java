package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler; // Cần import android.os.Handler (đã có)

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class activities_0_loading extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.layouts_0_loading);

        // ================== 🔊 SỬA: PHÁT ÂM THANH SAU 100MS ==================
        // Đảm bảo SoundPool có thời gian tải ID của file âm thanh
        new Handler().postDelayed(() -> {
            extra_sound_manager.playOpening(activities_0_loading.this);
        }, 500); // 100ms

        // Khởi tạo FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Delay chính (giả lập loading)
        // Lưu ý: Độ trễ này vẫn giữ nguyên 3000ms tính từ lúc onCreate bắt đầu.
        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null && currentUser.isEmailVerified()) {
                // Nếu đã đăng nhập và email đã verify -> vào thẳng Menu
                Intent intent = new Intent(activities_0_loading.this, activities_2_menu_manage_fragments.class);
                startActivity(intent);
                finish();
            } else {
                // Nếu không có user hoặc chưa verify -> đi đến Login
                // nếu user tồn tại nhưng chưa verify bạn cũng có thể signOut để sạch
                if (currentUser != null && !currentUser.isEmailVerified()) {
                    mAuth.signOut();
                }
                Intent intent = new Intent(activities_0_loading.this, activities_1_login.class);
                startActivity(intent);
                finish();
            }
        }, 7000);
    }
}