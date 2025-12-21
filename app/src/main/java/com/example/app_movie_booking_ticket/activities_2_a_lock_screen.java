package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Activity Màn hình khóa (Lock Screen)
 * Yêu cầu người dùng nhập mã PIN 6 số để mở khóa ứng dụng hoặc thực hiện tác vụ
 * nhạy cảm.
 * Hỗ trợ các chức năng: Nhập PIN, Xóa PIN, Quên mã PIN.
 */
public class activities_2_a_lock_screen extends extra_manager_language {

    private ImageView[] dots;
    private StringBuilder currentPin = new StringBuilder();
    private String storedPin;
    private static final int PIN_LENGTH = 6;

    // Intent target to launch after unlock
    private Intent targetIntent;

    /**
     * Khởi tạo giao diện Lock Screen.
     * Kiểm tra mã PIN đã lưu và thiết lập bàn phím số.
     *
     * @param savedInstanceState Bundle trạng thái đã lưu
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra_themeutils.applySavedTheme(this);
        setContentView(R.layout.layouts_2_a_lock_screen);

        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        storedPin = prefs.getString("app_pin", "");

        // Get target intent
        if (getIntent().hasExtra("target_intent")) {
            targetIntent = getIntent().getParcelableExtra("target_intent");
        }

        // Init Dots
        dots = new ImageView[PIN_LENGTH];
        dots[0] = findViewById(R.id.dot1);
        dots[1] = findViewById(R.id.dot2);
        dots[2] = findViewById(R.id.dot3);
        dots[3] = findViewById(R.id.dot4);
        dots[4] = findViewById(R.id.dot5);
        dots[5] = findViewById(R.id.dot6);

        // Init Keypad
        setupKeypad();

        // Forgot PIN
        TextView txtForgotPin = findViewById(R.id.txtForgotPin);
        txtForgotPin.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);

            // Xóa PIN Settings
            prefs.edit().remove("pin_enabled").remove("app_pin").apply();
            // Xóa User Settings nếu cần (UserPrefs) - tuỳ chọn, nhưng tốt nhất nên clear
            // hết session
            getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().clear().apply();

            // Sign out and go to login
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, activities_1_login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(this, getString(R.string.toast_session_expired), Toast.LENGTH_SHORT).show();
        });
    }

    private void setupKeypad() {
        int[] btnIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        for (int id : btnIds) {
            Button btn = findViewById(id);
            btn.setOnClickListener(v -> {
                extra_sound_manager.playUiClick(this);
                addDigit(btn.getText().toString());
            });
        }

        ImageButton btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            deleteDigit();
        });
    }

    private void addDigit(String digit) {
        if (currentPin.length() < PIN_LENGTH) {
            currentPin.append(digit);
            updateDots();

            if (currentPin.length() == PIN_LENGTH) {
                verifyPin();
            }
        }
    }

    private void deleteDigit() {
        if (currentPin.length() > 0) {
            currentPin.deleteCharAt(currentPin.length() - 1);
            updateDots();
        }
    }

    private void updateDots() {
        for (int i = 0; i < PIN_LENGTH; i++) {
            if (i < currentPin.length()) {
                dots[i].setImageResource(R.drawable.shape_pin_dot_filled);
            } else {
                dots[i].setImageResource(R.drawable.shape_pin_dot_empty);
            }
        }
    }

    private void verifyPin() {
        if (currentPin.toString().equals(storedPin)) {
            // Correct logic
            extra_sound_manager.playSuccess(this);
            unlockApp();
        } else {
            // Incorrect logic
            extra_sound_manager.playError(this);
            shakeAnimations();
            vibrateError();
            currentPin.setLength(0);
            updateDots();
            Toast.makeText(this, getString(R.string.toast_pin_wrong), Toast.LENGTH_SHORT).show();
        }
    }

    private void unlockApp() {
        if (targetIntent != null) {
            startActivity(targetIntent);
        } else {
            // Default fallback
            startActivity(new Intent(this, activities_2_a_menu_manage_fragments.class));
        }
        finish();
    }

    private void shakeAnimations() {
        Animation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setInterpolator(new android.view.animation.CycleInterpolator(5));
        shake.setDuration(300);
        findViewById(R.id.layoutPinDots).startAnimation(shake);
    }

    private void vibrateError() {
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (v != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(200);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Prevent backing out of lock screen
        super.onBackPressed();
        finishAffinity(); // Close app if user tries to back out
    }
}
