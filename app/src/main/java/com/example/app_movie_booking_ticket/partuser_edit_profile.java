package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import com.google.firebase.FirebaseException;
import com.google.firebase.database.*;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import android.widget.EditText;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MultipartBody;

import com.bumptech.glide.Glide;
import android.util.Log;

public class partuser_edit_profile extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private TextInputEditText inputFullName, inputPhone, inputDob, inputGender;
    private Button btnSave, btnCancel, btnChangeAvatar;
    private ImageView imgAvatar, imgPhoneStatus;
    private Button btnVerifyPhone;
    private String currentPhone = "";

    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;

    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private Uri selectedImageUri = null;
    private final String DEFAULT_AVATAR_URL = "https://i.ibb.co/C3JdHS1r/Avatar-trang-den.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra_themeutils.applySavedTheme(this);
        setContentView(R.layout.partuser_edit_profile);

        inputFullName = findViewById(R.id.inputFullNameEdit);
        inputPhone = findViewById(R.id.inputPhoneEdit);
        inputDob = findViewById(R.id.inputDobEdit);
        inputGender = findViewById(R.id.inputGenderEdit);
        btnSave = findViewById(R.id.btnSaveProfile);
        btnCancel = findViewById(R.id.btnCancelProfile);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        imgAvatar = findViewById(R.id.imgAvatar);
        imgPhoneStatus = findViewById(R.id.imgPhoneStatus);
        btnVerifyPhone = findViewById(R.id.btnVerifyPhone);

        setupGenderDropdown();
        setupDatePicker();
        setupPhoneAuthCallbacks();

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String uid = prefs.getString("uid", null);
        if (uid == null && mAuth.getCurrentUser() != null)
            uid = mAuth.getCurrentUser().getUid();
        if (uid == null) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, getString(R.string.toast_user_not_found), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String finalUid = uid;

        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    currentPhone = snapshot.child("phone").getValue(String.class);
                    String phone = currentPhone;

                    // Logic Sync: Compare DB phone vs Auth Phone
                    String authPhone = getLinkedPhoneNumber();
                    boolean verified = false;

                    if (phone != null && !phone.isEmpty() && authPhone != null) {
                        String normalizedDbPhone = normalizePhoneNumber(phone);
                        if (normalizedDbPhone.equals(authPhone)) {
                            verified = true;
                        }
                    }

                    inputFullName.setText(fullName != null ? fullName : "");
                    inputPhone.setText(phone != null ? phone : "");

                    imgPhoneStatus.setVisibility(android.view.View.VISIBLE);
                    if (verified) {
                        imgPhoneStatus.setImageResource(R.drawable.ic_check);
                        imgPhoneStatus.setColorFilter(getResources().getColor(R.color.netflix_green));
                        btnVerifyPhone.setVisibility(android.view.View.GONE);
                    } else {
                        imgPhoneStatus.setImageResource(R.drawable.ic_close);
                        imgPhoneStatus.setColorFilter(getResources().getColor(android.R.color.holo_red_dark)); // Use
                                                                                                               // red
                                                                                                               // explicitly
                        btnVerifyPhone.setVisibility(android.view.View.VISIBLE);
                    }
                    String dob = snapshot.child("dateOfBirth").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);
                    String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);

                    inputFullName.setText(fullName != null ? fullName : "");
                    inputPhone.setText(phone != null ? phone : "");
                    inputDob.setText(dob != null ? dob : "");
                    inputGender.setText(gender != null ? gender : "");

                    if (avatarUrl == null || avatarUrl.isEmpty()) {
                        Map<String, Object> defaultAvatar = new HashMap<>();
                        defaultAvatar.put("avatarUrl", DEFAULT_AVATAR_URL);
                        usersRef.child(finalUid).updateChildren(defaultAvatar)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        extra_sound_manager.playSuccess(partuser_edit_profile.this);
                                        Toast.makeText(partuser_edit_profile.this,
                                                getString(R.string.toast_default_avatar_set),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        extra_sound_manager.playError(partuser_edit_profile.this);
                                        Toast.makeText(partuser_edit_profile.this,
                                                String.format(getString(R.string.toast_default_avatar_error),
                                                        Objects.requireNonNull(task.getException()).getMessage()),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                        avatarUrl = DEFAULT_AVATAR_URL;
                    }

                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(partuser_edit_profile.this)
                                .load(avatarUrl)
                                .placeholder(R.drawable.ic_person)
                                .error(R.drawable.ic_person)
                                .circleCrop()
                                .into(imgAvatar);
                    } else {
                        imgAvatar.setImageResource(R.drawable.ic_person);
                    }
                } else {
                    extra_sound_manager.playError(partuser_edit_profile.this);
                    Toast.makeText(partuser_edit_profile.this, getString(R.string.toast_user_data_not_found),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                extra_sound_manager.playError(partuser_edit_profile.this);
                Toast.makeText(partuser_edit_profile.this,
                        String.format(getString(R.string.toast_load_error), error.getMessage()),
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnChangeAvatar.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setPackage("com.google.android.apps.photos");
            try {
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            } catch (android.content.ActivityNotFoundException e) {
                Intent fallback = new Intent(Intent.ACTION_GET_CONTENT);
                fallback.setType("image/*");
                fallback.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(fallback, "Chọn ảnh"), PICK_IMAGE_REQUEST);
            }
        });

        // inputPhone.setFocusable(true);

        btnVerifyPhone.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);

            String inputNumber = Objects.requireNonNull(inputPhone.getText()).toString().trim();
            if (inputNumber.isEmpty()) {
                Toast.makeText(this, getString(R.string.toast_enter_phone), Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if phone was changed but not verified
            if (inputNumber.equals(currentPhone) && imgPhoneStatus.getDrawable().getConstantState() == getResources()
                    .getDrawable(R.drawable.ic_check).getConstantState()) {
                Toast.makeText(this, getString(R.string.toast_phone_verified), Toast.LENGTH_SHORT).show();
                return;
            }

            // Normalizing phone number for Firebase (e.g., +84)
            String formattedPhone = inputNumber;
            if (formattedPhone.startsWith("0")) {
                formattedPhone = "+84" + formattedPhone.substring(1);
            } else if (!formattedPhone.startsWith("+")) {
                formattedPhone = "+84" + formattedPhone;
            }

            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(formattedPhone) // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this) // Activity (for callback binding)
                    .setCallbacks(mCallbacks) // OnVerificationStateChangedCallbacks
                    .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
            Toast.makeText(this, String.format(getString(R.string.toast_sending_otp), formattedPhone),
                    Toast.LENGTH_SHORT).show();
        });

        btnSave.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            String newName = Objects.requireNonNull(inputFullName.getText()).toString().trim();
            String newPhone = Objects.requireNonNull(inputPhone.getText()).toString().trim();
            String newDob = Objects.requireNonNull(inputDob.getText()).toString().trim();
            String newGender = Objects.requireNonNull(inputGender.getText()).toString().trim();

            if (TextUtils.isEmpty(newName)) {
                extra_sound_manager.playError(this);
                Toast.makeText(this, getString(R.string.toast_enter_name), Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("fullName", newName);

            if (!newPhone.equals(currentPhone)) {
                // User is saving a DIFFERENT phone number (likely unverified)
                // We must UNLINK the old verified phone from Auth (if exists) -> "biến mất cùng
                // với tài khoản"

                // Note: We blindly try to unlink. If it's not linked, it might error, but we
                // proceed to save anyway.
                // Or better, check if linked.
                mAuth.getCurrentUser().unlink(PhoneAuthProvider.PROVIDER_ID)
                        .addOnCompleteListener(task -> {
                            // Whether unlink success (was linked and now removed) or failed (wasn't
                            // linked),
                            // we proceed to save the NEW number to DB.
                            // Ideally we check error, but user wants "save unverified phone", so priority
                            // is DB update.

                            updates.put("phone", newPhone);
                            updates.put("phoneVerified", false);
                            updates.put("dateOfBirth", newDob);
                            updates.put("gender", newGender);

                            performProfileUpdate(updates, newName);
                        });

                return; // Async flow takes over
            }
            // Phone is same as verified/current. Just save.
            updates.put("phone", currentPhone);
            updates.put("dateOfBirth", newDob);
            updates.put("gender", newGender);
            performProfileUpdate(updates, newName);
        });

        btnCancel.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            finish();
        });

        inputPhone.addTextChangedListener(new android.text.TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim();

                String authPhone = getLinkedPhoneNumber();
                String normalizedInput = normalizePhoneNumber(input);

                boolean matchesAuth = authPhone != null && normalizedInput.equals(authPhone);

                // If matches Auth, it is verified.
                if (matchesAuth) {
                    btnVerifyPhone.setVisibility(android.view.View.GONE);
                    imgPhoneStatus.setImageResource(R.drawable.ic_check);
                    imgPhoneStatus.setColorFilter(getResources().getColor(R.color.netflix_green));
                } else {
                    // Mismatch -> Show Verify
                    btnVerifyPhone.setVisibility(android.view.View.VISIBLE);
                    imgPhoneStatus.setImageResource(R.drawable.ic_close);
                    imgPhoneStatus.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }

        });

        // Initialize button visibility based on initial state (handled in onDataChange
        // mostly, but good to ensure)
    }

    private void setupGenderDropdown() {
        final String[] genders = new String[] { getString(R.string.male), getString(R.string.female),
                getString(R.string.other) };

        inputGender.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle(getString(R.string.select_gender))
                    .setItems(genders, (dialog, which) -> {
                        inputGender.setText(genders[which]);
                    })
                    .show();
        });
    }

    private void setupDatePicker() {
        inputDob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            String currentDob = Objects.requireNonNull(inputDob.getText()).toString();
            if (!currentDob.isEmpty()) {
                try {
                    String[] parts = currentDob.split("/");
                    if (parts.length == 3) {
                        day = Integer.parseInt(parts[0]);
                        month = Integer.parseInt(parts[1]) - 1;
                        year = Integer.parseInt(parts[2]);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                        inputDob.setText(date);
                    },
                    year, month, day);
            datePickerDialog.show();
        });
    }

    private void setupPhoneAuthCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // Auto-retrieval or instant verification
                linkPhoneCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                extra_sound_manager.playError(partuser_edit_profile.this);
                Toast.makeText(partuser_edit_profile.this,
                        String.format(getString(R.string.toast_verification_failed), e.getMessage()), Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                verificationId = s;
                resendToken = token;
                showOtpDialog();
            }
        };
    }

    private void showOtpDialog() {
        EditText input = new EditText(this);
        input.setHint(getString(R.string.dialog_enter_otp));
        input.setTextAlignment(android.view.View.TEXT_ALIGNMENT_CENTER);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        android.widget.FrameLayout container = new android.widget.FrameLayout(this);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 50;
        params.rightMargin = 50;
        input.setLayoutParams(params);
        container.addView(input);

        new android.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_enter_otp_title))
                .setView(container)
                .setPositiveButton(getString(R.string.dialog_confirm), (dialog, which) -> {
                    String code = input.getText().toString().trim();
                    if (!code.isEmpty()) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                        linkPhoneCredential(credential);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void linkPhoneCredential(PhoneAuthCredential credential) {
        if (mAuth.getCurrentUser() != null) {
            // Check if user already has a phone number linked
            String currentLinkedPhone = null;
            for (com.google.firebase.auth.UserInfo profile : mAuth.getCurrentUser().getProviderData()) {
                if (PhoneAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                    currentLinkedPhone = profile.getPhoneNumber();
                    break;
                }
            }

            if (currentLinkedPhone != null) {
                // Unlink the old phone number first
                mAuth.getCurrentUser().unlink(PhoneAuthProvider.PROVIDER_ID)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Now link the new credential
                                finalLink(credential);
                            } else {
                                extra_sound_manager.playError(this);
                                Toast.makeText(this,
                                        String.format(getString(R.string.toast_unlink_error),
                                                Objects.requireNonNull(task.getException()).getMessage()),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // No existing phone, pure link
                finalLink(credential);
            }
        }
    }

    private void finalLink(PhoneAuthCredential credential) {
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> updatePhoneMap = new HashMap<>();
                        updatePhoneMap.put("phoneVerified", true);

                        String newPhone = mAuth.getCurrentUser().getPhoneNumber();
                        if (newPhone != null) {
                            updatePhoneMap.put("phone", newPhone);
                            currentPhone = newPhone;
                            inputPhone.setText(newPhone);
                        }

                        usersRef.child(mAuth.getCurrentUser().getUid()).updateChildren(updatePhoneMap);

                        extra_sound_manager.playSuccess(this);
                        Toast.makeText(this, getString(R.string.toast_phone_update_success), Toast.LENGTH_SHORT).show();
                        imgPhoneStatus.setImageResource(R.drawable.ic_check);
                        imgPhoneStatus.setColorFilter(getResources().getColor(R.color.netflix_green));
                        btnVerifyPhone.setVisibility(android.view.View.GONE);
                    } else {
                        Exception e = task.getException();
                        extra_sound_manager.playError(this);

                        if (e instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, getString(R.string.toast_phone_linked_other), Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            String msg = e != null ? e.getMessage() : "Unknown";
                            Toast.makeText(this, String.format(getString(R.string.toast_error), msg), Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imgAvatar.setImageURI(selectedImageUri);
        }
    }

    private void uploadToImgBB(Uri imageUri, String uid, Map<String, Object> updates) {
        try {
            Bitmap original = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            int maxDim = 1024;
            int width = original.getWidth();
            int height = original.getHeight();
            float scale = Math.min(1f, (float) maxDim / Math.max(width, height));
            Bitmap scaled = Bitmap.createScaledBitmap(original, Math.max(1, (int) (width * scale)),
                    Math.max(1, (int) (height * scale)), true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

            OkHttpClient client = new OkHttpClient.Builder().callTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .build();

            RequestBody formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("key", getString(R.string.imgbb_api_key))
                    .addFormDataPart("image", encodedImage)
                    .build();

            Request request = new Request.Builder().url("https://api.imgbb.com/1/upload").post(formBody).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> {
                        extra_sound_manager.playError(partuser_edit_profile.this);
                        Toast.makeText(partuser_edit_profile.this,
                                String.format(getString(R.string.toast_upload_error), e.getMessage()),
                                Toast.LENGTH_LONG).show();
                    });
                    Log.e("IMGBB_UPLOAD", "onFailure", e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        final String respStr = response.body() != null ? response.body().string() : "";
                        if (response.isSuccessful()) {
                            JSONObject obj = new JSONObject(respStr);
                            String imageUrl = obj.getJSONObject("data").getString("url");
                            updates.put("avatarUrl", imageUrl);
                            usersRef.child(uid).updateChildren(updates)
                                    .addOnCompleteListener(task -> runOnUiThread(() -> {
                                        if (task.isSuccessful()) {
                                            extra_sound_manager.playSuccess(partuser_edit_profile.this);
                                            Toast.makeText(partuser_edit_profile.this,
                                                    getString(R.string.toast_profile_updated),
                                                    Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            extra_sound_manager.playError(partuser_edit_profile.this);
                                            Toast.makeText(partuser_edit_profile.this,
                                                    String.format(getString(R.string.toast_db_error),
                                                            Objects.requireNonNull(task.getException()).getMessage()),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }));
                        } else {
                            runOnUiThread(() -> {
                                extra_sound_manager.playError(partuser_edit_profile.this);
                                Toast.makeText(partuser_edit_profile.this,
                                        String.format(getString(R.string.toast_upload_failed),
                                                String.valueOf(response.code()), respStr),
                                        Toast.LENGTH_LONG)
                                        .show();
                            });
                        }
                    } catch (Exception ex) {
                        runOnUiThread(() -> {
                            extra_sound_manager.playError(partuser_edit_profile.this);
                            Toast.makeText(partuser_edit_profile.this,
                                    String.format(getString(R.string.toast_response_error), ex.getMessage()),
                                    Toast.LENGTH_LONG).show();
                        });
                        Log.e("IMGBB_UPLOAD", "parse error", ex);
                    } finally {
                        if (response.body() != null)
                            response.body().close();
                    }
                }
            });
        } catch (IOException e) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, String.format(getString(R.string.toast_image_error), e.getMessage()),
                    Toast.LENGTH_SHORT).show();
            Log.e("IMGBB_UPLOAD", "bitmap error", e);
        }
    }

    // Helper to perform the final DB update to avoid code duplication
    private void performProfileUpdate(Map<String, Object> updates, String newName) {
        if (selectedImageUri != null) {
            // If we have an image to upload, upload it first, then update childs inside
            // that method
            // But wait, uploadToImgBB calls updateChildren internally.
            // We should refactor strictly if we want perfect DRY, but here we can just pass
            // 'updates' to uploadToImgBB
            uploadToImgBB(selectedImageUri, mAuth.getCurrentUser().getUid(), updates);
        } else {
            // Direct update
            usersRef.child(mAuth.getCurrentUser().getUid()).updateChildren(updates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            extra_sound_manager.playSuccess(this);
                            prefs.edit().putString("username", newName).apply();
                            Toast.makeText(this, getString(R.string.toast_profile_updated), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            extra_sound_manager.playError(this);
                            Toast.makeText(this,
                                    String.format(getString(R.string.toast_update_error),
                                            Objects.requireNonNull(task.getException()).getMessage()),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    // Helper to get formatted/normalized phone number
    private String normalizePhoneNumber(String phone) {
        if (phone == null || phone.isEmpty())
            return "";
        String clean = phone.trim();
        if (clean.startsWith("0")) {
            return "+84" + clean.substring(1);
        } else if (!clean.startsWith("+")) {
            return "+84" + clean;
        }
        return clean;
    }

    // Helper to get the phone number actually linked to Firebase Auth
    private String getLinkedPhoneNumber() {
        if (mAuth.getCurrentUser() == null)
            return null;
        for (com.google.firebase.auth.UserInfo profile : mAuth.getCurrentUser().getProviderData()) {
            if (PhoneAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                return profile.getPhoneNumber();
            }
        }
        return null;
    }
}