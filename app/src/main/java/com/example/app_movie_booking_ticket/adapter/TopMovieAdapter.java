package com.example.app_movie_booking_ticket.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_movie_booking_ticket.R;
import com.example.app_movie_booking_ticket.parthome_movie_detail;
import com.example.app_movie_booking_ticket.extra_sound_manager;
import com.example.app_movie_booking_ticket.extra.MovieCacheManager;
import com.example.app_movie_booking_ticket.model.Movie;

import java.util.List;

public class TopMovieAdapter extends RecyclerView.Adapter<TopMovieAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> movieList;

    public TopMovieAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;

    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.parthome_item_top_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.tvMovieName.setText(movie.getTitle());
        Glide.with(context)
                .load(movie.getPoster())
                .placeholder(R.drawable.ic_default_poster)
                .into(holder.imgMovie);

        // Kiểm tra xem phim đã chiếu chưa
        boolean isExpired = MovieCacheManager.getInstance().isMovieExpired(movie.getMovieID());

        if (isExpired) {
            // Hiển thị badge và overlay cho phim đã chiếu
            holder.tvExpiredBadge.setVisibility(View.VISIBLE);
            holder.viewExpiredOverlay.setVisibility(View.VISIBLE);
        } else {
            holder.tvExpiredBadge.setVisibility(View.GONE);
            holder.viewExpiredOverlay.setVisibility(View.GONE);
        }

        // Phát âm thanh khi click
        holder.itemView.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(v.getContext());
            Intent intent = new Intent(context, parthome_movie_detail.class);
            intent.putExtra("movie", movie);
            intent.putExtra("isExpired", isExpired); // Truyền trạng thái đã chiếu
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void updateList(List<Movie> newList) {
        this.movieList = newList;
        notifyDataSetChanged();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMovie;
        TextView tvMovieName;
        TextView tvExpiredBadge;
        View viewExpiredOverlay;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMovie = itemView.findViewById(R.id.imgMovie);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            tvExpiredBadge = itemView.findViewById(R.id.tvExpiredBadge);
            viewExpiredOverlay = itemView.findViewById(R.id.viewExpiredOverlay);
        }
    }
}
