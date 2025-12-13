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

    private Context context;
    private List<AppNotification> list;

    public NotificationAdapter(Context context, List<AppNotification> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppNotification noti = list.get(position);

        holder.txtTitle.setText(noti.getTitle());
        holder.txtMessage.setText(noti.getMessage());

        SimpleDateFormat sdf =
                new SimpleDateFormat("HH:mm • dd/MM/yyyy", Locale.getDefault());
        holder.txtTime.setText(
                sdf.format(new Date(noti.getTimestamp()))
        );

        // ===== CLICK =====
        holder.itemView.setOnClickListener(v -> {
            String type = noti.getType();

            if ("PROFILE".equals(type)) {
                Intent intent =
                        new Intent(context, partuser_edit_profile.class);
                context.startActivity(intent);
            }
            // LOGIN thì không cần mở gì
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

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
