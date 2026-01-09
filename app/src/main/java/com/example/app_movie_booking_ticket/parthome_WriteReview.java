package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.app_movie_booking_ticket.databinding.ParthomeWriteReviewBinding;
import com.example.app_movie_booking_ticket.model.ReviewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jspecify.annotations.NonNull;

public class parthome_WriteReview extends extra_manager_language {
    private ParthomeWriteReviewBinding binding; // Tên binding theo file XML của bạn
    private String movieID;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra_themeutils.applySavedTheme(this);
        binding = ParthomeWriteReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        movieID = getIntent().getStringExtra("MOVIE_ID");

        binding.btnSubmitReview.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            saveReview();
        });
        loadExistingReview();
        binding.btnCloseWrite.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            finish();
        });
        binding.btnDeleteReview.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_delete_review_title))
                    .setMessage(getString(R.string.dialog_delete_review_message))
                    .setPositiveButton(getString(R.string.dialog_delete), (dialog, which) -> deleteReview())
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show();
        });

    }

    private void saveReview() {
        String userId = mAuth.getCurrentUser().getUid();
        float rating = binding.ratingBarInput.getRating();
        String comment = binding.edtReviewComment.getText().toString().trim();
        if (comment.length() > 200) {
            Toast.makeText(this, getString(R.string.toast_review_char_limit), Toast.LENGTH_SHORT).show();
            return;
        }
        if (rating == 0) {
            Toast.makeText(this, getString(R.string.toast_select_rating), Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy thông tin người dùng hiện tại
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.child("fullName").getValue(String.class);
                String userAvatar = snapshot.child("avatarUrl").getValue(String.class);

                ReviewModel review = new ReviewModel(
                        userId,
                        userName,
                        userAvatar,
                        (int) rating,
                        comment,
                        System.currentTimeMillis() // Cập nhật lại thời gian đánh giá/chỉnh sửa
                );

                // Dùng child(userId) để đảm bảo mỗi User chỉ có 1 node duy nhất dưới mỗi phim
                mDatabase.child("Reviews").child(movieID).child(userId).setValue(review)
                        .addOnSuccessListener(aVoid -> {
                            extra_sound_manager.playSuccess(parthome_WriteReview.this);
                            Toast.makeText(parthome_WriteReview.this, getString(R.string.toast_review_saved),
                                    Toast.LENGTH_SHORT)
                                    .show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            extra_sound_manager.playError(parthome_WriteReview.this);
                            Toast.makeText(parthome_WriteReview.this, getString(R.string.toast_error, e.getMessage()),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadExistingReview() {
        String userId = FirebaseAuth.getInstance().getUid();
        mDatabase.child("Reviews").child(movieID).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            ReviewModel oldReview = snapshot.getValue(ReviewModel.class);
                            if (oldReview != null) {
                                // Hiển thị lại số sao và bình luận cũ để người dùng sửa
                                binding.ratingBarInput.setRating(oldReview.getRating());
                                binding.edtReviewComment.setText(oldReview.getComment());
                                binding.btnSubmitReview.setText(getString(R.string.btn_update)); // Đổi tên nút cho rõ
                                                                                                 // ràng
                                binding.btnDeleteReview.setVisibility(View.VISIBLE);
                            }
                        } else {
                            // Chưa có đánh giá -> Ẩn nút Xóa
                            binding.btnDeleteReview.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void deleteReview() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("Reviews").child(movieID).child(userId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    extra_sound_manager.playSuccess(this);
                    Toast.makeText(this, getString(R.string.toast_review_deleted), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    extra_sound_manager.playError(this);
                    Toast.makeText(this, getString(R.string.toast_error, e.getMessage()), Toast.LENGTH_SHORT).show();
                });
    }

}