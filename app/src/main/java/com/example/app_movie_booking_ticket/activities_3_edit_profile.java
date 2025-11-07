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

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

/**
 * Màn hình chỉnh sửa thông tin người dùng
 * ---------------------------------------
 * - Sửa họ tên, số điện thoại
 * - Đổi ảnh đại diện (upload lên ImgBB)
 * - Tự động gán avatar mặc định nếu user chưa có
 */
public class activities_3_edit_profile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private TextInputEditText inputFullName, inputPhone;
    private Button btnSave, btnCancel, btnChangeAvatar;
    private ImageView imgAvatar;

    private DatabaseReference usersRef;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;

    // Ảnh người dùng chọn (nếu có)
    private Uri selectedImageUri = null;

    // URL avatar mặc định
    private final String DEFAULT_AVATAR_URL = "https://i.ibb.co/C3JdHS1r/Avatar-trang-den.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra_themeutils.applySavedTheme(this);
        setContentView(R.layout.layouts_3_edit_profile);

        // -------------------------------
        // ÁNH XẠ VIEW
        // -------------------------------
        inputFullName = findViewById(R.id.inputFullNameEdit);
        inputPhone = findViewById(R.id.inputPhoneEdit);
        btnSave = findViewById(R.id.btnSaveProfile);
        btnCancel = findViewById(R.id.btnCancelProfile);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        imgAvatar = findViewById(R.id.imgAvatar);

        // -------------------------------
        // KHỞI TẠO FIREBASE
        // -------------------------------
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Lấy UID người dùng
        String uid = prefs.getString("uid", null);
        if (uid == null && mAuth.getCurrentUser() != null) {
            uid = mAuth.getCurrentUser().getUid();
        }
        if (uid == null) {
            Toast.makeText(this, "Không xác định người dùng.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String finalUid = uid;

        // -------------------------------
        // LOAD DỮ LIỆU NGƯỜI DÙNG HIỆN TẠI
        // -------------------------------
        String finalUid1 = uid;
        String finalUid2 = uid;
        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);

                    inputFullName.setText(fullName != null ? fullName : "");
                    inputPhone.setText(phone != null ? phone : "");

                    if (avatarUrl == null || avatarUrl.isEmpty()) {
                        String finalUidCopy = finalUid2;
                        String finalAvatarUrl = DEFAULT_AVATAR_URL;

                        Map<String, Object> defaultAvatar = new HashMap<>();
                        defaultAvatar.put("avatarUrl", finalAvatarUrl);

                        runOnUiThread(() -> {
                            usersRef.child(finalUidCopy).updateChildren(defaultAvatar)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(activities_3_edit_profile.this,
                                                    "Đã gán avatar mặc định.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(activities_3_edit_profile.this,
                                                    "Không thể cập nhật avatar mặc định: " +
                                                            Objects.requireNonNull(task.getException()).getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                        });

                        avatarUrl = finalAvatarUrl; // gán tạm để hiển thị ngay trên giao diện
                    }


                    // 🎨 Hiển thị avatar (dù mặc định hay upload)
                    if (imgAvatar != null) {
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(activities_3_edit_profile.this)
                                    .load(avatarUrl)
                                    .placeholder(R.drawable.ic_person) // ảnh tạm trong lúc tải
                                    .error(R.drawable.ic_person)       // ảnh fallback nếu lỗi tải
                                    .circleCrop()                      // bo tròn ảnh
                                    .into(imgAvatar);
                        } else {
                            imgAvatar.setImageResource(R.drawable.ic_person);
                        }
                    }
                } else {
                    Toast.makeText(activities_3_edit_profile.this, "Không tìm thấy dữ liệu người dùng!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activities_3_edit_profile.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // -------------------------------
        // NÚT "ĐỔI ẢNH ĐẠI DIỆN"
        // -------------------------------
        btnChangeAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            // Ưu tiên mở bằng ứng dụng Google Photos
            intent.setPackage("com.google.android.apps.photos");

            try {
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            } catch (android.content.ActivityNotFoundException e) {
                // Nếu chưa cài Google Photos thì fallback về trình chọn ảnh mặc định
                Intent fallback = new Intent(Intent.ACTION_GET_CONTENT);
                fallback.setType("image/*");
                fallback.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(fallback, "Chọn ảnh"), PICK_IMAGE_REQUEST);
            }
        });

        // -------------------------------
        // NÚT "LƯU THAY ĐỔI"
        // -------------------------------
        btnSave.setOnClickListener(v -> {
            String newName = Objects.requireNonNull(inputFullName.getText()).toString().trim();
            String newPhone = Objects.requireNonNull(inputPhone.getText()).toString().trim();

            if (TextUtils.isEmpty(newName)) {
                Toast.makeText(this, "Vui lòng nhập họ tên.", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("fullName", newName);
            updates.put("phone", newPhone);

            // Nếu user có chọn ảnh mới thì upload ImgBB
            if (selectedImageUri != null) {
                uploadToImgBB(selectedImageUri, finalUid, updates);
            } else {
                usersRef.child(finalUid).updateChildren(updates)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                prefs.edit().putString("username", newName).apply();
                                Toast.makeText(this, "Cập nhật hồ sơ thành công.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Lỗi cập nhật: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        // -------------------------------
        // NÚT "HỦY"
        // -------------------------------
        btnCancel.setOnClickListener(v -> finish());
    }

    // ---------------------------------
    // XỬ LÝ KẾT QUẢ CHỌN ẢNH
    // ---------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imgAvatar.setImageURI(selectedImageUri); // preview tạm thời
        }
    }

    // ---------------------------------
    // HÀM UPLOAD ẢNH LÊN IMGBB
    // ---------------------------------
    private void uploadToImgBB(Uri imageUri, String uid, Map<String, Object> updates) {
        try {
            // 1) load và scale ảnh để tránh OOM
            Bitmap original = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            int maxDim = 1024; // thay nếu muốn khác
            int width = original.getWidth();
            int height = original.getHeight();
            float scale = Math.min(1f, (float) maxDim / Math.max(width, height));
            Bitmap scaled = Bitmap.createScaledBitmap(original,
                    Math.max(1, (int)(width * scale)),
                    Math.max(1, (int)(height * scale)),
                    true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP); // NO_WRAP tránh newline

            OkHttpClient client = new OkHttpClient.Builder()
                    .callTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .build();

            RequestBody formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("key", getString(R.string.imgbb_api_key)) // lưu key trong strings.xml
                    .addFormDataPart("image", encodedImage)
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.imgbb.com/1/upload")
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> Toast.makeText(activities_3_edit_profile.this,
                            "Lỗi upload ảnh: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    Log.e("IMGBB_UPLOAD", "onFailure", e);
                }
                @Override public void onResponse(@NonNull Call call, @NonNull Response response) {
                    try {
                        final String respStr = response.body() != null ? response.body().string() : "";
                        Log.d("IMGBB_UPLOAD", "response code=" + response.code() + " body=" + respStr);

                        if (response.isSuccessful()) {
                            JSONObject obj = new JSONObject(respStr);
                            String imageUrl = obj.getJSONObject("data").getString("url");

                            updates.put("avatarUrl", imageUrl);
                            usersRef.child(uid).updateChildren(updates)
                                    .addOnCompleteListener(task -> runOnUiThread(() -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(activities_3_edit_profile.this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(activities_3_edit_profile.this, "Lỗi lưu DB: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }));
                        } else {
                            runOnUiThread(() -> Toast.makeText(activities_3_edit_profile.this,
                                    "Upload thất bại (" + response.code() + "): " + respStr, Toast.LENGTH_LONG).show());
                        }
                    } catch (Exception ex) {
                        runOnUiThread(() -> Toast.makeText(activities_3_edit_profile.this,
                                "Lỗi xử lý phản hồi upload: " + ex.getMessage(), Toast.LENGTH_LONG).show());
                        Log.e("IMGBB_UPLOAD", "parse error", ex);
                    } finally {
                        if (response.body() != null) response.body().close();
                    }
                }
            });

        } catch (IOException e) {
            Toast.makeText(this, "Không thể xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("IMGBB_UPLOAD", "bitmap error", e);
        }
    }

}
