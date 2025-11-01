package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class activities_3_advanced_settings extends AppCompatActivity {

    private Switch switchDarkMode, switchNotification;
    private Button btnChangePassword, btnDeleteAccount, btnBackSettings;

    private SharedPreferences prefs;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra_themeutils.applySavedTheme(this);
        setContentView(R.layout.layouts_3_advanced_settings);

        // Ánh xạ
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchNotification = findViewById(R.id.switchNotification);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnBackSettings = findViewById(R.id.btnBackSettings);

        mAuth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);

        switchDarkMode.setChecked(extra_themeutils.isDarkMode(this));
        switchNotification.setChecked(prefs.getBoolean("notifications", true));

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            extra_themeutils.setDarkMode(this, isChecked);
            recreate();
        });

        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications", isChecked).apply();
            Toast.makeText(this, isChecked ? "Thông báo đã bật" : "Thông báo đã tắt", Toast.LENGTH_SHORT).show();
        });

        btnChangePassword.setOnClickListener(v ->
                startActivity(new Intent(this, activities_3_change_password.class))
        );

        // ⚠️ Xóa tài khoản
        btnDeleteAccount.setOnClickListener(v -> {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

            new MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
                    .setIcon(R.drawable.ic_warning_red)
                    .setTitle("⚠️ Xác nhận xóa tài khoản")
                    .setMessage("Hành động này sẽ xóa vĩnh viễn tài khoản và toàn bộ dữ liệu liên quan. Bạn có chắc chắn muốn tiếp tục?")
                    .setPositiveButton("Xóa", (dialog, which) -> {

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user == null) {
                            Toast.makeText(this, "Không xác định được người dùng.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Tạo layout chứa input + icon xem/ẩn mật khẩu
                        TextInputLayout layout = new TextInputLayout(this, null,
                                com.google.android.material.R.style.Widget_Material3_TextInputLayout_OutlinedBox);
                        layout.setHint("Nhập mật khẩu để xác nhận");
                        // dùng built-in password toggle
                        layout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);

                        final TextInputEditText input = new TextInputEditText(this);

                        // --- Mặc định: CHE mật khẩu (chấm ●●●)
                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        input.setTransformationMethod(PasswordTransformationMethod.getInstance());

                        // thêm view và đảm bảo icon KHỞI TẠO ở trạng thái "nhắm" (ẩn)
                        layout.addView(input);
                        layout.setEndIconActivated(false); // giữ icon ở trạng thái ẩn ban đầu

                        // Không xuống dòng, Enter = xác nhận
                        input.setSingleLine(true);
                        input.setImeOptions(EditorInfo.IME_ACTION_DONE);

                        // Đảm bảo khi bấm icon mắt, ta đồng bộ transformation (show/hide)
                        layout.setEndIconOnClickListener(view -> {
                            // Nếu hiện đang bị che -> show; ngược lại -> che
                            if (input.getTransformationMethod() instanceof PasswordTransformationMethod) {
                                // chuyển sang hiện mật khẩu (plain text)
                                input.setTransformationMethod(null);
                                layout.setEndIconActivated(true);
                            } else {
                                // chuyển sang che mật khẩu
                                input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                layout.setEndIconActivated(false);
                            }
                            // giữ con trỏ cuối cùng
                            if (input.getText() != null) input.setSelection(input.getText().length());
                        });

                        // Show dialog xác thực
                        new MaterialAlertDialogBuilder(this)
                                .setTitle("Xác thực lại tài khoản")
                                .setView(layout)
                                .setPositiveButton("Xác nhận", (d2, w2) -> {
                                    String password = input.getText() != null ? input.getText().toString().trim() : "";
                                    dialogConfirmDelete(user, password);
                                })
                                .setNegativeButton("Hủy", (d2, w2) -> d2.dismiss())
                                .show();

                        // Enter = Xác nhận
                        input.setOnEditorActionListener((v1, actionId, event) -> {
                            if (actionId == EditorInfo.IME_ACTION_DONE) {
                                String password = input.getText() != null ? input.getText().toString().trim() : "";
                                dialogConfirmDelete(user, password);
                                return true;
                            }
                            return false;
                        });

                    })
                    .setNegativeButton("Hủy", (dialog, which2) -> {
                        if (vibrator != null) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                                vibrator.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE));
                            else vibrator.vibrate(80);
                        }
                        dialog.dismiss();
                    })
                    .setCancelable(true)
                    .show();

            if (vibrator != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                else vibrator.vibrate(100);
            }
        });

        btnBackSettings.setOnClickListener(v -> finish());
    }

    private void dialogConfirmDelete(FirebaseUser user, String password) {
        if (password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu.", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = user.getEmail();
        if (email == null) {
            Toast.makeText(this, "Không có email để xác thực.", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        user.reauthenticate(credential)
                .addOnSuccessListener(unused -> {

                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                    usersRef.child(user.getUid()).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                user.delete()
                                        .addOnSuccessListener(unused2 -> {

                                            getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();
                                            FirebaseAuth.getInstance().signOut();

                                            Toast.makeText(this, "Đã xóa tài khoản thành công!", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(this, activities_1_login.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(this, "Không thể xóa tài khoản: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                        );
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Lỗi khi xóa dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Mật khẩu không đúng hoặc phiên đăng nhập đã hết hạn!", Toast.LENGTH_LONG).show()
                );
    }
}
