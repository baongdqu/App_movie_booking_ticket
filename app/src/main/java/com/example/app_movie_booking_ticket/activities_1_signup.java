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

public class activities_1_signup extends AppCompatActivity {

    private TextInputEditText inputFullName, inputEmailSignup, inputPasswordSignup, inputConfirmPassword, inputPhone;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.layouts_1_signup);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        inputFullName = findViewById(R.id.inputFullName);
        inputEmailSignup = findViewById(R.id.inputEmailSignup);
        inputPasswordSignup = findViewById(R.id.inputPasswordSignup);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        inputPhone = findViewById(R.id.inputPhone);
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);
        TextView txtBackToLogin = findViewById(R.id.txtBackToLogin);

        txtBackToLogin.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            startActivity(new Intent(activities_1_signup.this, activities_1_login.class));
            finish();
        });

        btnCreateAccount.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            registerUser();
        });
    }

    private void registerUser() {
        String fullName = Objects.requireNonNull(inputFullName.getText()).toString().trim();
        String email = Objects.requireNonNull(inputEmailSignup.getText()).toString().trim();
        String password = Objects.requireNonNull(inputPasswordSignup.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(inputConfirmPassword.getText()).toString().trim();
        String phone = Objects.requireNonNull(inputPhone.getText()).toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @NonNull
                    @Override
                    protected Object clone() throws CloneNotSupportedException {
                        return super.clone();
                    }

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String uid = firebaseUser.getUid();
                                extra_user user = new extra_user(uid, fullName, email, phone);
                                user.setAvatarUrl("https://i.ibb.co/C3JdHS1r/Avatar-trang-den.png");

                                usersRef.child(uid).setValue(user)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                firebaseUser.sendEmailVerification()
                                                        .addOnCompleteListener(verifyTask -> {
                                                            if (verifyTask.isSuccessful()) {
                                                                extra_sound_manager.playSuccess(activities_1_signup.this);
                                                                Toast.makeText(activities_1_signup.this,
                                                                        "Đăng ký thành công! Vui lòng kiểm tra email để xác minh tài khoản.",
                                                                        Toast.LENGTH_LONG).show();
                                                                mAuth.signOut();
                                                                startActivity(new Intent(activities_1_signup.this, activities_1_login.class));
                                                                finish();
                                                            } else {
                                                                extra_sound_manager.playError(activities_1_signup.this);
                                                                Toast.makeText(activities_1_signup.this,
                                                                        "Không gửi được email xác minh: " + Objects.requireNonNull(verifyTask.getException()).getMessage(),
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                            } else {
                                                extra_sound_manager.playError(activities_1_signup.this);
                                                Toast.makeText(activities_1_signup.this,
                                                        "Lỗi lưu dữ liệu: " + Objects.requireNonNull(dbTask.getException()).getMessage(),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        } else {
                            extra_sound_manager.playError(activities_1_signup.this);
                            Toast.makeText(activities_1_signup.this,
                                    "Đăng ký thất bại: " + Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
