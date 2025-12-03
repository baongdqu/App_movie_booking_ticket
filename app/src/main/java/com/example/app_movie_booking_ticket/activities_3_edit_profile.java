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
import com.google.firebase.database.*;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MultipartBody;

import com.bumptech.glide.Glide;
import android.util.Log;

public class activities_3_edit_profile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private TextInputEditText inputFullName, inputPhone, inputDob, inputGender;
    private Button btnSave, btnCancel, btnChangeAvatar;
    private ImageView imgAvatar;

    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;

    private Uri selectedImageUri = null;
    private final String DEFAULT_AVATAR_URL = "https://i.ibb.co/C3JdHS1r/Avatar-trang-den.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra_themeutils.applySavedTheme(this);
        setContentView(R.layout.layouts_3_edit_profile);

        inputFullName = findViewById(R.id.inputFullNameEdit);
        inputPhone = findViewById(R.id.inputPhoneEdit);
        inputDob = findViewById(R.id.inputDobEdit);
        inputGender = findViewById(R.id.inputGenderEdit);
        btnSave = findViewById(R.id.btnSaveProfile);
        btnCancel = findViewById(R.id.btnCancelProfile);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        imgAvatar = findViewById(R.id.imgAvatar);

        setupGenderDropdown();
        setupDatePicker();

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String uid = prefs.getString("uid", null);
        if (uid == null && mAuth.getCurrentUser() != null)
            uid = mAuth.getCurrentUser().getUid();
        if (uid == null) {
            extra_sound_manager.playError(this);
            Toast.makeText(this, "Không xác định người dùng.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String finalUid = uid;

        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
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
                                        extra_sound_manager.playSuccess(activities_3_edit_profile.this);
                                        Toast.makeText(activities_3_edit_profile.this, "Đã gán avatar mặc định.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        extra_sound_manager.playError(activities_3_edit_profile.this);
                                        Toast.makeText(activities_3_edit_profile.this,
                                                "Không thể cập nhật avatar mặc định: "
                                                        + Objects.requireNonNull(task.getException()).getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                        avatarUrl = DEFAULT_AVATAR_URL;
                    }

                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(activities_3_edit_profile.this)
                                .load(avatarUrl)
                                .placeholder(R.drawable.ic_person)
                                .error(R.drawable.ic_person)
                                .circleCrop()
                                .into(imgAvatar);
                    } else {
                        imgAvatar.setImageResource(R.drawable.ic_person);
                    }
                } else {
                    extra_sound_manager.playError(activities_3_edit_profile.this);
                    Toast.makeText(activities_3_edit_profile.this, "Không tìm thấy dữ liệu người dùng!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                extra_sound_manager.playError(activities_3_edit_profile.this);
                Toast.makeText(activities_3_edit_profile.this, "Lỗi tải dữ liệu: " + error.getMessage(),
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

        btnSave.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            String newName = Objects.requireNonNull(inputFullName.getText()).toString().trim();
            String newPhone = Objects.requireNonNull(inputPhone.getText()).toString().trim();
            String newDob = Objects.requireNonNull(inputDob.getText()).toString().trim();
            String newGender = Objects.requireNonNull(inputGender.getText()).toString().trim();

            if (TextUtils.isEmpty(newName)) {
                extra_sound_manager.playError(this);
                Toast.makeText(this, "Vui lòng nhập họ tên.", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("fullName", newName);
            updates.put("phone", newPhone);
            updates.put("dateOfBirth", newDob);
            updates.put("gender", newGender);

            if (selectedImageUri != null) {
                uploadToImgBB(selectedImageUri, finalUid, updates);
            } else {
                usersRef.child(finalUid).updateChildren(updates)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                extra_sound_manager.playSuccess(this);
                                prefs.edit().putString("username", newName).apply();
                                Toast.makeText(this, "Cập nhật hồ sơ thành công.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                extra_sound_manager.playError(this);
                                Toast.makeText(this,
                                        "Lỗi cập nhật: " + Objects.requireNonNull(task.getException()).getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        btnCancel.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            finish();
        });
    }

    private void setupGenderDropdown() {
        final String[] genders = new String[] { "Nam", "Nữ", "Khác" };

        inputGender.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Chọn giới tính")
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
                        extra_sound_manager.playError(activities_3_edit_profile.this);
                        Toast.makeText(activities_3_edit_profile.this, "Lỗi upload ảnh: " + e.getMessage(),
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
                                            extra_sound_manager.playSuccess(activities_3_edit_profile.this);
                                            Toast.makeText(activities_3_edit_profile.this, "Cập nhật hồ sơ thành công!",
                                                    Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            extra_sound_manager.playError(activities_3_edit_profile.this);
                                            Toast.makeText(activities_3_edit_profile.this,
                                                    "Lỗi lưu DB: "
                                                            + Objects.requireNonNull(task.getException()).getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }));
                        } else {
                            runOnUiThread(() -> {
                                extra_sound_manager.playError(activities_3_edit_profile.this);
                                Toast.makeText(activities_3_edit_profile.this,
                                        "Upload thất bại (" + response.code() + "): " + respStr, Toast.LENGTH_LONG)
                                        .show();
                            });
                        }
                    } catch (Exception ex) {
                        runOnUiThread(() -> {
                            extra_sound_manager.playError(activities_3_edit_profile.this);
                            Toast.makeText(activities_3_edit_profile.this,
                                    "Lỗi xử lý phản hồi upload: " + ex.getMessage(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Không thể xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("IMGBB_UPLOAD", "bitmap error", e);
        }
    }
}