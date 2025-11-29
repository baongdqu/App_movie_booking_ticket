package com.example.app_movie_booking_ticket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_movie_booking_ticket.R;
import com.example.app_movie_booking_ticket.model.Ticket;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<Ticket> ticketList;

    public TicketAdapter(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);
        holder.tvMovieTitle.setText(ticket.getMovieTitle());
        holder.tvDateTime.setText(ticket.getDate() + " - " + ticket.getTime());
        holder.tvSeats.setText(ticket.getSeats());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvTotalPrice.setText(formatter.format(ticket.getTotalPrice()));
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieTitle, tvDateTime, tvSeats, tvTotalPrice;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvSeats = itemView.findViewById(R.id.tvSeats);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }
    }
}
