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
import com.example.app_movie_booking_ticket.activities_4_movie_detail;
import com.example.app_movie_booking_ticket.model.Movie;

import java.util.List;

import com.example.app_movie_booking_ticket.SeatSelectionActivity;

public class AllMoviesAdapter extends RecyclerView.Adapter<AllMoviesAdapter.MovieViewHolder> {

    private final Context context;
    private final List<Movie> movieList;

    public AllMoviesAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_all_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        holder.tvMovieName.setText(movie.getTitle());
        holder.tvMovieGenre.setText(movie.getGenre() != null ? String.join(", ", movie.getGenre()) : "");
        holder.tvMovieTime.setText(movie.getTime());
        holder.tvMovieDate.setText(String.valueOf(movie.getYear()));
        Glide.with(context).load(movie.getPoster()).into(holder.imgMovie);



        holder.btnDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, activities_4_movie_detail.class);
            intent.putExtra("movie", movie);
            context.startActivity(intent);
        });
        holder.btnBuy.setOnClickListener(v -> {
            Intent intent = new Intent(context, SeatSelectionActivity.class);
            intent.putExtra("movieTitle", movie.getTitle()); // gửi tên phim
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMovie;
        TextView tvMovieName, tvMovieGenre, tvMovieTime, tvMovieDate;
        Button btnDetail, btnBuy;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMovie = itemView.findViewById(R.id.imgMovie);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            tvMovieGenre = itemView.findViewById(R.id.tvMovieGenre);
            tvMovieTime = itemView.findViewById(R.id.tvMovieTime);
            tvMovieDate = itemView.findViewById(R.id.tvMovieDate);
            btnDetail = itemView.findViewById(R.id.btnDetail);
            btnBuy = itemView.findViewById(R.id.btnBuy);
        }
    }
}
