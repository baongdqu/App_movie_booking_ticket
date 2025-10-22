package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class activities_1_login extends AppCompatActivity {

    // 🔹 Khai báo các thành phần giao diện
    private TextInputEditText inputEmail, inputPassword;

    // 🔹 Firebase Authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Liên kết với layout
        setContentView(R.layout.layouts_1_login);

        // ================== 🔧 KHỞI TẠO CÁC THÀNH PHẦN ==================
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnSignup = findViewById(R.id.btnSignup);
        TextView btntxtForgotPassword = findViewById(R.id.btntxtForgotPassword);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // ================== 🔘 SỰ KIỆN CLICK ĐĂNG NHẬP ==================
        btnLogin.setOnClickListener(v -> loginUser());

        // ================== 🔘 MỞ MÀN HÌNH ĐĂNG KÝ ==================
        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(activities_1_login.this, activities_1_signup.class);
            startActivity(intent);
        });

        // ================== 🔘 QUÊN MẬT KHẨU (tuỳ chọn) ==================
        btntxtForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(activities_1_login.this, activities_1_forgot_password.class));
        });
    }

    // ==============================================================
    // 🧠 HÀM XỬ LÝ ĐĂNG NHẬP
    // ==============================================================

    private void loginUser() {
        // Lấy dữ liệu từ input
        String email = Objects.requireNonNull(inputEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(inputPassword.getText()).toString().trim();

        // ======= Bước 1: Kiểm tra dữ liệu nhập =======
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập email và mật khẩu!", Toast.LENGTH_SHORT).show();
            return;
        }

        // ======= Bước 2: Gửi yêu cầu đăng nhập đến Firebase Auth =======
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Nếu đăng nhập thành công
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                // ======= Bước 3: Kiểm tra email đã verify chưa =======
                                if (user.isEmailVerified()) {
                                    Toast.makeText(activities_1_login.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                                    // ======= Bước 4: Chuyển sang màn hình Menu =======
                                    Intent intent = new Intent(activities_1_login.this, activities_2_menu.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Nếu chưa verify → không cho vào
                                    Toast.makeText(activities_1_login.this,
                                            "Vui lòng xác minh email trước khi đăng nhập!",
                                            Toast.LENGTH_LONG).show();
                                    mAuth.signOut();
                                }
                            }

                        } else {
                            // Nếu đăng nhập thất bại (sai mật khẩu, email không tồn tại, ...)
                            Toast.makeText(activities_1_login.this,
                                    "Đăng nhập thất bại: " +
                                            Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
