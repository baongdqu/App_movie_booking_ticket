package com.example.app_movie_booking_ticket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_movie_booking_ticket.databinding.ItemAllMovieBinding;
import com.example.app_movie_booking_ticket.databinding.ItemCastBinding;
import com.example.app_movie_booking_ticket.model.extra_Movie;

import java.util.List;

public class CastListAdapter extends RecyclerView.Adapter<CastListAdapter.CastViewHolder> {
    private Context context;
    private List<extra_Movie.Cast> casts;

    public CastListAdapter(Context context, List<extra_Movie.Cast> casts)
    {
        this.context = context;
        this.casts = casts;
    }
    public static class CastViewHolder extends RecyclerView.ViewHolder{
        ItemCastBinding binding;
        public CastViewHolder(ItemCastBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemCastBinding binding = ItemCastBinding.inflate(inflater, parent, false);
        return new CastListAdapter.CastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        extra_Movie.Cast cast = casts.get(position);
        holder.binding.textView2.setText(cast.getActor());
        Glide.with(context)
                .load(cast.getPicUrl())
                .into(holder.binding.imageView);
    }

    @Override
    public int getItemCount() {
        return casts.size();
    }
}
