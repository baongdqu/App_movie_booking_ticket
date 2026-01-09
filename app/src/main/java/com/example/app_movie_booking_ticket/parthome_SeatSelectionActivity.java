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
public class parthome_SeatSelectionActivity extends extra_manager_language {

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
        extra_themeutils.applySavedTheme(this);
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
        dbRef = FirebaseDatabase.getInstance().getReference("Bookings").child(sanitizeFirebaseKey(movieTitle));
        // Check if coming from Cinema Selection
        selectedCinemaId = getIntent().getStringExtra("cinemaId");
        selectedCinemaName = getIntent().getStringExtra("cinemaName");
        selectedDate = getIntent().getStringExtra("date");
        selectedShowtime = getIntent().getStringExtra("time");
        pricePerSeat = getIntent().getIntExtra("pricePerSeat", 0);

        fromCinemaSelection = (selectedCinemaId != null && !selectedCinemaId.isEmpty()
                && selectedDate != null && selectedShowtime != null);

        if (fromCinemaSelection) {
            // LUỒNG 1: Đã chọn rạp, ngày, giờ từ CinemaDetail -> CinemaSelection
            tvMovieTitle.setText(movieTitle + "\n" + selectedCinemaName);

            // KỸ THUẬT: Ẩn toàn bộ các layout chọn lại để tránh người dùng chọn sai rạp
            // khác
            layoutDates.setVisibility(View.GONE);
            layoutTimes.setVisibility(View.GONE);
            findViewById(R.id.labelDate).setVisibility(View.GONE);
            findViewById(R.id.labelTime).setVisibility(View.GONE);

            // Nạp sơ đồ ghế ngay lập tức
            loadSeats(selectedDate, selectedShowtime);
        } else {
            // LUỒNG 2: Đi từ Movie Detail (Menu) -> Phải chọn từ đầu
            tvMovieTitle.setText(movieTitle);

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

    private void loadSeatsFromCinema() {
        String showtimeKey = selectedDate + "_" + selectedShowtime;
        String sanitizedTitle = sanitizeFirebaseKey(movieTitle);

        // Trỏ đến danh sách rạp của suất chiếu đó
        DatabaseReference cinemasRef = FirebaseDatabase.getInstance()
                .getReference("Bookings")
                .child(sanitizedTitle)
                .child(showtimeKey)
                .child("cinemas");

        cinemasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists())
                    return;

                String actualDbKey = "";
                // Tìm Key thực tế khớp với ID truyền vào
                for (DataSnapshot cinemaSnap : snapshot.getChildren()) {
                    String dbKey = cinemaSnap.getKey();
                    if (selectedCinemaId.contains(dbKey) || dbKey.contains(selectedCinemaId)) {
                        actualDbKey = dbKey;
                        break;
                    }
                }

                if (!actualDbKey.isEmpty()) {
                    DataSnapshot targetCinema = snapshot.child(actualDbKey);

                    // Lấy giá tiền từ rạp này
                    Long price = targetCinema.child("pricePerSeat").getValue(Long.class);
                    pricePerSeat = (price != null) ? price.intValue() : 0;

                    // Hiển thị ghế
                    displaySeats(targetCinema.child("seats"));
                } else {
                    Toast.makeText(parthome_SeatSelectionActivity.this, getString(R.string.toast_cinema_not_found),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void displaySeats(DataSnapshot seatsSnapshot) {
        gridSeats.removeAllViews();

        // Cấu hình giao diện Grid
        gridSeats.setColumnCount(8);
        gridSeats.setAlignmentMode(GridLayout.ALIGN_MARGINS);
        gridSeats.setUseDefaultMargins(true);

        if (seatsSnapshot.exists()) {
            for (DataSnapshot seat : seatsSnapshot.getChildren()) {
                String seatName = seat.getKey();
                String status = seat.getValue(String.class);

                Button seatBtn = new Button(this);
                seatBtn.setText(seatName);
                seatBtn.setTextSize(12);
                seatBtn.setTextColor(Color.WHITE);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 90;
                params.height = 90;
                params.setMargins(8, 8, 8, 8);
                seatBtn.setLayoutParams(params);

                seatBtn.setBackgroundResource(R.drawable.bg_seat_selector);

                // Kiểm tra trạng thái ghế
                if ("booked".equals(status)) {
                    seatBtn.setEnabled(false);
                    seatBtn.setAlpha(0.5f); // Làm mờ ghế đã đặt
                } else {
                    seatBtn.setEnabled(true);
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
                    // 1. Kiểm tra rạp có tồn tại trong suất chiếu này không
                    boolean shouldAddDate = false;

                    // Trường hợp LUỒNG 2: Đi từ Movie Detail (chưa chọn rạp cụ thể) -> Hiện tất cả
                    // ngày
                    if (selectedCinemaId == null || selectedCinemaId.isEmpty()) {
                        shouldAddDate = true;
                    } else {
                        // Trường hợp LUỒNG 1: Đã có rạp (từ Cinema Selection) -> Chỉ hiện ngày rạp đó
                        // có suất
                        DataSnapshot cinemasSnap = timeSnap.child("cinemas");
                        for (DataSnapshot c : cinemasSnap.getChildren()) {
                            String dbKey = c.getKey(); // Key trên Firebase (vd: cgv_giga_mall)

                            // Kỹ thuật so khớp mờ để khớp ID
                            if (selectedCinemaId.contains(dbKey) || dbKey.contains(selectedCinemaId)) {
                                shouldAddDate = true;
                                break;
                            }
                        }
                    }

                    // 2. Thêm ngày vào Set (để không bị trùng)
                    if (shouldAddDate) {
                        String key = timeSnap.getKey();
                        if (key != null && key.contains("_")) {
                            String date = key.split("_")[0];
                            uniqueDates.add(date);
                        }
                    }
                }

                // 3. Hiển thị Button (Giữ nguyên logic cũ của bạn)
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
                        for (int i = 0; i < layoutDates.getChildCount(); i++) {
                            layoutDates.getChildAt(i).setSelected(false);
                        }
                        btnDate.setSelected(true);
                        selectedDate = date;

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
                android.util.Log.e("FirebaseError", error.getMessage());
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
                    String key = timeSnap.getKey(); // Ví dụ: 2026-01-15_16:00

                    // 1. Kiểm tra suất chiếu có thuộc Ngày đang chọn không
                    if (key != null && key.startsWith(date)) {

                        // 2. 🔥 QUAN TRỌNG: Kiểm tra suất chiếu này có dành cho rạp đang chọn không?
                        boolean hasCinemaAtThisTime = false;
                        DataSnapshot cinemasSnap = timeSnap.child("cinemas");

                        for (DataSnapshot c : cinemasSnap.getChildren()) {
                            String dbKey = c.getKey();
                            // Dùng contains để so khớp mờ ID
                            if (selectedCinemaId.contains(dbKey) || dbKey.contains(selectedCinemaId)) {
                                hasCinemaAtThisTime = true;
                                break;
                            }
                        }

                        // 3. Chỉ tạo Button nếu rạp đó có suất chiếu vào giờ này
                        if (hasCinemaAtThisTime) {
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
                                for (int i = 0; i < layoutTimes.getChildCount(); i++) {
                                    layoutTimes.getChildAt(i).setSelected(false);
                                }
                                btnTime.setSelected(true);
                                selectedShowtime = time;

                                gridSeats.removeAllViews();
                                selectedSeats.clear();
                                tvTotalPrice.setText(getString(R.string.total_price));

                                loadSeats(date, time);
                            });

                            layoutTimes.addView(btnTime);
                        }
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
        String showtimeKey = date + "_" + time;
        String sanitizedTitle = sanitizeFirebaseKey(movieTitle);

        // 1. Trỏ đến node 'cinemas' của suất chiếu đó
        DatabaseReference cinemasRef = FirebaseDatabase.getInstance()
                .getReference("Bookings")
                .child(sanitizedTitle)
                .child(showtimeKey)
                .child("cinemas");

        cinemasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists())
                    return;

                String finalTargetId = "";

                // 2. KỸ THUẬT SO KHỚP MỜ: Tìm Key thực tế trên Firebase
                for (DataSnapshot cinemaSnap : snapshot.getChildren()) {
                    String dbKey = cinemaSnap.getKey(); // Ví dụ: "cgv_giga_mall"

                    // Nếu ID truyền vào chứa Key trong DB hoặc ngược lại
                    if (selectedCinemaId.contains(dbKey) || dbKey.contains(selectedCinemaId)) {
                        finalTargetId = dbKey;
                        break;
                    }
                }

                // 3. Nếu tìm thấy ID khớp, tiến hành load ghế
                if (!finalTargetId.isEmpty()) {
                    DataSnapshot targetSnapshot = snapshot.child(finalTargetId);

                    gridSeats.removeAllViews();
                    selectedSeats.clear();
                    tvTotalPrice.setText(getString(R.string.total_price));

                    // Lấy giá vé
                    Long price = targetSnapshot.child("pricePerSeat").getValue(Long.class);
                    pricePerSeat = (price != null) ? price.intValue() : 0;

                    // Hiển thị ghế
                    displaySeats(targetSnapshot.child("seats"));
                } else {
                    android.util.Log.e("SeatSelection", "Không tìm thấy rạp khớp với ID: " + selectedCinemaId);
                    Toast.makeText(parthome_SeatSelectionActivity.this, getString(R.string.toast_cinema_data_mismatch),
                            Toast.LENGTH_SHORT)
                            .show();
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
}
