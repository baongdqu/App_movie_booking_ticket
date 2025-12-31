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
import com.example.app_movie_booking_ticket.extra_sound_manager;
import com.example.app_movie_booking_ticket.model.Movie;

import java.util.List;

/**
 * Adapter để hiển thị danh sách phim trong màn hình chi tiết rạp.
 * Hỗ trợ hiển thị phim đang chiếu và sắp chiếu với badge tương ứng.
 */
public class CinemaMovieAdapter extends RecyclerView.Adapter<CinemaMovieAdapter.ViewHolder> {

    private Context context;
    private List<CinemaMovie> movieList;
    private OnMovieClickListener listener;
    private String cinemaName;
    private String cinemaId;

    /**
     * Interface callback khi click vào phim
     */
    public interface OnMovieClickListener {
        void onMovieClick(Movie movie, String cinemaName, String cinemaId, int showtimeCount);
    }

    /**
     * Data class chứa thông tin phim và số suất chiếu tại rạp
     */
    public static class CinemaMovie {
        private Movie movie;
        private int showtimeCount;
        private boolean isUpcoming;
        private double rating;
        private String earliestShowtime; // Ngày chiếu sớm nhất (format: "dd/MM")

        public CinemaMovie(Movie movie, int showtimeCount, boolean isUpcoming, double rating) {
            this.movie = movie;
            this.showtimeCount = showtimeCount;
            this.isUpcoming = isUpcoming;
            this.rating = rating;
            this.earliestShowtime = null;
        }

        public CinemaMovie(Movie movie, int showtimeCount, boolean isUpcoming, double rating, String earliestShowtime) {
            this.movie = movie;
            this.showtimeCount = showtimeCount;
            this.isUpcoming = isUpcoming;
            this.rating = rating;
            this.earliestShowtime = earliestShowtime;
        }

        public Movie getMovie() {
            return movie;
        }

        public int getShowtimeCount() {
            return showtimeCount;
        }

        public boolean isUpcoming() {
            return isUpcoming;
        }

        public double getRating() {
            return rating;
        }

        public String getEarliestShowtime() {
            return earliestShowtime;
        }

        public void setEarliestShowtime(String earliestShowtime) {
            this.earliestShowtime = earliestShowtime;
        }
    }

    public CinemaMovieAdapter(Context context, List<CinemaMovie> movieList,
            String cinemaName, String cinemaId,
            OnMovieClickListener listener) {
        this.context = context;
        this.movieList = movieList;
        this.cinemaName = cinemaName;
        this.cinemaId = cinemaId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cinema_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CinemaMovie cinemaMovie = movieList.get(position);
        Movie movie = cinemaMovie.getMovie();

        // Hiển thị tên phim
        holder.tvMovieTitle.setText(movie.getTitle());

        // Hiển thị poster
        Glide.with(context)
                .load(movie.getPoster())
                .placeholder(R.drawable.ic_default_poster)
                .error(R.drawable.ic_default_poster)
                .centerCrop()
                .into(holder.imgMoviePoster);

        // Hiển thị rating
        if (cinemaMovie.getRating() > 0) {
            holder.tvMovieRating.setText(String.format("%.1f", cinemaMovie.getRating()));
        } else {
            holder.tvMovieRating.setText(String.format("%.1f", movie.getImdb()));
        }

        // Hiển thị số suất chiếu và ngày chiếu sớm nhất
        String showtimeText = cinemaMovie.getShowtimeCount() + " suất";
        if (cinemaMovie.getEarliestShowtime() != null && !cinemaMovie.getEarliestShowtime().isEmpty()) {
            showtimeText += " • " + cinemaMovie.getEarliestShowtime();
        }
        holder.tvShowtimeCount.setText(showtimeText);

        // Hiển thị badge trạng thái
        if (cinemaMovie.isUpcoming()) {
            holder.tvStatusBadge.setVisibility(View.VISIBLE);
            holder.tvStatusBadge.setText("Sắp chiếu");
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_upcoming);
            holder.viewUpcomingOverlay.setVisibility(View.VISIBLE);
        } else {
            holder.tvStatusBadge.setVisibility(View.GONE);
            holder.viewUpcomingOverlay.setVisibility(View.GONE);
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(context);
            if (listener != null) {
                listener.onMovieClick(movie, cinemaName, cinemaId, cinemaMovie.getShowtimeCount());
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList != null ? movieList.size() : 0;
    }

    public void updateList(List<CinemaMovie> newList) {
        this.movieList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMoviePoster;
        TextView tvMovieTitle;
        TextView tvMovieRating;
        TextView tvShowtimeCount;
        TextView tvStatusBadge;
        View viewUpcomingOverlay;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMoviePoster = itemView.findViewById(R.id.imgMoviePoster);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvMovieRating = itemView.findViewById(R.id.tvMovieRating);
            tvShowtimeCount = itemView.findViewById(R.id.tvShowtimeCount);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            viewUpcomingOverlay = itemView.findViewById(R.id.viewUpcomingOverlay);
        }
    }
}
