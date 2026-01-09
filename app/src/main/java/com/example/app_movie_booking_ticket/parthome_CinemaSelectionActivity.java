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
    // Thêm biến để kiểm tra luồng
    private String preselectedCinemaId = "";
    private boolean fromCinemaDetail = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parthome_cinema_selection);

        // 1. NHẬN INTENT VÀ GÁN BIẾN KIỂM TRA LUỒNG (QUAN TRỌNG)
        preselectedCinemaId = getIntent().getStringExtra("preselectedCinemaId");
        String preName = getIntent().getStringExtra("preselectedCinemaName");
        // Phải gán giá trị cho fromCinemaDetail ở đây
        fromCinemaDetail = (preselectedCinemaId != null && !preselectedCinemaId.isEmpty());

        movieTitle = getIntent().getStringExtra("movieTitle");
        movieID = getIntent().getStringExtra("movieID");
        posterUrl = getIntent().getStringExtra("posterUrl");

        // 2. INIT VIEWS
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        ivMoviePoster = findViewById(R.id.ivMoviePoster);
        layoutDates = findViewById(R.id.layoutDates);
        layoutTimes = findViewById(R.id.layoutTimes);
        recyclerCinemas = findViewById(R.id.recyclerCinemas);
        btnContinue = findViewById(R.id.btnContinue);
        TextView tvLabelCinema = findViewById(R.id.tvLabelCinema);

        // 3. THIẾT LẬP GIAO DIỆN DỰA TRÊN LUỒNG
        if (fromCinemaDetail) {
            // Gán dữ liệu rạp mặc định để nút "Tiếp tục" có thể sáng
            selectedCinemaId = preselectedCinemaId;
            selectedCinemaName = preName;
            selectedPrice = getIntent().getIntExtra("pricePerSeat", 0);

            // Cập nhật tiêu đề kèm tên rạp
            tvMovieTitle.setText(movieTitle + "\n@" + shortenCinemaName(preName));
            // Ẩn tiêu đề "Chọn rạp chiếu" và danh sách rạp
            if (tvLabelCinema != null) tvLabelCinema.setVisibility(View.GONE);
            recyclerCinemas.setVisibility(View.GONE);
        } else {
            // Chỉ gán tên phim đơn thuần nếu không đi từ CinemaDetail
            tvMovieTitle.setText(movieTitle);
        }

        // Load poster
        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(this).load(posterUrl).into(ivMoviePoster);
        }

        // 4. INIT ADAPTER
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

        // Database reference
        String sanitizedTitle = sanitizeFirebaseKey(movieTitle);
        dbRef = FirebaseDatabase.getInstance().getReference("Bookings").child(sanitizedTitle);

        loadAvailableDates();

        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Continue button
        btnContinue.setOnClickListener(v -> {
            if (selectedCinemaId.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn suất chiếu", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, parthome_SeatSelectionActivity.class);
            intent.putExtra("movieID", movieID);
            intent.putExtra("movieTitle", movieTitle);
            intent.putExtra("posterUrl", posterUrl);
            intent.putExtra("date", selectedDate);
            intent.putExtra("time", selectedTime);
            intent.putExtra("cinemaId", selectedCinemaId);
            intent.putExtra("cinemaName", selectedCinemaName);
            intent.putExtra("pricePerSeat", selectedPrice);
            startActivity(intent);
        });
    }
    private String shortenCinemaName(String name) {
        if (name == null) return "";
        return name.replace("Thủ Đức", "").replace("Thành phố", "").trim();
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
                            if (!fromCinemaDetail) {
                                selectedCinemaId = "";
                                cinemaAdapter.clearSelection();
                            } else {
                                // Nếu đi từ rạp, giữ nguyên preselectedCinemaId
                                selectedCinemaId = preselectedCinemaId;
                            }
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
                    cinemaAdapter.updateData(cinemaList);
                    updateContinueButton(); // Cập nhật để vô hiệu hóa nút nếu không có rạp
                    return;
                }

                for (DataSnapshot cinemaSnap : snapshot.getChildren()) {
                    String id = cinemaSnap.getKey();

                    if (fromCinemaDetail && !id.equals(preselectedCinemaId)) {
                        continue;
                    }

                    Map<String, Object> cinema = new HashMap<>();
                    cinema.put("id", id);
                    cinema.put("name", cinemaSnap.child("name").getValue(String.class));
                    cinema.put("address", cinemaSnap.child("address").getValue(String.class));
                    cinema.put("pricePerSeat", cinemaSnap.child("pricePerSeat").getValue(Long.class));
                    cinemaList.add(cinema);

                    if (fromCinemaDetail && id.equals(preselectedCinemaId)) {
                        selectedCinemaId = id;
                        selectedCinemaName = (String) cinema.get("name");
                        selectedCinemaAddress = (String) cinema.get("address");
                        selectedPrice = ((Long) cinema.get("pricePerSeat")).intValue();
                    }
                }

                cinemaAdapter.updateData(cinemaList);

                if (fromCinemaDetail && !selectedCinemaId.isEmpty()) {
                    cinemaAdapter.setPreSelectedCinema(selectedCinemaId);
                    //  QUAN TRỌNG: Gọi hàm này để nút Tiếp tục sáng lên ngay lập tức
                    updateContinueButton();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void updateContinueButton() {
        android.util.Log.d("DEBUG_BTN", "Date: " + selectedDate + " | Time: " + selectedTime + " | ID: " + selectedCinemaId);
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
