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
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class activities_1_forgot_password extends AppCompatActivity {

    private TextInputEditText inputEmailForgot;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.layouts_1_forgot_password);

        // Ánh xạ view
        inputEmailForgot = findViewById(R.id.inputEmailForgot);
        Button btnResetPassword = findViewById(R.id.btnResetPassword);
        TextView txtBackToLogin = findViewById(R.id.txtBackToLogin);

        mAuth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            resetPassword();
        });

        txtBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(activities_1_forgot_password.this, activities_1_login.class));
            finish();
        });
    }

    private void resetPassword() {
        String email = Objects.requireNonNull(inputEmailForgot.getText()).toString().trim();

        if (TextUtils.isEmpty(email)) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, getString(R.string.toast_enter_email_to_reset), Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            extra_sound_manager.playSuccess(activities_1_forgot_password.this);
                            Toast.makeText(activities_1_forgot_password.this,
                                    getString(R.string.toast_email_sent),
                                    Toast.LENGTH_LONG).show();

                            startActivity(new Intent(activities_1_forgot_password.this, activities_1_login.class));
                            finish();
                        } else {
                            extra_sound_manager.playError(activities_1_forgot_password.this);
                            Toast.makeText(activities_1_forgot_password.this,
                                    String.format(getString(R.string.toast_email_error),
                                            Objects.requireNonNull(task.getException()).getMessage()),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
