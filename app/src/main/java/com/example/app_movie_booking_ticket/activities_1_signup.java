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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

/**
 * Màn hình Đăng ký tài khoản người dùng
 * -----------------------------------------------------
 * - Cho phép người dùng nhập họ tên, email, mật khẩu, số điện thoại
 * - Kiểm tra hợp lệ đầu vào
 * - Tạo tài khoản Firebase Authentication
 * - Lưu thông tin người dùng vào Firebase Realtime Database
 * - Gán ảnh đại diện mặc định (từ dịch vụ lưu ảnh miễn phí như ImgBB)
 * - Gửi email xác minh tài khoản
 */
public class activities_1_signup extends AppCompatActivity {

    // -------------------------
    // KHAI BÁO BIẾN GIAO DIỆN
    // -------------------------
    private TextInputEditText inputFullName, inputEmailSignup, inputPasswordSignup, inputConfirmPassword, inputPhone;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.layouts_1_signup);

        // -------------------------
        // KHỞI TẠO FIREBASE
        // -------------------------
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // -------------------------
        // ÁNH XẠ CÁC VIEW TRONG GIAO DIỆN
        // -------------------------
        inputFullName = findViewById(R.id.inputFullName);
        inputEmailSignup = findViewById(R.id.inputEmailSignup);
        inputPasswordSignup = findViewById(R.id.inputPasswordSignup);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        inputPhone = findViewById(R.id.inputPhone);
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
        TextView txtBackToLogin = findViewById(R.id.txtBackToLogin);

        // -------------------------
        // XỬ LÝ NÚT "QUAY LẠI ĐĂNG NHẬP"
        // -------------------------
        txtBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(activities_1_signup.this, activities_1_login.class));
            finish();
        });

        // -------------------------
        // XỬ LÝ NÚT "TẠO TÀI KHOẢN"
        // -------------------------
        btnCreateAccount.setOnClickListener(v -> registerUser());
    }

    /**
     * Hàm xử lý đăng ký người dùng
     */
    private void registerUser() {
        // Lấy dữ liệu người dùng nhập vào
        String fullName = Objects.requireNonNull(inputFullName.getText()).toString().trim();
        String email = Objects.requireNonNull(inputEmailSignup.getText()).toString().trim();
        String password = Objects.requireNonNull(inputPasswordSignup.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(inputConfirmPassword.getText()).toString().trim();
        String phone = Objects.requireNonNull(inputPhone.getText()).toString().trim();

        // -------------------------
        // KIỂM TRA HỢP LỆ ĐẦU VÀO
        // -------------------------
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        // -------------------------
        // TẠO TÀI KHOẢN TRÊN FIREBASE AUTH
        // -------------------------
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String uid = firebaseUser.getUid();

                                // -------------------------
                                // TẠO ĐỐI TƯỢNG NGƯỜI DÙNG
                                // -------------------------
                                extra_user user = new extra_user(uid, fullName, email, phone);

                                // -------------------------
                                // GÁN ẢNH ĐẠI DIỆN MẶC ĐỊNH
                                // -------------------------
                                // 👉 Thay URL bên dưới bằng link ảnh thật của bạn (ImgBB, Cloudinary, v.v.)
                                user.setAvatarUrl("https://i.ibb.co/C3JdHS1r/Avatar-trang-den.png");

                                // -------------------------
                                // LƯU THÔNG TIN NGƯỜI DÙNG VÀO REALTIME DATABASE
                                // -------------------------
                                usersRef.child(uid).setValue(user)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                // -------------------------
                                                // GỬI EMAIL XÁC MINH TÀI KHOẢN
                                                // -------------------------
                                                firebaseUser.sendEmailVerification()
                                                        .addOnCompleteListener(verifyTask -> {
                                                            if (verifyTask.isSuccessful()) {
                                                                Toast.makeText(activities_1_signup.this,
                                                                        "Đăng ký thành công! Vui lòng kiểm tra email để xác minh tài khoản.",
                                                                        Toast.LENGTH_LONG).show();

                                                                // Đăng xuất để chờ xác minh email
                                                                mAuth.signOut();

                                                                // Quay lại màn hình đăng nhập
                                                                startActivity(new Intent(activities_1_signup.this, activities_1_login.class));
                                                                finish();
                                                            } else {
                                                                Toast.makeText(activities_1_signup.this,
                                                                        "Không gửi được email xác minh: " +
                                                                                Objects.requireNonNull(verifyTask.getException()).getMessage(),
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(activities_1_signup.this,
                                                        "Lỗi lưu dữ liệu: " +
                                                                Objects.requireNonNull(dbTask.getException()).getMessage(),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(activities_1_signup.this,
                                    "Đăng ký thất bại: " +
                                            Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
