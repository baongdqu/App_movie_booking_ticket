package com.example.app_movie_booking_ticket;

import android.content.Intent;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TicketDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TICKET_ID = "ticketId";

    private ImageView imgPoster;
    private TextView tvMovieTitle, tvCinema, tvDateTime, tvSeats, tvTotalPrice;
    // --- THÊM 2 BIẾN NÀY ---
    private TextView tvPaymentMethod, tvTransactionTime;
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

        // Toolbar navigation
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            Intent intent = new Intent(TicketDetailActivity.this, activities_2_a_menu_manage_fragments.class);
            intent.putExtra("OPEN_FRAGMENT", "TICKET_FRAGMENT");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Ánh xạ View
        imgPoster = findViewById(R.id.imgPoster);
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvCinema = findViewById(R.id.tvCinema);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvSeats = findViewById(R.id.tvSeats);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnRefund = findViewById(R.id.btnRefund);

        // --- ÁNH XẠ 2 VIEW MỚI (Đảm bảo ID này trùng với XML) ---
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvTransactionTime = findViewById(R.id.tvTransactionTime);

        ticketId = getIntent().getStringExtra(EXTRA_TICKET_ID);
        if (ticketId == null || ticketId.trim().isEmpty()) {
            finish();
            return;
        }
        loadTicket(ticketId);

        btnRefund.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            refundTicket();
        });
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

                        // Lấy dữ liệu cơ bản
                        String movieTitle = t.child("movieTitle").getValue(String.class);
                        String posterUrl = t.child("posterUrl").getValue(String.class);
                        String cinemaName = t.child("cinemaName").getValue(String.class);
                        String date = t.child("date").getValue(String.class);
                        String time = t.child("time").getValue(String.class);

                        // Lấy dữ liệu thanh toán chi tiết
                        String method = t.child("payment/method").getValue(String.class);
                        Long paidAt = t.child("payment/paidAt").getValue(Long.class);

                        ticketUserId = t.child("userId").getValue(String.class);

                        Long tpObj = t.child("totalPrice").getValue(Long.class);
                        long totalPrice = (tpObj != null) ? tpObj : 0L;

                        String status = t.child("status").getValue(String.class);
                        refunded = "REFUNDED".equals(status);

                        // Xử lý danh sách ghế
                        ArrayList<String> seats = new ArrayList<>();
                        if (t.child("seats").exists()) {
                            for (DataSnapshot s : t.child("seats").getChildren()) {
                                String seat = s.getValue(String.class);
                                if (seat != null)
                                    seats.add(seat);
                            }
                        }

                        // Hiển thị lên UI
                        tvMovieTitle.setText(movieTitle != null ? movieTitle : "");
                        tvCinema.setText(!TextUtils.isEmpty(cinemaName) ? ("🎬 " + cinemaName) : "🎬 (Chưa có rạp)");
                        tvDateTime.setText("🕒 " + (date != null ? date : "") + " • " + (time != null ? time : ""));
                        tvSeats.setText(
                                seats.isEmpty() ? "🎟 Ghế: (Chưa có)" : "🎟 Ghế: " + TextUtils.join(", ", seats));
                        tvTotalPrice.setText(moneyFmt.format(totalPrice) + "đ");

                        // Hiển thị phương thức thanh toán
                        String paymentDisplay = ("BALANCE".equals(method) ? "Số dư" : "Cổng VNPay");
                        if (tvPaymentMethod != null)
                            tvPaymentMethod.setText(paymentDisplay);

                        // Hiển thị thời gian giao dịch
                        if (paidAt != null && tvTransactionTime != null) {
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy", Locale.getDefault());
                            tvTransactionTime.setText(sdf.format(new Date(paidAt)));
                        }

                        // Load ảnh poster
                        Glide.with(TicketDetailActivity.this)
                                .load(posterUrl)
                                .placeholder(R.drawable.placeholder_movie)
                                .error(R.drawable.placeholder_movie)
                                .into(imgPoster);

                        updateRefundButtonUI();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("TICKET_DETAIL", "loadTicket error", error.toException());
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
                if (!snapshotTicket.exists())
                    return;

                String status = snapshotTicket.child("status").getValue(String.class);
                if (status == null)
                    status = "PAID";

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
                                    extra_sound_manager.playSuccess(TicketDetailActivity.this);
                                    Toast.makeText(TicketDetailActivity.this,
                                            "Hoàn vé thành công!",
                                            Toast.LENGTH_SHORT).show();
                                    sendNotification(ticketUserId, "Hoàn tiền thành công",
                                            "Vé của bạn đã được hoàn tiền.", "REFUND");

                                    // ✅ báo về Fragment reload rồi quay lại
                                    setResult(RESULT_OK);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    extra_sound_manager.playError(TicketDetailActivity.this);
                                    Toast.makeText(TicketDetailActivity.this,
                                            "Cập nhật vé thất bại",
                                            Toast.LENGTH_SHORT).show();
                                });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void sendNotification(String userId, String title, String message, String type) {
        DatabaseReference notifRef = FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(userId);

        String key = notifRef.push().getKey();
        if (key == null)
            return;

        Map<String, Object> notif = new HashMap<>();
        notif.put("title", title);
        notif.put("message", message);
        notif.put("type", type); // Truyền "REFUND"
        notif.put("timestamp", System.currentTimeMillis());
        notif.put("read", false);

        notifRef.child(key).setValue(notif);
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
