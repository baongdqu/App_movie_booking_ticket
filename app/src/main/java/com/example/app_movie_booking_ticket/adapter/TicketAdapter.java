package com.example.app_movie_booking_ticket.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_movie_booking_ticket.R;
import com.example.app_movie_booking_ticket.model.TicketSimple;
import com.example.app_movie_booking_ticket.parthome_movie_detail;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {

    private final Context context;
    private final List<TicketSimple> list;

    public TicketAdapter(Context context, List<TicketSimple> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_ticket, parent, false);
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
                .into(holder.imgPoster);

        // 👉 DETAILS
        holder.btnDetails.setOnClickListener(v -> {
            if (ticket.movie == null) return;

            Intent intent = new Intent(context, parthome_movie_detail.class);
            intent.putExtra("movie", ticket.movie);
            context.startActivity(intent);
        });

        // 👉 REFUND (chưa xử lý)
        holder.btnRefund.setOnClickListener(v ->
                Toast.makeText(
                        context,
                        "Refund feature coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ===== VIEW HOLDER =====
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPoster;
        TextView tvTitle, tvInfo, tvSeats;
        Button btnDetails, btnRefund;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPoster = itemView.findViewById(R.id.imgPoster);
            tvTitle   = itemView.findViewById(R.id.tvTitle);
            tvInfo    = itemView.findViewById(R.id.tvInfo);
            tvSeats   = itemView.findViewById(R.id.tvSeats);
            btnDetails = itemView.findViewById(R.id.btnDetails);
            btnRefund  = itemView.findViewById(R.id.btnRefund);
        }
    }
}
