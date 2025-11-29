package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookingConfirmationActivity extends AppCompatActivity {

    private TextView tvMovieTitle, tvDate, tvTime, tvSeats, tvTotalPrice;
    private MaterialButton btnConfirmPayment;
    private ImageView btnBack;

    private String movieTitle;
    private String date;
    private String time;
    private ArrayList<String> selectedSeats;
    private int totalPrice;

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        // Init views
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvSeats = findViewById(R.id.tvSeats);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        btnBack = findViewById(R.id.btnBack);

        // Get data from intent
        Intent intent = getIntent();
        movieTitle = intent.getStringExtra("movieTitle");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        selectedSeats = intent.getStringArrayListExtra("seats");
        totalPrice = intent.getIntExtra("totalPrice", 0);

        // Display data
        tvMovieTitle.setText(movieTitle);
        tvDate.setText(date);
        tvTime.setText(time);

        StringBuilder seatsStr = new StringBuilder();
        if (selectedSeats != null) {
            for (String seat : selectedSeats) {
                if (seatsStr.length() > 0) seatsStr.append(", ");
                seatsStr.append(seat);
            }
        }
        tvSeats.setText(seatsStr.toString());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalPrice.setText(formatter.format(totalPrice));

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        // Listeners
        btnBack.setOnClickListener(v -> finish());
        btnConfirmPayment.setOnClickListener(v -> processBooking());
    }

    private void processBooking() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đặt vé!", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = currentUser.getEmail();
        if (email == null) email = "unknown_user";
        String userKey = email.replace(".", ",");

        // 1. Save to UserBookings
        DatabaseReference userBookingsRef = dbRef.child("UserBookings").child(userKey);
        String bookingId = userBookingsRef.push().getKey();

        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("movieTitle", movieTitle);
        bookingData.put("date", date);
        bookingData.put("time", time);
        bookingData.put("seats", tvSeats.getText().toString());
        bookingData.put("totalPrice", totalPrice);
        bookingData.put("timestamp", System.currentTimeMillis());

        if (bookingId != null) {
            userBookingsRef.child(bookingId).setValue(bookingData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // 2. Update seat status in Bookings
                    updateSeatStatus();
                } else {
                    Toast.makeText(this, "Lỗi khi lưu thông tin đặt vé!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateSeatStatus() {
        DatabaseReference seatsRef = dbRef.child("Bookings")
                .child(movieTitle)
                .child(date + "_" + time)
                .child("seats");

        Map<String, Object> updates = new HashMap<>();
        for (String seat : selectedSeats) {
            updates.put(seat, "booked");
        }

        seatsRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Đặt vé thành công!", Toast.LENGTH_SHORT).show();
                // Navigate to main menu
                Intent intent = new Intent(BookingConfirmationActivity.this, activities_2_menu_manage_fragments.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi cập nhật ghế!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
