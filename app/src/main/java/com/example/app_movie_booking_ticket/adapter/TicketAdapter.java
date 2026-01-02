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

import java.util.List;

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

        holder.tvTitle.setText(ticket.title);
        holder.tvInfo.setText(ticket.info);
        holder.tvSeats.setText(ticket.seats);

        Glide.with(context)
                .load(ticket.posterUrl)
                .placeholder(R.drawable.placeholder_movie)
                .error(R.drawable.placeholder_movie)
                .into(holder.imgPoster);

        holder.btnDetails.setOnClickListener(v -> {
            if (detailListener != null) detailListener.onDetailClick(ticket);
        });

        holder.btnRefund.setOnClickListener(v -> {
            if (refundListener != null) refundListener.onRefundClick(ticket);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
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
