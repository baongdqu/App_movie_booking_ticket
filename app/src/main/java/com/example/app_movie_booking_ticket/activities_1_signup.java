package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

/**
 * Activity Đăng ký (Sign Up)
 * Cho phép người dùng tạo tài khoản mới.
 * Lưu thông tin người dùng vào Firebase Realtime Database.
 * Hỗ trợ đăng ký qua Google - tự động điền email từ Google.
 */
public class activities_1_signup extends AppCompatActivity {

    private TextInputEditText inputFullName, inputEmailSignup, inputPasswordSignup, inputConfirmPassword, inputPhone;
    private TextInputLayout emailInputLayout;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    // Google Sign-In
    private extra_google_signin_helper googleSignInHelper;
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    private String googlePhotoUrl = ""; // Lưu URL ảnh từ Google nếu có

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
        ImageButton btnGoogleSignUp = findViewById(R.id.btnGoogleSignUp);

        // Nhận email từ Google nếu được chuyển từ Login
        handleGoogleEmailFromIntent();

        txtBackToLogin.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            startActivity(new Intent(activities_1_signup.this, activities_1_login.class));
            finish();
        });

        btnCreateAccount.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            registerUser();
        });

        // ================== 🔘 GOOGLE SIGN-UP ==================
        setupGoogleSignUp(btnGoogleSignUp);
    }

    /**
     * Xử lý email từ Google được truyền qua Intent từ màn hình Login
     */
    private void handleGoogleEmailFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String googleEmail = intent.getStringExtra("google_email");
            String googleName = intent.getStringExtra("google_name");
            googlePhotoUrl = intent.getStringExtra("google_photo");

            if (googleEmail != null && !googleEmail.isEmpty()) {
                inputEmailSignup.setText(googleEmail);
                inputEmailSignup.setEnabled(false); // Khóa trường email
                inputEmailSignup.setFocusable(false);

                // Đổi màu để indicate email bị khóa
                inputEmailSignup.setAlpha(0.7f);
            }

            if (googleName != null && !googleName.isEmpty()) {
                inputFullName.setText(googleName);
            }
        }
    }

    /**
     * Thiết lập Google Sign-Up
     */
    private void setupGoogleSignUp(ImageButton btnGoogleSignUp) {
        googleSignInHelper = new extra_google_signin_helper(this);

        // Đăng ký ActivityResultLauncher cho Google Sign-In
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        googleSignInHelper.handleSignInResultForSignup(result.getData(),
                                new extra_google_signin_helper.GoogleSignInCallback() {
                                    @Override
                                    public void onLoginSuccess(FirebaseUser user) {
                                        // Không sử dụng trong signup
                                    }

                                    @Override
                                    public void onNeedRegistration(String email, String displayName, String photoUrl) {
                                        // Điền thông tin vào form
                                        inputEmailSignup.setText(email);
                                        inputEmailSignup.setEnabled(false);
                                        inputEmailSignup.setFocusable(false);
                                        inputEmailSignup.setAlpha(0.7f);

                                        if (displayName != null && !displayName.isEmpty()) {
                                            inputFullName.setText(displayName);
                                        }

                                        googlePhotoUrl = photoUrl;

                                        Toast.makeText(activities_1_signup.this,
                                                getString(R.string.toast_google_account_linked),
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        extra_sound_manager.playError(activities_1_signup.this);
                                        Toast.makeText(activities_1_signup.this,
                                                String.format(getString(R.string.toast_google_signin_failed),
                                                        errorMessage),
                                                Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onCancelled() {
                                        Toast.makeText(activities_1_signup.this,
                                                getString(R.string.toast_google_signin_cancelled),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

        btnGoogleSignUp.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            googleSignInLauncher.launch(googleSignInHelper.getSignInIntent());
        });
    }

    /**
     * Xử lý logic đăng ký người dùng mới.
     * 1. Validate thông tin nhập (Tên, Email, Pass, Phone).
     * 2. Tạo tài khoản trên Firebase Auth.
     * 3. Lưu thông tin bổ sung vào Firebase Database.
     * 4. Gửi email xác thực.
     */
    private void registerUser() {
        String fullName = Objects.requireNonNull(inputFullName.getText()).toString().trim();
        String email = Objects.requireNonNull(inputEmailSignup.getText()).toString().trim();
        String password = Objects.requireNonNull(inputPasswordSignup.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(inputConfirmPassword.getText()).toString().trim();
        String phone = Objects.requireNonNull(inputPhone.getText()).toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                || TextUtils.isEmpty(confirmPassword)) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, getString(R.string.toast_fill_info), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, getString(R.string.toast_invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, getString(R.string.toast_password_6_chars), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, getString(R.string.toast_password_mismatch), Toast.LENGTH_SHORT).show();
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
                                extra_user user = new extra_user(uid, fullName, email, phone, "", "");

                                // Sử dụng ảnh từ Google nếu có, nếu không thì dùng ảnh mặc định
                                if (googlePhotoUrl != null && !googlePhotoUrl.isEmpty()) {
                                    user.setAvatarUrl(googlePhotoUrl);
                                } else {
                                    user.setAvatarUrl("https://i.ibb.co/C3JdHS1r/Avatar-trang-den.png");
                                }
                                user.setBalance(0);

                                usersRef.child(uid).setValue(user)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                firebaseUser.sendEmailVerification()
                                                        .addOnCompleteListener(verifyTask -> {
                                                            if (verifyTask.isSuccessful()) {
                                                                extra_sound_manager
                                                                        .playSuccess(activities_1_signup.this);
                                                                Toast.makeText(activities_1_signup.this,
                                                                        getString(R.string.toast_account_created),
                                                                        Toast.LENGTH_LONG).show();
                                                                mAuth.signOut();
                                                                startActivity(new Intent(activities_1_signup.this,
                                                                        activities_1_login.class));
                                                                finish();
                                                            } else {
                                                                extra_sound_manager.playError(activities_1_signup.this);
                                                                Toast.makeText(activities_1_signup.this,
                                                                        String.format(getString(
                                                                                R.string.toast_verification_send_error),
                                                                                Objects.requireNonNull(
                                                                                        verifyTask.getException())
                                                                                        .getMessage()),
                                                                        Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                            } else {
                                                extra_sound_manager.playError(activities_1_signup.this);
                                                Toast.makeText(activities_1_signup.this,
                                                        String.format(getString(R.string.toast_db_error),
                                                                Objects.requireNonNull(dbTask.getException())
                                                                        .getMessage()),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        } else {
                            extra_sound_manager.playError(activities_1_signup.this);
                            String errorMessage;
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthUserCollisionException) {
                                // Email đã được đăng ký bởi tài khoản khác
                                errorMessage = getString(R.string.toast_email_already_registered);
                            } else {
                                errorMessage = String.format(getString(R.string.toast_error),
                                        Objects.requireNonNull(exception).getMessage());
                            }
                            Toast.makeText(activities_1_signup.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
