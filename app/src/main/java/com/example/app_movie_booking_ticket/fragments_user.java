package com.example.app_movie_booking_ticket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class fragments_user extends Fragment {

    // ============================
    //  Các view (để dùng ở nhiều chỗ)
    // ============================
    private TextView txtUsername;
    private TextView txtEmail;
    private ImageView imgAvatar;            // <- trước đây là local trong onViewCreated, bây giờ là field
    private ImageButton btnBack;
    private Button btnEditProfile, btnSettings, btnLogout;

    // Firebase + prefs
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private SharedPreferences prefs;

    // Ảnh mặc định (nên giống constant trong edit_profile)
    private static final String DEFAULT_AVATAR_URL = "https://i.ibb.co/C3JdHS1r/Avatar-trang-den.png";

    public fragments_user() {
        // Required empty public constructor
    }

    public static fragments_user newInstance() {
        return new fragments_user();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout fragment_user.xml
        return inflater.inflate(R.layout.layouts_fragments_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ---------------------------
        // ÁNH XẠ VIEW (từ view của fragment)
        // ---------------------------
        imgAvatar = view.findViewById(R.id.imgAvatar); // <- đảm bảo id trùng với layout
        txtUsername = view.findViewById(R.id.txtUsername);
        txtEmail = view.findViewById(R.id.txtEmail);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnLogout = view.findViewById(R.id.btnLogout);

        // ---------------------------
        // KHỞI TẠO FIREBASE + PREFS
        // ---------------------------
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference("users");
        prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Kiểm tra user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
            return;
        }

        // Lấy email (ưu tiên từ prefs)
        String email = prefs.getString("email", currentUser.getEmail());
        txtEmail.setText(email != null ? email : "Không có email");

        // Load dữ liệu (username + avatar)
        if (email != null) {
            loadUserData(email);
        } else {
            txtUsername.setText("Người dùng");
            imgAvatar.setImageResource(R.drawable.ic_person);
        }

        // Chỉnh sửa hồ sơ
        btnEditProfile.setOnClickListener(v -> startActivity(new Intent(requireActivity(), activities_3_edit_profile.class)));

        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), activities_3_advanced_settings.class));
        });

        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            mAuth.signOut();
            Intent intent = new Intent(requireActivity(), activities_1_login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // refresh khi quay lại (nếu người dùng vừa đổi avatar)
        String emailReload = prefs.getString("email", null);
        if (emailReload == null && mAuth.getCurrentUser() != null) {
            emailReload = mAuth.getCurrentUser().getEmail();
        }
        if (emailReload != null) {
            loadUserData(emailReload);
        }
    }

    /**
     * Load user (username + avatarUrl) từ Realtime DB theo email
     * - Nếu avatarUrl không có -> dùng avatar mặc định
     * - Sử dụng Glide để load ảnh vào imgAvatar
     */
    private void loadUserData(String email) {
        ref.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // chỉ lấy node đầu tiên tìm được
                            for (DataSnapshot child : snapshot.getChildren()) {
                                String username = child.child("fullName").getValue(String.class);
                                String avatarUrl = child.child("avatarUrl").getValue(String.class);

                                // set tên
                                txtUsername.setText(username != null ? username : "Người dùng");

                                // set avatar — nếu null hoặc rỗng thì dùng default
                                final String urlToLoad = (avatarUrl != null && !avatarUrl.isEmpty()) ? avatarUrl : DEFAULT_AVATAR_URL;

                                // Dùng Glide (context là fragment -> requireActivity())
                                try {
                                    Glide.with(requireActivity())
                                            .load(urlToLoad)
                                            .placeholder(R.drawable.ic_person)
                                            .error(R.drawable.ic_person)
                                            .circleCrop()
                                            .into(imgAvatar);
                                } catch (Exception e) {
                                    // phòng trường hợp context không hợp lệ
                                    imgAvatar.setImageResource(R.drawable.ic_person);
                                }

                                break; // chỉ lấy 1 user
                            }
                        } else {
                            txtUsername.setText("Không tìm thấy người dùng");
                            imgAvatar.setImageResource(R.drawable.ic_person);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(),
                                "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(requireActivity(), activities_1_login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
