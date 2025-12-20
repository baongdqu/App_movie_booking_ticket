package com.example.app_movie_booking_ticket;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vnpay.authentication.VNP_AuthenticationActivity;
import com.vnpay.authentication.VNP_SdkCompletedCallback;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;
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
import com.google.firebase.database.Transaction;
import com.google.firebase.database.MutableData;


public class PaymentActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    private String posterUrl;
    private String movieTitle;
    private String date;
    private String time;
    private ArrayList<String> seats;
    private int totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        setContentView(R.layout.activity_payment);
        // ===== NHẬN DATA TỪ INTENT =====
        Intent intent = getIntent();

        posterUrl = intent.getStringExtra("posterUrl");
        movieTitle = intent.getStringExtra("movieTitle");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        seats = intent.getStringArrayListExtra("seats");
        totalPrice = intent.getIntExtra("totalPrice", 0);

        // ===== MAP VIEW =====
        ImageView imagePoster = findViewById(R.id.imagePoster);
        TextView txtTitle = findViewById(R.id.txtTitle);
        TextView txtTime = findViewById(R.id.txtTime);
        TextView txtSeat = findViewById(R.id.txtSeat);
        TextView txtTotal = findViewById(R.id.txtTotal);
        ImageView btnBack = findViewById(R.id.btnBackPay);
        TextView txtUser = findViewById(R.id.txtUser);
        TextView txtPhone = findViewById(R.id.txtPhone);
        TextView txtEmail = findViewById(R.id.txtEmail);

        Button btnContinue = findViewById(R.id.btnContinue);
        Button btnPayByBalance = findViewById(R.id.btnPayByBalance);

       // ===== CLICK THANH TOÁN BẰNG SỐ DƯ =====
        btnPayByBalance.setOnClickListener(v -> {
            Log.d("PAY_BALANCE", "Clicked pay by balance");
            payByBalance();
        });


        Log.d("PAYMENT", "posterUrl = " + posterUrl);

        // ===== HIỂN THỊ DATA =====
        txtTitle.setText(movieTitle);
        Glide.with(this)
                .load(posterUrl)
                .into(imagePoster);
        txtTime.setText(time + "\n" + date);

        if (seats != null && !seats.isEmpty()) {
            txtSeat.setText(android.text.TextUtils.join(", ", seats));
        }

        txtTotal.setText(totalPrice + "đ");

        // Demo thông tin user (sau này lấy từ Firebase)
        loadUserInfo(txtUser, txtEmail, txtPhone);

        // ===== MỞ VNPAY =====
        String paymentUrl = null;
        try {
            paymentUrl = createVnpayUrl(totalPrice);
            String finalPaymentUrl = paymentUrl;
            btnContinue.setOnClickListener(v -> openSdk(finalPaymentUrl));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

//        findViewById(R.id.btnContinue).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openSdk();
//            }
//        });

        btnBack.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            finish();
        });

    }


    public void openSdk(String paymentUrl) {

        Intent intent = new Intent(this, VNP_AuthenticationActivity.class);
        intent.putExtra("url", paymentUrl); //bắt buộc, VNPAY cung cấp
        intent.putExtra("tmn_code", "C1C16DDU"); //bắt buộc, VNPAY cung cấp
        intent.putExtra("scheme", "resultactivity"); //bắt buộc, scheme để mở lại app khi có kết quả thanh toán từ mobile banking
        intent.putExtra("is_sandbox", true); //bắt buộc, true <=> môi trường test, true <=> môi trường live
        VNP_AuthenticationActivity.setSdkCompletedCallback(new VNP_SdkCompletedCallback() {
            @Override
            public void sdkAction(String action) {
                Log.wtf("SplashActivity", "action: " + action);

                switch (action) {

                    case "SuccessBackAction":
                        // 1. Lưu Firebase
                        bookSeats(movieTitle, date, time, seats);
                        saveTicketSuccess();
                        break;

                    case "FaildBackAction":
                    case "WebBackAction":
                        // Không làm gì
                        break;

                    case "AppBackAction":
                        // User thoát
                        break;
                }
            }
        });
        startActivity(intent);
    }

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
                txtPhone.setText(phone != null ? phone : "");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes());
            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String createVnpayUrl(int totalPrice) throws Exception {
        try {
            String vnp_TmnCode = "C1C16DDU";
            String vnp_HashSecret = "8XWZ093QGUAF75SADH9B1E7KH7NM2SOR"; // TEST ONLY
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

            // SORT
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
    private void payByBalance() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        DatabaseReference balanceRef =
                FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(uid)
                        .child("balance");

        balanceRef.runTransaction(new Transaction.Handler() {

            @NonNull
            @Override
            public Transaction.Result doTransaction(
                    @NonNull MutableData currentData) {

                Long balance = currentData.getValue(Long.class);

                // 🔥 balance null = 0
                if (balance == null) balance = 0L;

                // ❌ KHÔNG ĐỦ TIỀN
                if (balance < totalPrice) {
                    return Transaction.abort();
                }

                // ✅ TRỪ TIỀN
                currentData.setValue(balance - totalPrice);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(
                    DatabaseError error,
                    boolean committed,
                    DataSnapshot snapshot) {

                // ❌ TRANSACTION FAIL
                if (!committed) {
                    Toast.makeText(
                            PaymentActivity.this,
                            "Số dư không đủ để thanh toán",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                // ✅ THANH TOÁN THÀNH CÔNG
                bookSeats(movieTitle, date, time, seats);
                saveTicketSuccessByBalance();

                Toast.makeText(
                        PaymentActivity.this,
                        "Thanh toán bằng số dư thành công",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            }
        });
    }
    private void saveTicketSuccessByBalance() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference ref =
                FirebaseDatabase.getInstance().getReference("tickets");

        String ticketId = ref.push().getKey();
        if (ticketId == null) return;

        Map<String, Object> payment = new HashMap<>();
        payment.put("method", "BALANCE");
        payment.put("status", "PAID");
        payment.put("paidAt", System.currentTimeMillis());

        Map<String, Object> ticket = new HashMap<>();
        ticket.put("userId", user.getUid());
        ticket.put("movieTitle", movieTitle);
        ticket.put("posterUrl", posterUrl);
        ticket.put("date", date);
        ticket.put("time", time);
        ticket.put("seats", seats);
        ticket.put("totalPrice", totalPrice);
        ticket.put("payment", payment);
        ticket.put("status", "PAID");
        ticket.put("createdAt", System.currentTimeMillis());

        ref.child(ticketId).setValue(ticket);
    }



    private void saveTicketSuccess() {
        if (currentUser == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("tickets");

        String ticketId = ref.push().getKey();

        Map<String, Object> payment = new HashMap<>();
        payment.put("method", "VNPAY");
        payment.put("status", "PAID");
        payment.put("paidAt", System.currentTimeMillis());

        Map<String, Object> ticket = new HashMap<>();
        ticket.put("userId", currentUser.getUid());
        ticket.put("movieTitle", movieTitle);
        ticket.put("posterUrl", posterUrl);
        ticket.put("date", date);
        ticket.put("time", time);
        ticket.put("seats", seats);
        ticket.put("totalPrice", totalPrice);
        ticket.put("payment", payment);
        ticket.put("createdAt", System.currentTimeMillis());
        ticket.put("status", "PAID");

        ref.child(ticketId).setValue(ticket);
    }

    private void bookSeats(
            String movieTitle,
            String date,
            String time,
            List<String> selectedSeats
    ) {
        DatabaseReference seatsRef = FirebaseDatabase.getInstance()
                .getReference("Bookings")
                .child(movieTitle)
                .child(date + "_" + time)
                .child("seats");

        Map<String, Object> updates = new HashMap<>();

        for (String seat : selectedSeats) {
            updates.put(seat, "booked");
        }

        seatsRef.updateChildren(updates)
                .addOnSuccessListener(unused ->
                        Log.d("BOOK_SEAT", "Book ghế thành công"))
                .addOnFailureListener(e ->
                        Log.e("BOOK_SEAT", "Lỗi book ghế", e));
    }

}