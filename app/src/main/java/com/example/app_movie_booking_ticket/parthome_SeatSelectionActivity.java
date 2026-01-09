package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Activity Chọn ghế (Seat Selection)
 * Cho phép người dùng chọn ngày, giờ chiếu và vị trí ghế ngồi.
 * Tính toán tổng tiền vé dựa trên số ghế đã chọn.
 */
public class parthome_SeatSelectionActivity extends AppCompatActivity {

    private TextView tvMovieTitle, tvTotalPrice;
    private LinearLayout layoutDates, layoutTimes;
    private GridLayout gridSeats;
    private Button btnContinue;
    private String posterUrl;
    private String movieTitle;
    private String movieID;
    private String selectedDate = "";
    private String selectedShowtime = "";
    private String selectedCinemaId = "";
    private String selectedCinemaName = "";
    private int pricePerSeat = 0;
    private List<String> selectedSeats = new ArrayList<>();
    private boolean fromCinemaSelection = false;

    private DatabaseReference dbRef;

    /**
     * Khởi tạo màn hình chọn ghế.
     * Load danh sách ngày/giờ chiếu từ Firebase hoặc trực tiếp từ Cinema Selection.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parthome_seat_selection);

        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        gridSeats = findViewById(R.id.gridSeats);
        layoutDates = findViewById(R.id.layoutDates);
        layoutTimes = findViewById(R.id.layoutTimes);
        btnContinue = findViewById(R.id.btnContinue);

        movieTitle = getIntent().getStringExtra("movieTitle");
        if (movieTitle == null || movieTitle.isEmpty())
            movieTitle = getString(R.string.movie_name);
        posterUrl = getIntent().getStringExtra("posterUrl");
        movieID = getIntent().getStringExtra("movieID");

        // Check if coming from Cinema Selection
        selectedCinemaId = getIntent().getStringExtra("cinemaId");
        selectedCinemaName = getIntent().getStringExtra("cinemaName");
        selectedDate = getIntent().getStringExtra("date");
        selectedShowtime = getIntent().getStringExtra("time");
        pricePerSeat = getIntent().getIntExtra("pricePerSeat", 0);

        fromCinemaSelection = selectedCinemaId != null && !selectedCinemaId.isEmpty();

        if (fromCinemaSelection) {
            // Show cinema name in title
            tvMovieTitle.setText(movieTitle + "\n" + selectedCinemaName);
            // Hide date/time selection since already selected
            layoutDates.setVisibility(View.GONE);
            layoutTimes.setVisibility(View.GONE);
            findViewById(R.id.labelDate).setVisibility(View.GONE);
            findViewById(R.id.labelTime).setVisibility(View.GONE);
            // Load seats directly
            loadSeatsFromCinema();
        } else {
            tvMovieTitle.setText(movieTitle);
            dbRef = FirebaseDatabase.getInstance().getReference("Bookings").child(movieTitle);
            loadAvailableDates();
        }

        btnContinue.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                extra_sound_manager.playError(this);
                Toast.makeText(this, getString(R.string.toast_select_seat), Toast.LENGTH_SHORT).show();
                return;
            }
            extra_sound_manager.playUiClick(this);
            int total = selectedSeats.size() * pricePerSeat;
            Toast.makeText(this, String.format(getString(R.string.toast_seat_total), selectedSeats.toString(),
                    String.valueOf(total)), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(parthome_SeatSelectionActivity.this, parthome_PaymentActivity.class);
            intent.putExtra("movieID", movieID);
            intent.putExtra("movieTitle", movieTitle);
            intent.putExtra("date", selectedDate);
            intent.putExtra("time", selectedShowtime);
            intent.putExtra("cinemaId", selectedCinemaId);
            intent.putExtra("cinemaName", selectedCinemaName);
            intent.putStringArrayListExtra("seats", new ArrayList<>(selectedSeats));
            intent.putExtra("pricePerSeat", pricePerSeat);
            intent.putExtra("totalPrice", total);
            intent.putExtra("posterUrl", posterUrl);
            startActivity(intent);
        });

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        extra_sound_manager.playUiClick(this);
        super.onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        extra_sound_manager.playUiClick(this);
    }

    private void loadSeatsFromCinema() {
        String showtimeKey = selectedDate + "_" + selectedShowtime;
        String sanitizedTitle = sanitizeFirebaseKey(movieTitle);
        DatabaseReference seatRef = FirebaseDatabase.getInstance()
                .getReference("Bookings")
                .child(sanitizedTitle)
                .child(showtimeKey)
                .child("cinemas")
                .child(selectedCinemaId)
                .child("seats");

        seatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                displaySeats(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void displaySeats(DataSnapshot seatsSnapshot) {
        gridSeats.removeAllViews();

        // Căn giữa GridLayout
        ViewGroup.LayoutParams lp = gridSeats.getLayoutParams();
        if (lp instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) lp;
            llp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            llp.gravity = android.view.Gravity.CENTER_HORIZONTAL;
            gridSeats.setLayoutParams(llp);
        }

        gridSeats.setAlignmentMode(GridLayout.ALIGN_MARGINS);
        gridSeats.setUseDefaultMargins(true);
        gridSeats.setColumnCount(8);

        selectedSeats.clear();
        tvTotalPrice.setText(getString(R.string.total_price));

        if (seatsSnapshot.exists()) {
            for (DataSnapshot seat : seatsSnapshot.getChildren()) {
                String seatName = seat.getKey();
                String status = seat.getValue(String.class);

                Button seatBtn = new Button(parthome_SeatSelectionActivity.this);
                seatBtn.setText(seatName);
                seatBtn.setTextSize(12);
                seatBtn.setTextColor(Color.WHITE);
                seatBtn.setAllCaps(false);
                seatBtn.setPadding(0, 0, 0, 0);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 90;
                params.height = 90;
                params.setMargins(8, 8, 8, 8);
                seatBtn.setLayoutParams(params);

                seatBtn.setBackgroundResource(R.drawable.bg_seat_selector);

                if ("booked".equals(status)) {
                    seatBtn.setEnabled(false);
                    seatBtn.setSelected(false);
                } else {
                    seatBtn.setEnabled(true);
                    seatBtn.setSelected(false);
                    seatBtn.setOnClickListener(v -> toggleSeat(seatBtn, seatName));
                }

                gridSeats.addView(seatBtn);
            }
        }
    }

    // Lấy danh sách các ngày chiếu có thật trong database
    private void loadAvailableDates() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    extra_sound_manager.playError(parthome_SeatSelectionActivity.this);
                    Toast.makeText(parthome_SeatSelectionActivity.this, getString(R.string.toast_no_schedule),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                layoutDates.removeAllViews();
                Set<String> uniqueDates = new HashSet<>();

                for (DataSnapshot timeSnap : snapshot.getChildren()) {
                    String key = timeSnap.getKey(); // ví dụ: 2025-11-08_15:15
                    if (key != null && key.contains("_")) {
                        String date = key.split("_")[0];
                        uniqueDates.add(date);
                    }
                }

                // Tạo nút chọn ngày
                for (String date : uniqueDates) {
                    Button btnDate = new Button(parthome_SeatSelectionActivity.this);
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
                        extra_sound_manager.playUiClick(parthome_SeatSelectionActivity.this);
                        // reset các nút khác
                        for (int i = 0; i < layoutDates.getChildCount(); i++) {
                            View child = layoutDates.getChildAt(i);
                            child.setSelected(false);
                        }

                        // chọn ngày mới
                        btnDate.setSelected(true);
                        selectedDate = date;

                        // reset suất chiếu & ghế
                        layoutTimes.removeAllViews();
                        gridSeats.removeAllViews();
                        selectedShowtime = "";
                        selectedSeats.clear();
                        tvTotalPrice.setText(getString(R.string.total_price));

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

    // 🔹 Sau khi chọn ngày, hiển thị các giờ chiếu tương ứng
    private void loadShowtimesForDate(String date) {
        layoutTimes.removeAllViews();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot timeSnap : snapshot.getChildren()) {
                    String key = timeSnap.getKey(); // ví dụ: 2025-11-08_15:15
                    if (key != null && key.startsWith(date)) {
                        String time = key.split("_")[1];

                        Button btnTime = new Button(parthome_SeatSelectionActivity.this);
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
                            extra_sound_manager.playUiClick(parthome_SeatSelectionActivity.this);
                            // reset các suất cũ
                            for (int i = 0; i < layoutTimes.getChildCount(); i++) {
                                View child = layoutTimes.getChildAt(i);
                                child.setSelected(false);
                            }

                            // chọn suất hiện tại
                            btnTime.setSelected(true);
                            selectedShowtime = time;

                            // reset ghế
                            gridSeats.removeAllViews();
                            selectedSeats.clear();
                            tvTotalPrice.setText(getString(R.string.total_price));

                            loadSeats(date, time);
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

    // 🔹 Load ghế của ngày + giờ cụ thể
    private void loadSeats(String date, String time) {
        DatabaseReference seatRef = dbRef.child(date + "_" + time);
        seatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                gridSeats.removeAllViews();

                // Căn giữa GridLayout trong LinearLayout cha
                ViewGroup.LayoutParams lp = gridSeats.getLayoutParams();
                if (lp instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) lp;
                    llp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    llp.gravity = android.view.Gravity.CENTER_HORIZONTAL;
                    gridSeats.setLayoutParams(llp);
                }

                gridSeats.setAlignmentMode(GridLayout.ALIGN_MARGINS);
                gridSeats.setUseDefaultMargins(true);
                gridSeats.setColumnCount(8);

                selectedSeats.clear();
                tvTotalPrice.setText(getString(R.string.total_price));

                if (snapshot.exists()) {
                    pricePerSeat = snapshot.child("pricePerSeat").getValue(Integer.class);
                    DataSnapshot seatsSnap = snapshot.child("seats");
                    for (DataSnapshot seat : seatsSnap.getChildren()) {
                        String seatName = seat.getKey();
                        String status = seat.getValue(String.class);

                        Button seatBtn = new Button(parthome_SeatSelectionActivity.this);
                        seatBtn.setText(seatName);
                        seatBtn.setTextSize(12);
                        seatBtn.setTextColor(Color.WHITE);
                        seatBtn.setAllCaps(false);
                        seatBtn.setPadding(0, 0, 0, 0);

                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        params.width = 90;
                        params.height = 90;
                        params.setMargins(8, 8, 8, 8);
                        seatBtn.setLayoutParams(params);

                        seatBtn.setBackgroundResource(R.drawable.bg_seat_selector);

                        // Trạng thái ban đầu
                        if ("booked".equals(status)) {
                            seatBtn.setEnabled(false);
                            seatBtn.setSelected(false);
                        } else {
                            seatBtn.setEnabled(true);
                            seatBtn.setSelected(false);
                            seatBtn.setOnClickListener(v -> toggleSeat(seatBtn, seatName));
                        }

                        gridSeats.addView(seatBtn);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void toggleSeat(Button seatBtn, String seatName) {
        if (selectedSeats.contains(seatName)) {
            selectedSeats.remove(seatName);
            seatBtn.setSelected(false);
        } else {
            selectedSeats.add(seatName);
            seatBtn.setSelected(true);
        }
        extra_sound_manager.playUiClick(this);
        tvTotalPrice.setText(
                String.format(getString(R.string.price_format), String.valueOf(selectedSeats.size() * pricePerSeat)));
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
