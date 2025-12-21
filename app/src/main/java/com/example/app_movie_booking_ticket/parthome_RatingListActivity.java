package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

public class parthome_RatingListActivity extends AppCompatActivity {

    private ParthomeRatingListBinding binding;
    private String movieID, movieTitle;
    private List<ReviewModel> fullList = new ArrayList<>();
    private ReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ParthomeRatingListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        movieID = getIntent().getStringExtra("movieID");
        movieTitle = getIntent().getStringExtra("movieTitle");

        setupRecyclerView();
        loadReviews("Tất cả"); // Mặc định tải tất cả

        // Nút viết đánh giá
        binding.btnWriteReview.setOnClickListener(v -> {
            checkEligibilityAndGoToWrite();
        });

        // Các nút lọc
        binding.btnFilterAll.setOnClickListener(v -> filter(0));
        binding.btnFilter5.setOnClickListener(v -> filter(5));
        // ... tương tự cho 4,3,2,1 sao
    }

    private void filter(int star) {
        if (star == 0) {
            adapter.updateList(fullList);
        } else {
            List<ReviewModel> filtered = new ArrayList<>();
            for (ReviewModel r : fullList) {
                if (r.getRating() == star) filtered.add(r);
            }
            adapter.updateList(filtered);
        }
    }

    private void checkEligibilityAndGoToWrite() {
        String currentUid = FirebaseAuth.getInstance().getUid();
        DatabaseReference ticketsRef = FirebaseDatabase.getInstance().getReference("tickets");

        ticketsRef.orderByChild("userId").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasBought = false;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    // So sánh tên phim từ node tickets
                    if (movieTitle.equalsIgnoreCase(ds.child("movieTitle").getValue(String.class))
                            && "PAID".equals(ds.child("payment/status").getValue(String.class))) {
                        hasBought = true;
                        break;
                    }
                }

                if (hasBought) {
                    Intent intent = new Intent(parthome_RatingListActivity.this, parthome_WriteReview.class);
                    intent.putExtra("MOVIE_ID", movieID);
                    startActivity(intent);
                } else {
                    Toast.makeText(parthome_RatingListActivity.this, "Bạn cần mua vé phim này để đánh giá!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadReviews(String type) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Reviews").child(movieID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    fullList.add(ds.getValue(ReviewModel.class));
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    // Thêm vào trong parthome_RatingListActivity
    private void setupRecyclerView() {
        adapter = new ReviewAdapter(fullList);
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReviews.setAdapter(adapter);
    }
}