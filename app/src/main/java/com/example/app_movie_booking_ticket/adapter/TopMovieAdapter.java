package com.example.app_movie_booking_ticket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_movie_booking_ticket.R;
import com.example.app_movie_booking_ticket.model.extra_Movie;

import java.util.List;

public class TopMovieAdapter extends RecyclerView.Adapter<TopMovieAdapter.MovieViewHolder> {

    private Context context;
    private List<extra_Movie> movieList;

    public TopMovieAdapter(Context context, List<extra_Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_top_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        extra_Movie movie = movieList.get(position);
        holder.tvMovieName.setText(movie.getTitle());
        Glide.with(context)
                .load(movie.getPoster())
                .placeholder(R.drawable.ic_default_poster)
                .into(holder.imgMovie);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMovie;
        TextView tvMovieName;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMovie = itemView.findViewById(R.id.imgMovie);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
        }
    }
}
