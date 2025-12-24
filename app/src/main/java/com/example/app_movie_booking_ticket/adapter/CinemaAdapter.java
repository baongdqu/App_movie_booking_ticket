package com.example.app_movie_booking_ticket.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_movie_booking_ticket.Cinema;
import com.example.app_movie_booking_ticket.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying list of nearby cinemas
 * Supports click to open Google Maps for directions
 */
public class CinemaAdapter extends RecyclerView.Adapter<CinemaAdapter.CinemaViewHolder> {

    private final Context context;
    private List<Cinema> cinemaList;
    private final String apiKey;

    public CinemaAdapter(Context context, String apiKey) {
        this.context = context;
        this.cinemaList = new ArrayList<>();
        this.apiKey = apiKey;
    }

    public void setCinemaList(List<Cinema> cinemaList) {
        this.cinemaList = cinemaList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CinemaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.partcinema_item_cinema, parent, false);
        return new CinemaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CinemaViewHolder holder, int position) {
        Cinema cinema = cinemaList.get(position);

        // Set name
        holder.tvCinemaName.setText(cinema.getName());

        // Set address
        holder.tvAddress.setText(cinema.getAddress());

        // Set distance
        holder.tvDistance.setText(cinema.getFormattedDistance());

        // Set rating
        if (cinema.getRating() > 0) {
            holder.layoutRating.setVisibility(View.VISIBLE);
            holder.tvRating.setText(String.format("%.1f", cinema.getRating()));
        } else {
            holder.layoutRating.setVisibility(View.GONE);
        }

        // Set open/closed status
        if (cinema.hasOpeningHours()) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            if (cinema.isOpenNow()) {
                holder.tvStatus.setText(R.string.cinema_open_now);
                holder.tvStatus.setBackgroundColor(Color.parseColor("#4CAF50"));
            } else {
                holder.tvStatus.setText(R.string.cinema_closed);
                holder.tvStatus.setBackgroundColor(Color.parseColor("#F44336"));
            }
        } else {
            holder.tvStatus.setVisibility(View.GONE);
        }

        // Load photo
        String photoUrl = cinema.getPhotoUrl(apiKey, 400);
        if (photoUrl != null) {
            Glide.with(context)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_cinema)
                    .error(R.drawable.ic_cinema)
                    .centerCrop()
                    .into(holder.imgCinema);
        } else {
            holder.imgCinema.setImageResource(R.drawable.ic_cinema);
        }

        // Directions button - open Google Maps
        holder.btnDirections.setOnClickListener(v -> {
            String uri = "google.navigation:q=" + cinema.getLatitude() + "," + cinema.getLongitude();
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
            } else {
                // Fallback to browser if Google Maps is not installed
                String browserUri = "https://www.google.com/maps/dir/?api=1&destination="
                        + cinema.getLatitude() + "," + cinema.getLongitude();
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(browserUri)));
            }
        });

        // Card click - same as directions
        holder.itemView.setOnClickListener(v -> {
            holder.btnDirections.performClick();
        });
    }

    @Override
    public int getItemCount() {
        return cinemaList.size();
    }

    static class CinemaViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCinema;
        TextView tvCinemaName;
        TextView tvAddress;
        TextView tvDistance;
        TextView tvRating;
        TextView tvStatus;
        LinearLayout layoutRating;
        MaterialButton btnCall;
        MaterialButton btnDirections;

        public CinemaViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCinema = itemView.findViewById(R.id.imgCinema);
            tvCinemaName = itemView.findViewById(R.id.tvCinemaName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            layoutRating = itemView.findViewById(R.id.layoutRating);
            btnCall = itemView.findViewById(R.id.btnCall);
            btnDirections = itemView.findViewById(R.id.btnDirections);
        }
    }
}
