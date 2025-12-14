package com.example.app_movie_booking_ticket;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class activities_1_login extends extra_manager_language {

    // 🔹 Khai báo các thành phần giao diện
    private TextInputEditText inputEmail, inputPassword;

    // 🔹 Firebase Authentication
    private FirebaseAuth mAuth;

    // Giao diện
    private TextView btntxtForgotPassword;
    private TextView txtResendVerify; // mới

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Liên kết với layout
        setContentView(R.layout.layouts_1_login);

        // ================== 🔊 BỔ SUNG ÂM THANH MỞ GIAO DIỆN ==================
        extra_sound_manager.playOpeningApp(this);

        // ================== 🔧 KHỞI TẠO CÁC THÀNH PHẦN ==================
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnSignup = findViewById(R.id.btnSignup);
        btntxtForgotPassword = findViewById(R.id.btntxtForgotPassword);

        // Thành phần mới: TextView cho gửi lại xác minh
        txtResendVerify = findViewById(R.id.txtResendVerify);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // ================== 🌐 KIỂM TRA KẾT QUẢ MẠNG TỪ LOADING SCREEN
        // ==================
        checkNoInternetFromLoading();

        // ================== 🔘 SỰ KIỆN CLICK ĐĂNG NHẬP ==================
        btnLogin.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            loginUser();
        });
        // ================== 🔘 MỞ MÀN HÌNH ĐĂNG KÝ ==================
        btnSignup.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            Intent intent = new Intent(activities_1_login.this, activities_1_signup.class);
            startActivity(intent);
        });

        // ================== 🔘 QUÊN MẬT KHẨU (tuỳ chọn) ==================
        btntxtForgotPassword.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            startActivity(new Intent(activities_1_login.this, activities_1_forgot_password.class));
        });

        // ================== 🔘 GỬI LẠI EMAIL XÁC MINH (tại giao diện login)
        // ==================
        txtResendVerify.setOnClickListener(v -> {
            // Yêu cầu user đã nhập email + mật khẩu ở form
            String email = Objects.requireNonNull(inputEmail.getText()).toString().trim();
            String password = Objects.requireNonNull(inputPassword.getText()).toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                extra_sound_manager.playError(this);
                Toast.makeText(this, getString(R.string.toast_fill_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            // Đăng nhập tạm để có FirebaseUser và gửi email xác minh
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                if (user.isEmailVerified()) {
                                    Toast.makeText(activities_1_login.this,
                                            getString(R.string.toast_email_already_verified), Toast.LENGTH_LONG)
                                            .show();
                                    // optional: signOut vì chỉ đăng nhập tạm
                                    mAuth.signOut();
                                } else {
                                    // gửi email xác minh
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(verifyTask -> {
                                                if (verifyTask.isSuccessful()) {
                                                    Toast.makeText(activities_1_login.this,
                                                            getString(R.string.toast_verification_sent),
                                                            Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(activities_1_login.this,
                                                            String.format(
                                                                    getString(R.string.toast_verification_send_error),
                                                                    Objects.requireNonNull(verifyTask.getException())
                                                                            .getMessage()),
                                                            Toast.LENGTH_LONG).show();
                                                }
                                                // sign out sau khi gửi
                                                mAuth.signOut();
                                            });
                                }
                            } else {
                                Toast.makeText(activities_1_login.this, getString(R.string.toast_user_not_found),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(activities_1_login.this,
                                    String.format(getString(R.string.toast_login_failed),
                                            Objects.requireNonNull(task.getException()).getMessage()),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    // ==============================================================

    private void loginUser() {
        // Lấy dữ liệu từ input
        String email = Objects.requireNonNull(inputEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(inputPassword.getText()).toString().trim();

        // ======= Bước 1: Kiểm tra dữ liệu nhập =======
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, getString(R.string.toast_fill_all_fields), Toast.LENGTH_SHORT).show();
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
                                    extra_sound_manager.playSuccess(activities_1_login.this);
                                    Toast.makeText(activities_1_login.this, getString(R.string.toast_login_success),
                                            Toast.LENGTH_SHORT)
                                            .show();

                                    // 🔹 Lưu thông tin người dùng vào SharedPreferences
                                    getSharedPreferences("UserPrefs", MODE_PRIVATE)
                                            .edit()
                                            .putString("email", user.getEmail()) // lưu email từ Firebase
                                            .putString("username",
                                                    user.getDisplayName() != null ? user.getDisplayName()
                                                            : "Người dùng")
                                            .putString("uid", user.getUid()) // lưu UID nếu cần
                                            .apply();

                                    // 🔹 Chuyển sang màn hình Menu (hoặc màn hình người dùng)
                                    Intent intent = new Intent(activities_1_login.this,
                                            activities_2_a_menu_manage_fragments.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Nếu chưa verify → hiển thị dialog cho phép gửi lại email
                                    new MaterialAlertDialogBuilder(activities_1_login.this)
                                            .setTitle(getString(R.string.dialog_email_not_verified_title))
                                            .setMessage(getString(R.string.dialog_email_not_verified_message))
                                            .setPositiveButton(getString(R.string.dialog_resend_email),
                                                    (dialog, which) -> {
                                                        user.sendEmailVerification()
                                                                .addOnCompleteListener(verifyTask -> {
                                                                    if (verifyTask.isSuccessful()) {
                                                                        extra_sound_manager
                                                                                .playUiClick(activities_1_login.this); // Âm
                                                                                                                       // thanh
                                                                                                                       // cho
                                                                                                                       // hành
                                                                                                                       // động
                                                                                                                       // gửi
                                                                                                                       // lại
                                                                                                                       // email
                                                                                                                       // thành
                                                                                                                       // công
                                                                        Toast.makeText(activities_1_login.this,
                                                                                getString(
                                                                                        R.string.toast_verification_sent),
                                                                                Toast.LENGTH_LONG).show();
                                                                    } else {
                                                                        extra_sound_manager
                                                                                .playError(activities_1_login.this); // Âm
                                                                                                                     // thanh
                                                                                                                     // lỗi
                                                                                                                     // nếu
                                                                                                                     // gửi
                                                                                                                     // lại
                                                                                                                     // email
                                                                                                                     // thất
                                                                                                                     // bại
                                                                        Toast.makeText(activities_1_login.this,
                                                                                String.format(getString(
                                                                                        R.string.toast_verification_send_error),
                                                                                        Objects.requireNonNull(
                                                                                                verifyTask
                                                                                                        .getException())
                                                                                                .getMessage()),
                                                                                Toast.LENGTH_LONG).show();
                                                                    }
                                                                    // sign out sau khi gửi
                                                                    mAuth.signOut();
                                                                });
                                                    })
                                            .setNeutralButton(getString(R.string.dialog_open_email),
                                                    (dialog, which) -> {
                                                        extra_sound_manager.playUiClick(activities_1_login.this); // Âm
                                                                                                                  // thanh
                                                                                                                  // cho
                                                                                                                  // hành
                                                                                                                  // động
                                                                                                                  // mở
                                                                                                                  // email
                                                        // cố gắng mở ứng dụng email mặc định
                                                        Intent intent = new Intent(Intent.ACTION_MAIN);
                                                        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                                                        try {
                                                            startActivity(intent);
                                                        } catch (ActivityNotFoundException ex) {
                                                            Toast.makeText(activities_1_login.this,
                                                                    getString(R.string.toast_email_app_not_found),
                                                                    Toast.LENGTH_SHORT)
                                                                    .show();
                                                        }
                                                        // sign out user (vì chưa verified)
                                                        mAuth.signOut();
                                                    })
                                            .setNegativeButton(getString(R.string.dialog_close), (dialog, which) -> {
                                                extra_sound_manager.playUiClick(activities_1_login.this); // Âm thanh
                                                                                                          // cho hành
                                                                                                          // động đóng
                                                                                                          // dialog
                                                // sign out để dọn phiên
                                                mAuth.signOut();
                                                dialog.dismiss();
                                            })
                                            .setCancelable(false)
                                            .show();
                                }
                            }

                        } else {
                            // Nếu đăng nhập thất bại (sai mật khẩu, email không tồn tại, ...)
                            extra_sound_manager.playError(activities_1_login.this); // Âm thanh lỗi khi đăng nhập thất
                                                                                    // bại
                            Toast.makeText(activities_1_login.this,
                                    String.format(getString(R.string.toast_login_failed),
                                            Objects.requireNonNull(task.getException()).getMessage()),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Kiểm tra xem có thông báo "không có mạng" từ Loading screen không
     * Nếu có thì hiển thị dialog trên màn hình Login (đẹp hơn)
     */
    private void checkNoInternetFromLoading() {
        boolean noInternet = getIntent().getBooleanExtra(activities_0_loading.EXTRA_NO_INTERNET, false);

        if (noInternet) {
            showNoInternetDialog();
        }
    }

    /**
     * Hiển thị hộp thoại thông báo không có kết nối mạng
     */
    private void showNoInternetDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_no_internet_title))
                .setMessage(getString(R.string.dialog_no_internet_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.retry), (dialog, which) -> {
                    dialog.dismiss();
                    // Khởi động lại app từ Loading screen để kiểm tra lại mạng
                    Intent intent = new Intent(this, activities_0_loading.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(getString(R.string.exit), (dialog, which) -> {
                    dialog.dismiss();
                    finishAffinity();
                })
                .show();
    }
}