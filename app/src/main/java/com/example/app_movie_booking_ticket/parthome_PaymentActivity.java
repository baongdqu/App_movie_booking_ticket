package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.vnpay.authentication.VNP_AuthenticationActivity;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Activity Thanh toán (Payment)
 * - VNPay (Sandbox)
 * - Balance nội bộ (Realtime Database users/{uid}/balance)
 *
 * ✅ Sau khi thanh toán thành công -> quay về Movie Detail (không quay về chọn ghế)
 */
public class parthome_PaymentActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    private String currentTicketId;

    private String posterUrl;
    private String movieTitle;
    private String movieID;

    private String cinemaName;
    private String cinemaId;

    private String date;
    private String time;
    private ArrayList<String> seats;
    private int totalPrice;

    private RadioButton rbVnpay;
    private RadioButton rbBalance;

    private long userBalance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        setContentView(R.layout.parthome_payment);

        // ===== NHẬN DATA TỪ INTENT =====
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            Log.d("VNPAY_RETURN", "Data nhận được: " + intent.getData());
        }

        posterUrl = intent.getStringExtra("posterUrl");
        movieTitle = intent.getStringExtra("movieTitle");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        seats = intent.getStringArrayListExtra("seats");
        totalPrice = intent.getIntExtra("totalPrice", 0);
        movieID = intent.getStringExtra("movieID");

        cinemaName = intent.getStringExtra("cinemaName");
        cinemaId = intent.getStringExtra("cinemaId");

        // ===== MAP VIEW =====
        ImageView imagePoster = findViewById(R.id.imagePoster);
        TextView txtTitle = findViewById(R.id.txtTitle);
        TextView txtCinemaName = findViewById(R.id.txtCinemaName);
        TextView txtTime = findViewById(R.id.txtTime);
        TextView txtSeat = findViewById(R.id.txtSeat);
        TextView txtTotal = findViewById(R.id.txtTotal);
        ImageView btnBack = findViewById(R.id.btnBackPay);
        TextView txtUser = findViewById(R.id.txtUser);
        TextView txtPhone = findViewById(R.id.txtPhone);
        TextView txtEmail = findViewById(R.id.txtEmail);

        rbVnpay = findViewById(R.id.rbVnpay);
        rbBalance = findViewById(R.id.rbBalance);

        MaterialButton btnContinue = findViewById(R.id.btnContinue);

        // ===== HIỂN THỊ DATA =====
        txtTitle.setText(movieTitle);

        Glide.with(this)
                .load(posterUrl)
                .placeholder(R.drawable.placeholder_movie)
                .error(R.drawable.placeholder_movie)
                .into(imagePoster);

        if (cinemaName != null && !cinemaName.isEmpty()) {
            txtCinemaName.setText("🎬 " + cinemaName);
        } else {
            txtCinemaName.setVisibility(View.GONE);
        }

        txtTime.setText(date + "\n" + time);

        if (seats != null && !seats.isEmpty()) {
            txtSeat.setText(android.text.TextUtils.join(", ", seats));
        }

        DecimalFormat formatter = new DecimalFormat("#,###");
        txtTotal.setText(formatter.format(totalPrice) + "đ");

        // Load user info + balance
        loadUserInfo(txtUser, txtEmail, txtPhone);
        loadUserBalance();

        setupPaymentMethodSelection();

        btnContinue.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            processPayment();
        });

        btnBack.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            finish();
        });
    }

    // =================== UI: chọn phương thức ===================
    private void setupPaymentMethodSelection() {
        rbVnpay.setChecked(true);

        View vnpayCard = (View) rbVnpay.getParent().getParent();
        View balanceCard = (View) rbBalance.getParent().getParent();

        if (vnpayCard instanceof MaterialCardView) {
            vnpayCard.setOnClickListener(v -> {
                rbVnpay.setChecked(true);
                rbBalance.setChecked(false);
            });
        }

        if (balanceCard instanceof MaterialCardView) {
            balanceCard.setOnClickListener(v -> {
                rbBalance.setChecked(true);
                rbVnpay.setChecked(false);
            });
        }

        rbVnpay.setOnClickListener(v -> rbBalance.setChecked(false));
        rbBalance.setOnClickListener(v -> rbVnpay.setChecked(false));
    }

    // =================== Load balance ===================
    private void loadUserBalance() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        DatabaseReference balanceRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("balance");

        balanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userBalance = parseBalance(snapshot.getValue());

                TextView txtBalance = findViewById(R.id.txtBalance);
                if (txtBalance != null) {
                    DecimalFormat formatter = new DecimalFormat("#,###");
                    txtBalance.setText(getString(R.string.balance_desc, formatter.format(userBalance)));
                }

                Log.d("BALANCE_DEBUG", "loadUserBalance uid=" + uid + " userBalance=" + userBalance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PAYMENT", "Error loading balance", error.toException());
            }
        });
    }

    // =================== Process payment ===================
    private void processPayment() {
        if (rbVnpay.isChecked()) {
            try {
                String paymentUrl = createVnpayUrl(totalPrice);
                if (paymentUrl != null) {
                    openSdk(paymentUrl);
                } else {
                    Toast.makeText(this,
                            getString(R.string.toast_payment_failed, "URL error"),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this,
                        getString(R.string.toast_payment_failed, e.getMessage()),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (rbBalance.isChecked()) {
            payByBalance(); // ✅ đã fix bug lần 1
        } else {
            Toast.makeText(this, R.string.toast_select_payment_method, Toast.LENGTH_SHORT).show();
        }
    }

    // =================== VNPay ===================
    public void openSdk(String paymentUrl) {
        createPendingTicket("VNPAY");

        Intent intent = new Intent(this, VNP_AuthenticationActivity.class);
        intent.putExtra("url", paymentUrl);
        intent.putExtra("tmn_code", "C1C16DDU");
        intent.putExtra("scheme", "resultactivity");
        intent.putExtra("is_sandbox", true);

        VNP_AuthenticationActivity.setSdkCompletedCallback(action -> {
            Log.d("VNPAY_SDK", "Action: " + action);

            if (action != null) {
                // ✅ updateTicketToPaid sẽ bookSeats + quay về movie detail
                updateTicketToPaid(currentTicketId);
                Toast.makeText(this, R.string.toast_payment_success, Toast.LENGTH_SHORT).show();
            } else {
                updateTicketStatus(currentTicketId, "CANCELLED");
                Toast.makeText(this,
                        "Thanh toán không thành công hoặc đã bị hủy",
                        Toast.LENGTH_SHORT).show();
            }
        });

        startActivity(intent);
    }

    // =================== Load user info ===================
    private void loadUserInfo(TextView txtUser, TextView txtEmail, TextView txtPhone) {
        currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        String uid = currentUser.getUid();

        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                String fullName = snapshot.child("fullName").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);

                txtUser.setText(fullName != null ? fullName : "Người dùng");
                txtEmail.setText(email != null ? email : "");
                txtPhone.setText(phone != null ? phone : "Chưa cập nhật");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // =================== Balance (FIX “lần 1 fail, lần 2 ok”) ===================
    private void payByBalance() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        DatabaseReference balanceRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("balance");

        // ✅ Prefetch số dư từ server trước khi transaction chạy
        balanceRef.get().addOnCompleteListener(task -> {

            if (!task.isSuccessful() || task.getResult() == null) {
                Toast.makeText(this, "Không đọc được số dư, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                return;
            }

            final long serverBalance = parseBalance(task.getResult().getValue());

            Log.d("BALANCE_DEBUG", "prefetch uid=" + uid
                    + " serverBalance=" + serverBalance
                    + " totalPrice=" + totalPrice);

            balanceRef.runTransaction(new Transaction.Handler() {

                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                    Object localVal = currentData.getValue();

                    // ✅ Nếu local cache null -> dùng serverBalance vừa fetch
                    long balance = (localVal == null) ? serverBalance : parseBalance(localVal);

                    Log.d("BALANCE_DEBUG", "doTransaction localVal=" + localVal
                            + " parsedBalance=" + balance
                            + " totalPrice=" + totalPrice);

                    if (balance < totalPrice) {
                        return Transaction.abort();
                    }

                    currentData.setValue(balance - totalPrice);
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {

                    if (error != null) {
                        Toast.makeText(parthome_PaymentActivity.this,
                                "Lỗi thanh toán: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!committed) {
                        Toast.makeText(parthome_PaymentActivity.this,
                                R.string.toast_insufficient_balance,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // ✅ Thành công
                    bookSeats(movieTitle, date, time, seats);
                    saveTicketSuccessByBalance();

                    Toast.makeText(parthome_PaymentActivity.this,
                            R.string.toast_payment_success,
                            Toast.LENGTH_SHORT).show();

                    // ✅ QUAY VỀ MOVIE DETAIL (KHÔNG VỀ CHỌN GHẾ)
                    goToMovieDetail();
                }
            });
        });
    }

    // =================== Điều hướng về Movie Detail (pop SeatSelection + Payment) ===================
    private void goToMovieDetail() {
        Intent i = new Intent(parthome_PaymentActivity.this, parthome_movie_detail.class);

        // gửi lại thông tin cơ bản để movie detail có thể reload nếu cần
        i.putExtra("movieID", movieID);
        i.putExtra("movieTitle", movieTitle);

        // ✅ pop các màn trên movie detail (SeatSelection + Payment) nếu movie detail đang nằm dưới stack
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(i);
        finish();
    }

    // =================== Save ticket ===================
    private void saveTicketSuccessByBalance() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tickets");
        String ticketId = ref.push().getKey();
        if (ticketId == null) return;

        Map<String, Object> payment = new HashMap<>();
        payment.put("method", "BALANCE");
        payment.put("status", "PAID");
        payment.put("paidAt", System.currentTimeMillis());

        Map<String, Object> ticket = new HashMap<>();
        ticket.put("movieId", movieID);
        ticket.put("userId", user.getUid());
        ticket.put("movieTitle", movieTitle);
        ticket.put("posterUrl", posterUrl);

        ticket.put("cinemaId", cinemaId);
        ticket.put("cinemaName", cinemaName);

        ticket.put("date", date);
        ticket.put("time", time);
        ticket.put("seats", seats);
        ticket.put("totalPrice", totalPrice);
        ticket.put("payment", payment);
        ticket.put("status", "PAID");
        ticket.put("createdAt", System.currentTimeMillis());

        ref.child(ticketId).setValue(ticket);
    }

    private void createPendingTicket(String method) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tickets");
        currentTicketId = ref.push().getKey();

        Map<String, Object> ticket = new HashMap<>();
        ticket.put("ticketId", currentTicketId);
        ticket.put("movieId", movieID);
        ticket.put("userId", auth.getCurrentUser().getUid());
        ticket.put("movieTitle", movieTitle);
        ticket.put("posterUrl", posterUrl);

        ticket.put("cinemaId", cinemaId);
        ticket.put("cinemaName", cinemaName);

        ticket.put("date", date);
        ticket.put("time", time);
        ticket.put("seats", seats);
        ticket.put("totalPrice", totalPrice);
        ticket.put("status", "PENDING");
        ticket.put("createdAt", System.currentTimeMillis());

        Map<String, Object> payment = new HashMap<>();
        payment.put("method", method);
        payment.put("status", "PENDING");
        ticket.put("payment", payment);

        ref.child(currentTicketId).setValue(ticket);
    }

    private void updateTicketStatus(String ticketId, String newStatus) {
        if (ticketId == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("tickets")
                .child(ticketId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", newStatus);
        updates.put("paidAt", System.currentTimeMillis());

        ref.updateChildren(updates);
    }

    private void updateTicketToPaid(String ticketId) {
        if (ticketId == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("tickets")
                .child(ticketId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "PAID");
        updates.put("payment/status", "PAID");
        updates.put("payment/paidAt", System.currentTimeMillis());

        ref.updateChildren(updates).addOnSuccessListener(unused -> {
            bookSeats(movieTitle, date, time, seats);

            // ✅ VNPay thành công -> quay về movie detail luôn
            goToMovieDetail();
        });
    }

    private void bookSeats(
            String movieTitle,
            String date,
            String time,
            List<String> selectedSeats) {

        String showtimeKey = date + "_" + time;

        DatabaseReference seatsRef;

        // ✅ Nếu có cinemaId -> ghi vào cinemas/{cinemaId}/seats (đúng như Firebase của bạn)
        if (cinemaId != null && !cinemaId.trim().isEmpty()) {
            seatsRef = FirebaseDatabase.getInstance()
                    .getReference("Bookings")
                    .child(movieTitle)
                    .child(showtimeKey)
                    .child("cinemas")
                    .child(cinemaId)
                    .child("seats");
        } else {
            // ✅ Trường hợp không chọn cinema -> fallback như cũ
            seatsRef = FirebaseDatabase.getInstance()
                    .getReference("Bookings")
                    .child(movieTitle)
                    .child(showtimeKey)
                    .child("seats");
        }

        Map<String, Object> updates = new HashMap<>();
        for (String seat : selectedSeats) {
            updates.put(seat, "booked");
        }

        seatsRef.updateChildren(updates)
                .addOnSuccessListener(unused -> Log.d("BOOK_SEAT", "Book ghế thành công"))
                .addOnFailureListener(e -> Log.e("BOOK_SEAT", "Lỗi book ghế", e));
    }


    // =================== Helpers ===================
    private long parseBalance(Object val) {
        if (val == null) return 0L;

        if (val instanceof Number) {
            return ((Number) val).longValue();
        }

        if (val instanceof String) {
            try {
                String s = ((String) val).replaceAll("[^0-9]", "");
                if (s.isEmpty()) return 0L;
                return Long.parseLong(s);
            } catch (Exception ignored) {
                return 0L;
            }
        }

        return 0L;
    }

    // =================== VNPay URL ===================
    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes());

            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) hash.append(String.format("%02x", b));
            return hash.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String createVnpayUrl(int totalPrice) throws Exception {
        try {
            String vnp_TmnCode = "C1C16DDU";
            String vnp_HashSecret = "8XWZ093QGUAF75SADH9B1E7KH7NM2SOR";
            String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

            Map<String, String> params = new HashMap<>();
            params.put("vnp_Version", "2.1.0");
            params.put("vnp_Command", "pay");
            params.put("vnp_TmnCode", vnp_TmnCode);
            params.put("vnp_Amount", String.valueOf(totalPrice * 100));
            params.put("vnp_CurrCode", "VND");
            params.put("vnp_TxnRef", String.valueOf(System.currentTimeMillis()));
            params.put("vnp_OrderInfo", "Thanh toan ve phim");
            params.put("vnp_OrderType", "other");
            params.put("vnp_Locale", "vn");
            params.put("vnp_ReturnUrl", "http://success.sdk.merchantbackapp");
            params.put("vnp_IpAddr", "127.0.0.1");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            params.put("vnp_CreateDate", sdf.format(new Date()));

            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (String field : fieldNames) {
                String value = params.get(field);
                if (value != null && !value.isEmpty()) {
                    hashData.append(field).append('=')
                            .append(URLEncoder.encode(value, "UTF-8")).append('&');

                    query.append(URLEncoder.encode(field, "UTF-8"))
                            .append('=')
                            .append(URLEncoder.encode(value, "UTF-8")).append('&');
                }
            }

            hashData.deleteCharAt(hashData.length() - 1);
            query.deleteCharAt(query.length() - 1);

            String secureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
            return vnp_Url + "?" + query + "&vnp_SecureHash=" + secureHash;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
