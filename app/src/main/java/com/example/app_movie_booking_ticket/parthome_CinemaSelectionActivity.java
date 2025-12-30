package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_movie_booking_ticket.adapter.CinemaSelectionAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Activity for selecting cinema before seat selection
 * Flow: Movie Detail -> Cinema Selection -> Seat Selection -> Payment
 */
public class parthome_CinemaSelectionActivity extends AppCompatActivity {

    private TextView tvMovieTitle;
    private ImageView ivMoviePoster;
    private LinearLayout layoutDates, layoutTimes;
    private RecyclerView recyclerCinemas;
    private Button btnContinue;

    private String movieTitle;
    private String movieID;
    private String posterUrl;
    private String selectedDate = "";
    private String selectedTime = "";
    private String selectedCinemaId = "";
    private String selectedCinemaName = "";
    private String selectedCinemaAddress = "";
    private int selectedPrice = 0;

    private DatabaseReference dbRef;
    private CinemaSelectionAdapter cinemaAdapter;
    private List<Map<String, Object>> cinemaList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parthome_cinema_selection);

        // Get intents
        movieTitle = getIntent().getStringExtra("movieTitle");
        movieID = getIntent().getStringExtra("movieID");
        posterUrl = getIntent().getStringExtra("posterUrl");

        if (movieTitle == null || movieTitle.isEmpty()) {
            movieTitle = getString(R.string.movie_name);
        }

        // Init views
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        ivMoviePoster = findViewById(R.id.ivMoviePoster);
        layoutDates = findViewById(R.id.layoutDates);
        layoutTimes = findViewById(R.id.layoutTimes);
        recyclerCinemas = findViewById(R.id.recyclerCinemas);
        btnContinue = findViewById(R.id.btnContinue);

        // Set movie info
        tvMovieTitle.setText(movieTitle);
        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(this).load(posterUrl).into(ivMoviePoster);
        }

        // Init RecyclerView
        recyclerCinemas.setLayoutManager(new LinearLayoutManager(this));
        cinemaAdapter = new CinemaSelectionAdapter(this, cinemaList,
                (cinemaId, name, address, price) -> {
                    selectedCinemaId = cinemaId;
                    selectedCinemaName = name;
                    selectedCinemaAddress = address;
                    selectedPrice = price;
                    updateContinueButton();
                });
        recyclerCinemas.setAdapter(cinemaAdapter);

        // Database reference - sanitize movie title to match Firebase key format
        String sanitizedTitle = sanitizeFirebaseKey(movieTitle);
        android.util.Log.d("CinemaSelection", "Original title: " + movieTitle);
        android.util.Log.d("CinemaSelection", "Sanitized title: " + sanitizedTitle);
        dbRef = FirebaseDatabase.getInstance().getReference("Bookings").child(sanitizedTitle);

        // Load dates
        loadAvailableDates();

        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            finish();
        });

        // Continue button
        btnContinue.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);

            if (selectedCinemaId.isEmpty()) {
                Toast.makeText(this, getString(R.string.please_select_date_time), Toast.LENGTH_SHORT).show();
                return;
            }

            // Navigate to seat selection
            Intent intent = new Intent(this, parthome_SeatSelectionActivity.class);
            intent.putExtra("movieID", movieID);
            intent.putExtra("movieTitle", movieTitle);
            intent.putExtra("posterUrl", posterUrl);
            intent.putExtra("date", selectedDate);
            intent.putExtra("time", selectedTime);
            intent.putExtra("cinemaId", selectedCinemaId);
            intent.putExtra("cinemaName", selectedCinemaName);
            intent.putExtra("cinemaAddress", selectedCinemaAddress);
            intent.putExtra("pricePerSeat", selectedPrice);
            startActivity(intent);
        });
    }

    private void loadAvailableDates() {
        android.util.Log.d("CinemaSelection", "Loading dates from: " + dbRef.toString());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                android.util.Log.d("CinemaSelection", "Snapshot exists: " + snapshot.exists());
                android.util.Log.d("CinemaSelection", "Children count: " + snapshot.getChildrenCount());
                if (!snapshot.exists()) {
                    Toast.makeText(parthome_CinemaSelectionActivity.this,
                            getString(R.string.toast_no_schedule), Toast.LENGTH_SHORT).show();
                    return;
                }

                layoutDates.removeAllViews();
                Set<String> uniqueDates = new HashSet<>();

                for (DataSnapshot timeSnap : snapshot.getChildren()) {
                    String key = timeSnap.getKey();
                    if (key != null && key.contains("_")) {
                        String date = key.split("_")[0];
                        uniqueDates.add(date);
                    }
                }

                // Create date buttons
                for (String date : uniqueDates) {
                    Button btnDate = new Button(parthome_CinemaSelectionActivity.this);
                    btnDate.setText(date);
                    btnDate.setBackgroundResource(R.drawable.bg_date_time_selector);
                    btnDate.setTextColor(Color.BLACK);
                    btnDate.setPadding(40, 16, 40, 16);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(12, 8, 12, 8);
                    btnDate.setLayoutParams(params);

                    btnDate.setOnClickListener(v -> {
                        extra_sound_manager.playUiClick(parthome_CinemaSelectionActivity.this);

                        // Reset other buttons
                        for (int i = 0; i < layoutDates.getChildCount(); i++) {
                            layoutDates.getChildAt(i).setSelected(false);
                        }
                        btnDate.setSelected(true);
                        selectedDate = date;

                        // Reset time and cinema
                        layoutTimes.removeAllViews();
                        cinemaList.clear();
                        cinemaAdapter.notifyDataSetChanged();
                        selectedTime = "";
                        selectedCinemaId = "";
                        updateContinueButton();

                        loadShowtimesForDate(date);
                    });

                    layoutDates.addView(btnDate);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void loadShowtimesForDate(String date) {
        layoutTimes.removeAllViews();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot timeSnap : snapshot.getChildren()) {
                    String key = timeSnap.getKey();
                    if (key != null && key.startsWith(date)) {
                        String time = key.split("_")[1];

                        Button btnTime = new Button(parthome_CinemaSelectionActivity.this);
                        btnTime.setText(time);
                        btnTime.setBackgroundResource(R.drawable.bg_date_time_selector);
                        btnTime.setTextColor(Color.BLACK);
                        btnTime.setPadding(40, 16, 40, 16);

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(12, 8, 12, 8);
                        btnTime.setLayoutParams(params);

                        btnTime.setOnClickListener(v -> {
                            extra_sound_manager.playUiClick(parthome_CinemaSelectionActivity.this);

                            // Reset other buttons
                            for (int i = 0; i < layoutTimes.getChildCount(); i++) {
                                layoutTimes.getChildAt(i).setSelected(false);
                            }
                            btnTime.setSelected(true);
                            selectedTime = time;

                            // Reset cinema selection
                            selectedCinemaId = "";
                            cinemaAdapter.clearSelection();
                            updateContinueButton();

                            loadCinemasForShowtime(date, time);
                        });

                        layoutTimes.addView(btnTime);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void loadCinemasForShowtime(String date, String time) {
        String showtimeKey = date + "_" + time;

        dbRef.child(showtimeKey).child("cinemas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                cinemaList.clear();

                if (!snapshot.exists()) {
                    Toast.makeText(parthome_CinemaSelectionActivity.this,
                            getString(R.string.no_cinema_available), Toast.LENGTH_SHORT).show();
                    cinemaAdapter.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot cinemaSnap : snapshot.getChildren()) {
                    Map<String, Object> cinema = new HashMap<>();
                    cinema.put("id", cinemaSnap.getKey());
                    cinema.put("name", cinemaSnap.child("name").getValue(String.class));
                    cinema.put("address", cinemaSnap.child("address").getValue(String.class));
                    cinema.put("pricePerSeat", cinemaSnap.child("pricePerSeat").getValue(Long.class));
                    cinemaList.add(cinema);
                }

                cinemaAdapter.updateData(cinemaList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void updateContinueButton() {
        boolean canContinue = !selectedDate.isEmpty() &&
                !selectedTime.isEmpty() &&
                !selectedCinemaId.isEmpty();
        btnContinue.setEnabled(canContinue);
        btnContinue.setAlpha(canContinue ? 1f : 0.5f);
    }

    /**
     * Remove invalid Firebase key characters: $ # [ ] . /
     */
    private String sanitizeFirebaseKey(String key) {
        if (key == null)
            return "";
        return key.replaceAll("[$#\\[\\]./]", "").trim();
    }
}
