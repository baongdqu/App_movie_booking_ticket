package com.example.app_movie_booking_ticket.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_movie_booking_ticket.R;
import com.example.app_movie_booking_ticket.model.AppNotification;
import com.example.app_movie_booking_ticket.partuser_edit_profile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        } else {
            holder.txtTitle.setText(noti.getTitle());
            holder.txtMessage.setText(noti.getMessage());
        }

        // ===== TIME =====
        SimpleDateFormat sdf =
                new SimpleDateFormat("HH:mm • dd/MM/yyyy", Locale.getDefault());
        holder.txtTime.setText(
                sdf.format(new Date(noti.getTimestamp())));

        // ===== READ / UNREAD UI =====
        holder.itemView.setAlpha(noti.isRead() ? 0.6f : 1f);

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

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ===== VIEW HOLDER =====
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtMessage, txtTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}
