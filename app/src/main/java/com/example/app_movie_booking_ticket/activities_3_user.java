package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class activities_3_user extends AppCompatActivity {

    private TextView txtUsername, txtEmail;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layouts_3_user);

        // 🔹 Ánh xạ view
        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageView imgAvatar = findViewById(R.id.imgAvatar);
        txtUsername = findViewById(R.id.txtUsername);
        txtEmail = findViewById(R.id.txtEmail);
        Button btnEditProfile = findViewById(R.id.btnEditProfile);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnLogout = findViewById(R.id.btnLogout);

        // 🔹 Firebase
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference("users");

        // 🔹 SharedPreferences
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // ✅ Kiểm tra nếu chưa đăng nhập → quay về màn hình login
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
            return;
        }

        // 🔹 Lấy email từ SharedPreferences (ưu tiên)
        String email = prefs.getString("email", null);
        if (email == null) {
            // nếu không có trong SharedPreferences → lấy từ FirebaseAuth
            email = currentUser.getEmail();
            if (email != null) {
                prefs.edit().putString("email", email).apply();
            }
        }

        // 🔹 Hiển thị email
        txtEmail.setText(email != null ? email : "Không có email");

        // 🔹 Gọi hàm tải dữ liệu người dùng
        if (email != null) {
            loadUserData(email);
        } else {
            txtUsername.setText("Người dùng");
        }

        // 🔙 Nút quay lại menu
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(activities_3_user.this, activities_2_menu.class);
            startActivity(intent);
            finish();
        });

        // ✏️ Chỉnh sửa thông tin
        btnEditProfile.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng chỉnh sửa thông tin (chưa hoạt động)", Toast.LENGTH_SHORT).show()
        );

        // ⚙️ Cài đặt tài khoản
        btnSettings.setOnClickListener(v ->
                Toast.makeText(this, "Mở cài đặt tài khoản (chưa hoạt động)", Toast.LENGTH_SHORT).show()
        );

        // 🚪 Đăng xuất
        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();

            // Xóa thông tin đã lưu
            prefs.edit().clear().apply();

            // Đăng xuất khỏi Firebase
            mAuth.signOut();

            // Quay lại màn hình đăng nhập
            redirectToLogin();
        });
    }

    private void loadUserData(String email) {
        ref.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                String username = child.child("fullName").getValue(String.class);
                                txtUsername.setText(username != null ? username : "Người dùng");
                            }
                        } else {
                            txtUsername.setText("Không tìm thấy người dùng");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(activities_3_user.this,
                                "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(activities_3_user.this, activities_1_login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
