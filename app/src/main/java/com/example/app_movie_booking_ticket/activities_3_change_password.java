package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class activities_3_change_password extends AppCompatActivity {

    private EditText inputOldPassword, inputNewPassword, inputConfirmPassword;
    private Button btnSavePassword, btnCancelPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra_themeutils.applySavedTheme(this);
        setContentView(R.layout.layouts_3_change_password);

        inputOldPassword = findViewById(R.id.inputOldPassword);
        inputNewPassword = findViewById(R.id.inputNewPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnSavePassword = findViewById(R.id.btnSavePassword);
        btnCancelPassword = findViewById(R.id.btnCancelPassword);

        mAuth = FirebaseAuth.getInstance();

        // Nút LƯU (Save) - Có âm thanh
        btnSavePassword.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            changePassword();
        });

        // Nút HỦY (Cancel) - ĐÃ BỔ SUNG ÂM THANH
        btnCancelPassword.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this); // Bổ sung âm thanh cho nút Hủy
            finish();
        });
    }

    private void changePassword() {
        String oldPass = inputOldPassword.getText().toString().trim();
        String newPass = inputNewPassword.getText().toString().trim();
        String confirmPass = inputConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 6) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || user.getEmail() == null) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, "Không xác định được người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), oldPass))
                .addOnSuccessListener(unused -> user.updatePassword(newPass)
                        .addOnSuccessListener(unused2 -> {
                            extra_sound_manager.playSuccess(this);
                            Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            extra_sound_manager.playError(this);
                            Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }))
                .addOnFailureListener(e -> {
                    extra_sound_manager.playError(this);
                    Toast.makeText(this, "Mật khẩu hiện tại không đúng!", Toast.LENGTH_SHORT).show();
                });
    }
}