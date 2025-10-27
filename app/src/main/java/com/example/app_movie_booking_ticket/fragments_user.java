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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class fragments_user extends Fragment {

    private TextView txtUsername;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private SharedPreferences prefs;

    public fragments_user() {
        // Required empty public constructor
    }

    public static fragments_user newInstance() {
        return new fragments_user();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout (tên file layout: fragment_user.xml)
        return inflater.inflate(R.layout.layouts_fragments_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ view (từ view)
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        ImageView imgAvatar = view.findViewById(R.id.imgAvatar);
        txtUsername = view.findViewById(R.id.txtUsername);
        TextView txtEmail = view.findViewById(R.id.txtEmail);
        Button btnEditProfile = view.findViewById(R.id.btnEditProfile);
        Button btnSettings = view.findViewById(R.id.btnSettings);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference("users");

        // SharedPreferences (lấy từ Activity chứa fragment)
        getContext();
        prefs = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Kiểm tra nếu chưa đăng nhập -> chuyển về Login
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
            return;
        }

        // Lấy email ưu tiên từ SharedPreferences
        String email = prefs.getString("email", null);
        if (email == null) {
            email = currentUser.getEmail();
            if (email != null) {
                prefs.edit().putString("email", email).apply();
            }
        }

        txtEmail.setText(email != null ? email : "Không có email");

        if (email != null) {
            loadUserData(email);
        } else {
            txtUsername.setText("Người dùng");
        }

        // Nút back: quay về Home (ta sẽ gọi activity chứa fragment để load lại fragment Home)
        btnBack.setOnClickListener(v -> {
            if (requireActivity() instanceof activities_2_menu_manage_fragments) {
                ((activities_2_menu_manage_fragments) requireActivity()).loadFragment(new fragments_home());
                // chọn lại nav item home nếu muốn
                ((activities_2_menu_manage_fragments) requireActivity()).selectBottomNavItem(R.id.nav_home);
            }
        });

        btnEditProfile.setOnClickListener(v ->
                Toast.makeText(getContext(), "Chức năng chỉnh sửa (chưa implement)", Toast.LENGTH_SHORT).show()
        );

        btnSettings.setOnClickListener(v ->
                Toast.makeText(getContext(), "Cài đặt (chưa implement)", Toast.LENGTH_SHORT).show()
        );

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
            // Xóa thông tin đã lưu
            prefs.edit().clear().apply();
            // Đăng xuất khỏi Firebase
            mAuth.signOut();
            // Quay về login và clear stack
            Intent intent = new Intent(requireActivity(), activities_1_login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void loadUserData(String email) {
        ref.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                String username = child.child("fullName").getValue(String.class);
                                txtUsername.setText(username != null ? username : "Người dùng");
                                break;
                            }
                        } else {
                            txtUsername.setText("Không tìm thấy người dùng");
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
