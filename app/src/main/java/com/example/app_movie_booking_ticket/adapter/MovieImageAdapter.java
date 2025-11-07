package com.example.app_movie_booking_ticket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_movie_booking_ticket.databinding.ItemCastBinding;
import com.example.app_movie_booking_ticket.databinding.ItemMovieimagesBinding;
import com.example.app_movie_booking_ticket.model.extra_Movie;

import java.util.List;

public class MovieImageAdapter  extends RecyclerView.Adapter<MovieImageAdapter.MovieImageViewHolder>{
    private Context context;
    private List<String> picturesList;

    public MovieImageAdapter(Context context, List<String> picturesList)
    {
        this.context = context;
        this.picturesList = picturesList;
    }
    public static class MovieImageViewHolder extends RecyclerView.ViewHolder{
        ItemMovieimagesBinding binding;
        public MovieImageViewHolder(ItemMovieimagesBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    @NonNull
    @Override
    public MovieImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemMovieimagesBinding binding = ItemMovieimagesBinding.inflate(inflater, parent, false);
        return new MovieImageAdapter.MovieImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieImageViewHolder holder, int position) {
        String imageUrl = picturesList.get(position);
        Glide.with(context)
                .load(imageUrl)
                .centerCrop()
                .into(holder.binding.imageView);
    }

    @Override
    public int getItemCount() {
        return picturesList != null ? picturesList.size() : 0;
    }
}
