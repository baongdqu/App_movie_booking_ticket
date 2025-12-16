package com.example.app_movie_booking_ticket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_movie_booking_ticket.R;
import com.example.app_movie_booking_ticket.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter cho RecyclerView hiển thị tin nhắn chat
 * Hỗ trợ 2 loại ViewHolder: User và Bot
 */
public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> messages = new ArrayList<>();
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == ChatMessage.TYPE_USER) {
            View view = inflater.inflate(R.layout.partchatbot_item_chat_user, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.partchatbot_item_chat_bot, parent, false);
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        String time = timeFormat.format(new Date(message.getTimestamp()));

        if (holder instanceof UserMessageViewHolder) {
            UserMessageViewHolder userHolder = (UserMessageViewHolder) holder;
            userHolder.tvMessage.setText(message.getMessage());
            userHolder.tvTimestamp.setText(time);
        } else if (holder instanceof BotMessageViewHolder) {
            BotMessageViewHolder botHolder = (BotMessageViewHolder) holder;
            botHolder.tvMessage.setText(message.getMessage());
            botHolder.tvTimestamp.setText(time);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * Thêm tin nhắn mới và scroll tới cuối
     */
    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    /**
     * Lấy danh sách tin nhắn (cho lịch sử conversation)
     */
    public List<ChatMessage> getMessages() {
        return messages;
    }

    /**
     * Xóa tất cả tin nhắn
     */
    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
    }

    // ========== ViewHolders ==========

    /**
     * ViewHolder cho tin nhắn của User
     */
    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        TextView tvTimestamp;

        UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvUserMessage);
            tvTimestamp = itemView.findViewById(R.id.tvUserTimestamp);
        }
    }

    /**
     * ViewHolder cho tin nhắn của Bot
     */
    static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        TextView tvTimestamp;

        BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvBotMessage);
            tvTimestamp = itemView.findViewById(R.id.tvBotTimestamp);
        }
    }
}
