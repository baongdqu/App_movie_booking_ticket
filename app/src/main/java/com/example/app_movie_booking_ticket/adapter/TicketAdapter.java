package com.example.app_movie_booking_ticket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_movie_booking_ticket.R;
import com.example.app_movie_booking_ticket.model.TicketSimple;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {

    public interface OnRefundClickListener {
        void onRefundClick(TicketSimple ticket);
    }

    public interface OnDetailClickListener {
        void onDetailClick(TicketSimple ticket);
    }

    private final Context context;
    private final List<TicketSimple> list;
    private final OnRefundClickListener refundListener;
    private final OnDetailClickListener detailListener;

    public TicketAdapter(Context context,
                         List<TicketSimple> list,
                         OnRefundClickListener refundListener,
                         OnDetailClickListener detailListener) {
        this.context = context;
        this.list = list;
        this.refundListener = refundListener;
        this.detailListener = detailListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.partmail_item_ticket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TicketSimple ticket = list.get(position);

        // 1. Hiển thị thông tin cơ bản
        holder.tvTitle.setText(ticket.movieTitle); // movieTitle

        // 2. Kết hợp thông tin Rạp và Thời gian

        // 3. Hiển thị số ghế
        holder.tvSeats.setText("Ghế: " + ticket.seats);

        // 4. Format và hiển thị Tổng tiền (Ví dụ: 150,000đ)
        DecimalFormat moneyFmt = new DecimalFormat("#,###");
        String priceText = moneyFmt.format(ticket.totalPrice) + "đ";
        // Nếu bạn có TextView tvPrice trong layout, hãy set nó ở đây
        // holder.tvPrice.setText(priceText);
        StringBuilder summaryBuilder = new StringBuilder();
        summaryBuilder.append("Rạp: ").append(ticket.cinemaName).append("\n");
        summaryBuilder.append("Suất: ").append(ticket.date).append(" • ").append(ticket.time).append("\n");

        if (ticket.payment != null && ticket.payment.paidAt > 0) {
            String purchaseTime = formatTimestamp(ticket.payment.paidAt);
            summaryBuilder.append("Mua lúc: ").append(purchaseTime);
        }
        // 4. Hiển thị ghế (Vì JSON seats là List nên dùng TextUtils.join)
        if (ticket.seats != null) {
            holder.tvSeats.setText("Ghế: " + android.text.TextUtils.join(", ", ticket.seats));
        }
        holder.tvInfo.setText(summaryBuilder.toString());
        // Load Poster
        Glide.with(context)
                .load(ticket.posterUrl)
                .placeholder(R.drawable.placeholder_movie)
                .into(holder.imgPoster);

        // Listeners
        holder.btnDetails.setOnClickListener(v -> detailListener.onDetailClick(ticket));
        holder.btnRefund.setOnClickListener(v -> refundListener.onRefundClick(ticket));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    private String formatTimestamp(long timestamp) {
        try {
            // Định dạng: Giờ:Phút - Ngày/Tháng/Năm
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
            Date date = new Date(timestamp);
            return sdf.format(date);
        } catch (Exception e) {
            return "";
        }
    }
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPoster;
        TextView tvTitle, tvInfo, tvSeats;
        Button btnDetails, btnRefund;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            tvSeats = itemView.findViewById(R.id.tvSeats);
            btnDetails = itemView.findViewById(R.id.btnDetails);
            btnRefund = itemView.findViewById(R.id.btnRefund);
        }
    }
}
