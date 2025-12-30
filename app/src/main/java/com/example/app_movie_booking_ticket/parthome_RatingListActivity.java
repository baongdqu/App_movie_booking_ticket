package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app_movie_booking_ticket.adapter.ReviewAdapter;
import com.example.app_movie_booking_ticket.databinding.ParthomeRatingListBinding;
import com.example.app_movie_booking_ticket.model.ReviewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class parthome_RatingListActivity extends AppCompatActivity {

    private ParthomeRatingListBinding binding;
    private DatabaseReference mDatabase;
    private ReviewAdapter adapter;
    private String movieID;
    private String movieTitle;

    // Danh sách chính và danh sách đã lọc
    private List<ReviewModel> allReviews = new ArrayList<>();
    private List<ReviewModel> filteredReviews = new ArrayList<>();

    // Đánh giá của người dùng hiện tại
    private ReviewModel currentUserReview = null;

    // Thống kê
    private int[] starCounts = new int[6]; // index 1-5
    private double averageRating = 0;
    private int totalReviews = 0;

    // Filter hiện tại
    private int currentFilter = 0; // 0 = Tất cả, 1-5 = theo sao

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ParthomeRatingListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        movieID = getIntent().getStringExtra("movieID");
        movieTitle = getIntent().getStringExtra("movieTitle");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setupRecyclerView();
        setupFilterButtons();
        setupWriteReviewButton();

        binding.btnBacktoMovieDetail.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReviews();
    }

    private void setupRecyclerView() {
        adapter = new ReviewAdapter(filteredReviews);
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReviews.setAdapter(adapter);
    }

    private void setupFilterButtons() {
        Button[] filterButtons = {
                binding.btnFilterAll,
                binding.btnFilter1,
                binding.btnFilter2,
                binding.btnFilter3,
                binding.btnFilter4,
                binding.btnFilter5
        };

        // Set click listener cho tất cả các nút
        binding.btnFilterAll.setOnClickListener(v -> applyFilter(0, filterButtons));
        binding.btnFilter5.setOnClickListener(v -> applyFilter(5, filterButtons));
        binding.btnFilter4.setOnClickListener(v -> applyFilter(4, filterButtons));
        binding.btnFilter3.setOnClickListener(v -> applyFilter(3, filterButtons));
        binding.btnFilter2.setOnClickListener(v -> applyFilter(2, filterButtons));
        binding.btnFilter1.setOnClickListener(v -> applyFilter(1, filterButtons));

        // Mặc định chọn "Tất cả"
        updateFilterButtonUI(0, filterButtons);
    }

    private void applyFilter(int starFilter, Button[] buttons) {
        extra_sound_manager.playUiClick(this);
        currentFilter = starFilter;
        updateFilterButtonUI(starFilter, buttons);
        filterReviews(starFilter);
    }

    private void updateFilterButtonUI(int selectedFilter, Button[] buttons) {
        // Reset tất cả buttons về trạng thái bình thường
        for (Button btn : buttons) {
            btn.setSelected(false);
            btn.setAlpha(0.6f);
        }

        // Highlight button đang được chọn
        Button selectedBtn;
        switch (selectedFilter) {
            case 0:
                selectedBtn = binding.btnFilterAll;
                break;
            case 1:
                selectedBtn = binding.btnFilter1;
                break;
            case 2:
                selectedBtn = binding.btnFilter2;
                break;
            case 3:
                selectedBtn = binding.btnFilter3;
                break;
            case 4:
                selectedBtn = binding.btnFilter4;
                break;
            case 5:
                selectedBtn = binding.btnFilter5;
                break;
            default:
                selectedBtn = binding.btnFilterAll;
        }
        selectedBtn.setSelected(true);
        selectedBtn.setAlpha(1.0f);
    }

    private void filterReviews(int starFilter) {
        filteredReviews.clear();
        String currentUserId = FirebaseAuth.getInstance().getUid();

        if (starFilter == 0) {
            // Hiển thị tất cả, đưa đánh giá của user lên đầu
            for (ReviewModel review : allReviews) {
                if (currentUserId != null && currentUserId.equals(review.getUserId())) {
                    filteredReviews.add(0, review); // Thêm vào đầu
                } else {
                    filteredReviews.add(review);
                }
            }
        } else {
            // Lọc theo số sao
            for (ReviewModel review : allReviews) {
                if (review.getRating() == starFilter) {
                    if (currentUserId != null && currentUserId.equals(review.getUserId())) {
                        filteredReviews.add(0, review);
                    } else {
                        filteredReviews.add(review);
                    }
                }
            }
        }

        adapter.updateList(filteredReviews);

        // Hiển thị thông báo nếu không có đánh giá
        if (filteredReviews.isEmpty()) {
            binding.rvReviews.setVisibility(View.GONE);
            binding.llEmptyState.setVisibility(View.VISIBLE);
        } else {
            binding.rvReviews.setVisibility(View.VISIBLE);
            binding.llEmptyState.setVisibility(View.GONE);
        }
        updateReviewsHeader(starFilter);
    }

    private void setupWriteReviewButton() {
        binding.btnWriteReview.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            Intent intent = new Intent(this, parthome_WriteReview.class);
            intent.putExtra("MOVIE_ID", movieID);
            startActivity(intent);
        });
    }

    private void loadReviews() {
        mDatabase.child("Reviews").child(movieID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allReviews.clear();
                starCounts = new int[6];
                double totalScore = 0;
                totalReviews = (int) snapshot.getChildrenCount();

                String currentUserId = FirebaseAuth.getInstance().getUid();
                currentUserReview = null;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    try {
                        ReviewModel review = ds.getValue(ReviewModel.class);
                        if (review != null) {
                            allReviews.add(review);

                            // Thống kê sao
                            int rating = review.getRating();
                            if (rating >= 1 && rating <= 5) {
                                starCounts[rating]++;
                                totalScore += rating;
                            }

                            // Kiểm tra đánh giá của người dùng hiện tại
                            if (currentUserId != null && currentUserId.equals(review.getUserId())) {
                                currentUserReview = review;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("RatingList", "Error parsing review: " + e.getMessage());
                    }
                }

                // Sắp xếp theo thời gian mới nhất
                Collections.sort(allReviews, (r1, r2) -> Long.compare(r2.getTimestamp(), r1.getTimestamp()));

                // Tính điểm trung bình
                if (totalReviews > 0) {
                    averageRating = totalScore / totalReviews;
                } else {
                    averageRating = 0;
                }

                // Cập nhật UI
                updateSummaryUI();
                filterReviews(currentFilter);
                updateReviewsHeader(currentFilter);
                updateWriteReviewButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RatingList", "Firebase error: " + error.getMessage());
            }
        });
    }

    private void updateSummaryUI() {
        // Cập nhật điểm trung bình
        binding.tvAverageScore.setText(String.format("%.1f", averageRating));
        binding.tvTotalReviews.setText("(" + totalReviews + " đánh giá)");

        // Cập nhật số lượng từng mức sao
        binding.tvCount5Star.setText(String.valueOf(starCounts[5]));
        binding.tvCount4Star.setText(String.valueOf(starCounts[4]));
        binding.tvCount3Star.setText(String.valueOf(starCounts[3]));
        binding.tvCount2Star.setText(String.valueOf(starCounts[2]));
        binding.tvCount1Star.setText(String.valueOf(starCounts[1]));

        // Cập nhật progress bar phân phối sao
        if (totalReviews > 0) {
            binding.pb5Star.setProgress((starCounts[5] * 100) / totalReviews);
            binding.pb4Star.setProgress((starCounts[4] * 100) / totalReviews);
            binding.pb3Star.setProgress((starCounts[3] * 100) / totalReviews);
            binding.pb2Star.setProgress((starCounts[2] * 100) / totalReviews);
            binding.pb1Star.setProgress((starCounts[1] * 100) / totalReviews);
        } else {
            binding.pb5Star.setProgress(0);
            binding.pb4Star.setProgress(0);
            binding.pb3Star.setProgress(0);
            binding.pb2Star.setProgress(0);
            binding.pb1Star.setProgress(0);
        }

        // Cập nhật sentiment
        updateSentiment(averageRating);

        // Cập nhật số lượng cho các nút filter
        binding.btnFilterAll.setText("Tất cả (" + totalReviews + ")");
        binding.btnFilter5.setText("5 ★ (" + starCounts[5] + ")");
        binding.btnFilter4.setText("4 ★ (" + starCounts[4] + ")");
        binding.btnFilter3.setText("3 ★ (" + starCounts[3] + ")");
        binding.btnFilter2.setText("2 ★ (" + starCounts[2] + ")");
        binding.btnFilter1.setText("1 ★ (" + starCounts[1] + ")");
    }

    private void updateSentiment(double average) {
        if (totalReviews == 0) {
            binding.tvSentiment.setText("Chưa có đánh giá");
            binding.tvSentimentDescription.setText("Hãy là người đầu tiên đánh giá!");
        } else if (average >= 4.5) {
            binding.tvSentiment.setText("Cực phẩm");
            binding.tvSentimentDescription.setText("Phim được đánh giá rất cao!");
        } else if (average >= 3.5) {
            binding.tvSentiment.setText("Đáng xem");
            binding.tvSentimentDescription.setText("Phim nhận được nhiều phản hồi tích cực.");
        } else if (average >= 2.5) {
            binding.tvSentiment.setText("Kén người mê");
            binding.tvSentimentDescription.setText("Phim có ý kiến chia đôi từ khán giả.");
        } else {
            binding.tvSentiment.setText("Cần cải thiện");
            binding.tvSentimentDescription.setText("Phim nhận được nhiều phản hồi tiêu cực.");
        }
    }

    private void updateReviewsHeader(int starFilter) {
        if (starFilter == 0) {
            binding.tvReviewsHeader.setText("Tất cả đánh giá (" + totalReviews + ")");
        } else {
            binding.tvReviewsHeader.setText("Đánh giá " + starFilter + " sao (" + starCounts[starFilter] + ")");
        }
    }

    private void updateWriteReviewButton() {
        if (currentUserReview != null) {
            // Đã có đánh giá -> cho phép chỉnh sửa
            binding.btnWriteReview.setText("Chỉnh sửa đánh giá");
            binding.btnWriteReview.setIconResource(android.R.drawable.ic_menu_edit);
        } else {
            // Chưa có đánh giá
            binding.btnWriteReview.setText("Viết đánh giá");
            binding.btnWriteReview.setIconResource(android.R.drawable.ic_menu_edit);
        }
    }
}
