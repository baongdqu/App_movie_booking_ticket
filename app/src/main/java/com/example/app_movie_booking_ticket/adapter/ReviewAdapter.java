package com.example.app_movie_booking_ticket.adapter;

import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Cần thư viện Glide để load ảnh
import com.example.app_movie_booking_ticket.R;
import com.example.app_movie_booking_ticket.databinding.ParthomeViewholderReviewBinding;
import com.example.app_movie_booking_ticket.model.ReviewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<ReviewModel> items;

    public ReviewAdapter(List<ReviewModel> items) {
        this.items = items;
    }

    // Hàm cập nhật danh sách cho mục đích lọc (Filter)
    public void updateList(List<ReviewModel> newList) {
        this.items = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ParthomeViewholderReviewBinding binding = ParthomeViewholderReviewBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewModel review = items.get(position);
        String currentUid = FirebaseAuth.getInstance().getUid();

        // Kiểm tra xem đây có phải đánh giá của người dùng hiện tại không
        boolean isCurrentUser = currentUid != null && currentUid.equals(review.getUserId());

        if (isCurrentUser) {
            holder.binding.tvUserNameReview.setText(review.getUserName() + " (Bạn)");
            holder.binding.getRoot().setCardBackgroundColor(Color.parseColor("#2A2A3A")); // Highlight nhẹ
        } else {
            holder.binding.tvUserNameReview.setText(review.getUserName());
            holder.binding.getRoot().setCardBackgroundColor(Color.TRANSPARENT);
        }

        holder.binding.tvCommentReview.setText(review.getComment());
        holder.binding.tvStarCountReview.setText(review.getRating() + " ★");

        // Format timestamp thành ngày tháng (ví dụ: 20/12/2025)
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(review.getTimestamp());
        String date = DateFormat.format("dd/MM/yyyy", cal).toString();
        holder.binding.tvDateReview.setText(date);

        // Load ảnh đại diện bằng Glide (nếu có)
        Glide.with(holder.itemView.getContext())
                .load(review.getUserAvatar())
                .placeholder(R.drawable.ic_default_avatar) // Ảnh mặc định
                .into(holder.binding.imgUserAvatar);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ParthomeViewholderReviewBinding binding;

        public ViewHolder(ParthomeViewholderReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}