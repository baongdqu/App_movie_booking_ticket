package com.example.app_movie_booking_ticket.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_movie_booking_ticket.R;
import com.example.app_movie_booking_ticket.model.AppNotification;
import com.example.app_movie_booking_ticket.partuser_edit_profile;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotificationAdapter
        extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    // 🔥 CALLBACK
    public interface OnNotificationClickListener {
        void onNotificationClick(AppNotification notification);
    }

    private final Context context;
    private final List<AppNotification> list;
    private final OnNotificationClickListener listener;

    // ✅ Constructor mới
    public NotificationAdapter(Context context,
            List<AppNotification> list,
            OnNotificationClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.partnortifications_item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AppNotification noti = list.get(position);
        String type = noti.getType();

        // ===== TITLE & MESSAGE =====
        if ("PROFILE".equals(type)) {
            holder.txtTitle.setText(
                    context.getString(R.string.notification_profile_update_title));
            holder.txtMessage.setText(
                    context.getString(R.string.notification_profile_update_message));
            // Set icon for profile update
            holder.imgIcon.setImageResource(R.drawable.ic_person);
            holder.iconBackground.setBackgroundResource(R.drawable.bg_notification_icon_blue);
        } else if ("REFUND".equals(type)) {
            holder.txtTitle.setText(noti.getTitle());
            holder.txtMessage.setText(noti.getMessage());
            // Set icon for refund
            holder.imgIcon.setImageResource(R.drawable.ic_wallet);
            holder.iconBackground.setBackgroundResource(R.drawable.bg_notification_icon_green);
        } else if ("LOGIN".equals(type)) {
            holder.txtTitle.setText(noti.getTitle());
            holder.txtMessage.setText(noti.getMessage());
            // Set icon for login
            holder.imgIcon.setImageResource(R.drawable.ic_lock);
            holder.iconBackground.setBackgroundResource(R.drawable.bg_notification_icon);
        } else {
            holder.txtTitle.setText(noti.getTitle());
            holder.txtMessage.setText(noti.getMessage());
            // Default icon
            holder.imgIcon.setImageResource(R.drawable.ic_notifications);
            holder.iconBackground.setBackgroundResource(R.drawable.bg_notification_icon);
        }

        // ===== TIME - format thông minh =====
        holder.txtTime.setText(getRelativeTimeSpan(noti.getTimestamp()));

        // ===== READ / UNREAD UI =====
        if (noti.isRead()) {
            holder.unreadDot.setVisibility(View.GONE);
            holder.itemView.setAlpha(0.7f);
        } else {
            holder.unreadDot.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(1f);
        }

        // ===== CLICK =====
        holder.itemView.setOnClickListener(v -> {

            // PROFILE giữ hành vi cũ
            if ("PROFILE".equals(type)) {
                Intent intent = new Intent(context, partuser_edit_profile.class);
                context.startActivity(intent);
                return;
            }

            // LOGIN / REFUND → fragment xử lý
            if (listener != null) {
                listener.onNotificationClick(noti);
            }
        });
    }

    /**
     * Convert timestamp to relative time string (e.g., "2 giờ trước")
     */
    private String getRelativeTimeSpan(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < TimeUnit.MINUTES.toMillis(1)) {
            return context.getString(R.string.just_now);
        } else if (diff < TimeUnit.HOURS.toMillis(1)) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            return context.getString(R.string.minutes_ago, (int) minutes);
        } else if (diff < TimeUnit.DAYS.toMillis(1)) {
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            return context.getString(R.string.hours_ago, (int) hours);
        } else if (diff < TimeUnit.DAYS.toMillis(7)) {
            long days = TimeUnit.MILLISECONDS.toDays(diff);
            return context.getString(R.string.days_ago, (int) days);
        } else {
            // Fallback to date format
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy",
                    java.util.Locale.getDefault());
            return sdf.format(new java.util.Date(timestamp));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ===== VIEW HOLDER =====
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtMessage, txtTime;
        ImageView imgIcon;
        View iconBackground;
        View unreadDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtTime = itemView.findViewById(R.id.txtTime);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            iconBackground = itemView.findViewById(R.id.iconBackground);
            unreadDot = itemView.findViewById(R.id.unreadDot);
        }
    }
}
