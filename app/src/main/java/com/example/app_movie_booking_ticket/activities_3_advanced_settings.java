package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class activities_3_advanced_settings extends AppCompatActivity {

    private Switch switchDarkMode, switchNotification;
    private Button btnChangePassword, btnDeleteAccount, btnBackSettings;

    private SharedPreferences prefs;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layouts_3_advanced_settings);

        // Ánh xạ
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchNotification = findViewById(R.id.switchNotification);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnBackSettings = findViewById(R.id.btnBackSettings);

        mAuth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // 🔘 Load trạng thái đã lưu
        switchDarkMode.setChecked(prefs.getBoolean("dark_mode", false));
        switchNotification.setChecked(prefs.getBoolean("notifications", true));

        // 🎨 Đổi chế độ sáng/tối
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // 🔔 Bật/tắt thông báo
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications", isChecked).apply();
            Toast.makeText(this, isChecked ? "Thông báo đã bật" : "Thông báo đã tắt", Toast.LENGTH_SHORT).show();
        });

        // 🔑 Đổi mật khẩu (gửi email reset)
        btnChangePassword.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null && user.getEmail() != null) {
                mAuth.sendPasswordResetEmail(user.getEmail())
                        .addOnSuccessListener(unused -> Toast.makeText(this, "Đã gửi email đặt lại mật khẩu!", Toast.LENGTH_LONG).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });

        // ⚠️ Xóa tài khoản
        btnDeleteAccount.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                FirebaseDatabase.getInstance().getReference("users").child(uid).removeValue();
                user.delete()
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Tài khoản đã bị xóa!", Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                            startActivity(new Intent(this, activities_1_login.class));
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Không thể xóa: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });

        // 🔙 Quay lại
        btnBackSettings.setOnClickListener(v -> finish());
    }
}
