package com.example.app_movie_booking_ticket.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_movie_booking_ticket.activities_4_movie_detail;
import com.example.app_movie_booking_ticket.databinding.ItemAllMovieBinding;
import com.example.app_movie_booking_ticket.model.extra_Movie;

import java.util.List;

public class AllMoviesAdapter extends RecyclerView.Adapter<AllMoviesAdapter.AllMoviesViewHolder> {
    private Context context;
    private List<extra_Movie> movieList;

    public AllMoviesAdapter(Context context, List<extra_Movie> movieList)
    {
        this.context = context;
        this.movieList = movieList;
    }

    public static class AllMoviesViewHolder extends RecyclerView.ViewHolder {
        ItemAllMovieBinding binding;
        public AllMoviesViewHolder(ItemAllMovieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public AllMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemAllMovieBinding binding = ItemAllMovieBinding.inflate(inflater, parent, false);
        return new AllMoviesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AllMoviesViewHolder holder, int position) {
        extra_Movie movie = movieList.get(position);
        holder.binding.textTitle.setText(movie.getTitle());
        if (movie.getGenre() != null && !movie.getGenre().isEmpty()) {
            String genres = String.join(", ", movie.getGenre());
            holder.binding.textGenre.setText(genres);
        } else {
            holder.binding.textGenre.setText("N/A");
        }
        holder.binding.textYear.setText(String.valueOf(movie.getYear()));
        holder.binding.textDuration.setText(movie.getTime());
        Glide.with(context)
                .load(movie.getPoster())
                .into(holder.binding.imagePoster);

        holder.binding.buttonDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, activities_4_movie_detail.class);
            intent.putExtra("movie", movie);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }
}
