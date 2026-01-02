package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TicketDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TICKET_ID = "ticketId";

    private ImageView imgPoster;
    private TextView tvMovieTitle, tvCinema, tvDateTime, tvSeats, tvTotalPrice;
    private MaterialButton btnRefund;

    private final DecimalFormat moneyFmt = new DecimalFormat("#,###");
    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    private String ticketId;
    private String ticketUserId;
    private boolean refunded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        imgPoster = findViewById(R.id.imgPoster);
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvCinema = findViewById(R.id.tvCinema);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvSeats = findViewById(R.id.tvSeats);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnRefund = findViewById(R.id.btnRefund);

        ticketId = getIntent().getStringExtra(EXTRA_TICKET_ID);
        if (ticketId == null || ticketId.trim().isEmpty()) {
            finish();
            return;
        }

        loadTicket(ticketId);

        btnRefund.setOnClickListener(v -> refundTicket());
    }

    private void loadTicket(String ticketId) {
        db.child("tickets").child(ticketId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot t) {
                        if (!t.exists()) {
                            Toast.makeText(TicketDetailActivity.this, "Không tìm thấy vé", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                        String movieTitle = t.child("movieTitle").getValue(String.class);
                        String posterUrl = t.child("posterUrl").getValue(String.class);
                        String cinemaName = t.child("cinemaName").getValue(String.class);
                        String date = t.child("date").getValue(String.class);
                        String time = t.child("time").getValue(String.class);

                        ticketUserId = t.child("userId").getValue(String.class);

                        Long tpObj = t.child("totalPrice").getValue(Long.class);
                        long totalPrice = (tpObj != null) ? tpObj : 0L;

                        String status = t.child("status").getValue(String.class);
                        refunded = "REFUNDED".equals(status);

                        ArrayList<String> seats = new ArrayList<>();
                        if (t.child("seats").exists()) {
                            for (DataSnapshot s : t.child("seats").getChildren()) {
                                String seat = s.getValue(String.class);
                                if (seat != null) seats.add(seat);
                            }
                        }

                        tvMovieTitle.setText(movieTitle != null ? movieTitle : "");
                        tvCinema.setText(!TextUtils.isEmpty(cinemaName) ? ("🎬 " + cinemaName) : "🎬 (Chưa có rạp)");
                        tvDateTime.setText("🕒 " + (date != null ? date : "") + " • " + (time != null ? time : ""));
                        tvSeats.setText(seats.isEmpty()
                                ? "🎟 Ghế: (Chưa có)"
                                : "🎟 Ghế: " + TextUtils.join(", ", seats));
                        tvTotalPrice.setText(moneyFmt.format(totalPrice) + "đ");

                        if (!TextUtils.isEmpty(posterUrl)) {
                            Glide.with(TicketDetailActivity.this)
                                    .load(posterUrl)
                                    .placeholder(R.drawable.placeholder_movie)
                                    .error(R.drawable.placeholder_movie)
                                    .into(imgPoster);
                        } else {
                            imgPoster.setImageResource(R.drawable.placeholder_movie);
                        }

                        updateRefundButtonUI();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("TICKET_DETAIL", "loadTicket error", error.toException());
                        Toast.makeText(TicketDetailActivity.this, "Không tải được vé", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void updateRefundButtonUI() {
        if (refunded) {
            btnRefund.setEnabled(false);
            btnRefund.setText("Đã hoàn vé");
        } else {
            btnRefund.setEnabled(true);
            btnRefund.setText("Hoàn vé");
        }
    }

    private void refundTicket() {
        if (refunded) {
            Toast.makeText(this, "Vé đã được hoàn", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        if (current == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ticketUserId != null && !ticketUserId.equals(current.getUid())) {
            Toast.makeText(this, "Bạn không có quyền hoàn vé này", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ticketRef = db.child("tickets").child(ticketId);

        ticketRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotTicket) {
                if (!snapshotTicket.exists()) return;

                String status = snapshotTicket.child("status").getValue(String.class);
                if (status == null) status = "PAID";

                if (!"PAID".equals(status)) {
                    Toast.makeText(TicketDetailActivity.this,
                            "Vé đã được hoàn hoặc không hợp lệ",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Long tpObj = snapshotTicket.child("totalPrice").getValue(Long.class);
                final long tp = (tpObj != null) ? tpObj : 0L; // ✅ final

                String uid = current.getUid();
                DatabaseReference balanceRef = db.child("users").child(uid).child("balance");

                balanceRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        Long balObj = currentData.getValue(Long.class);
                        long bal = (balObj != null) ? balObj : 0L;

                        currentData.setValue(bal + tp);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                        if (!committed) {
                            Toast.makeText(TicketDetailActivity.this,
                                    "Hoàn tiền thất bại",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("status", "REFUNDED");
                        updates.put("refundedAt", System.currentTimeMillis());

                        ticketRef.updateChildren(updates)
                                .addOnSuccessListener(unused -> {
                                    refunded = true;
                                    Toast.makeText(TicketDetailActivity.this,
                                            "Hoàn vé thành công!",
                                            Toast.LENGTH_SHORT).show();

                                    // ✅ báo về Fragment reload rồi quay lại
                                    setResult(RESULT_OK);
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(TicketDetailActivity.this,
                                        "Cập nhật vé thất bại",
                                        Toast.LENGTH_SHORT).show());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
