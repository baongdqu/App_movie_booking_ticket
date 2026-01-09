package com.example.app_movie_booking_ticket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class parthome_PaymentActivity extends extra_manager_language {

    private static final String TAG = "PAYMENT";
    private static final String PREFS = "payment_prefs";
    private static final String KEY_PENDING_TICKET_ID = "pending_ticket_id";

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

    // Thông tin người dùng để gửi email hóa đơn
    private String userEmail = "";
    private String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra_themeutils.applySavedTheme(this);

        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        setContentView(R.layout.parthome_payment);

        Intent intent = getIntent();

        // ✅ XỬ LÝ RETURN TỪ VNPAY (nếu có)
        handleVnpayReturn(intent);

        // ===== NHẬN DATA TỪ INTENT (extras) =====
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

        // ===== HIỂN THỊ =====
        txtTitle.setText(movieTitle);

        Glide.with(this)
                .load(posterUrl)
                .placeholder(R.drawable.placeholder_movie)
                .error(R.drawable.placeholder_movie)
                .into(imagePoster);

        if (cinemaName != null && !cinemaName.isEmpty()) {
            txtCinemaName.setText(getString(R.string.cinema_name_with_icon, cinemaName));
        } else {
            txtCinemaName.setVisibility(View.GONE);
        }

        txtTime.setText(date + "\n" + time);

        if (seats != null && !seats.isEmpty()) {
            txtSeat.setText(android.text.TextUtils.join(", ", seats));
        }

        DecimalFormat formatter = new DecimalFormat("#,###");
        txtTotal.setText(getString(R.string.price_format_vnd, formatter.format(totalPrice)));

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

    // ✅ Khi PaymentActivity đang mở mà nhận deep link mới
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleVnpayReturn(intent);
    }

    // =================== QUAN TRỌNG: xử lý kết quả VNPAY bằng intent.getData()
    // ===================
    private void handleVnpayReturn(Intent intent) {
        if (intent == null)
            return;

        Uri data = intent.getData();
        if (data == null)
            return;

        String responseCode = data.getQueryParameter("vnp_ResponseCode");

        // Lấy ID vé chờ từ SharedPreferences
        String pendingId = getPrefs().getString(KEY_PENDING_TICKET_ID, null);
        if (pendingId == null) return;

        if (!"00".equals(responseCode)) {
            // HỦY THANH TOÁN: Cập nhật trạng thái và load lại dữ liệu để tránh màn hình trống
            updateTicketStatus(pendingId, "CANCELLED");
            Toast.makeText(this, "Bạn đã hủy thanh toán", Toast.LENGTH_SHORT).show();

            // 🔥 QUAN TRỌNG: Gọi hàm này để nạp lại dữ liệu phim/ghế lên giao diện
            reloadPaymentData(pendingId);

            clearPendingTicketId();
        } else {
            // ... xử lý thành công như cũ ...
        }
    }

    // Hàm bổ trợ để nạp lại dữ liệu từ Firebase nếu biến bị null
    private void reloadPaymentData(String ticketId) {
        FirebaseDatabase.getInstance().getReference("tickets").child(ticketId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            movieTitle = snapshot.child("movieTitle").getValue(String.class);
                            date = snapshot.child("date").getValue(String.class);
                            time = snapshot.child("time").getValue(String.class);
                            posterUrl = snapshot.child("posterUrl").getValue(String.class);
                            seats = (ArrayList<String>) snapshot.child("seats").getValue();
                            totalPrice = snapshot.child("totalPrice").getValue(Integer.class);

                            // Cập nhật lại UI sau khi đã có dữ liệu
                            refreshUI();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Lưu các biến vào Bundle trước khi App bị kill
        outState.putString("movieTitle", movieTitle);
        outState.putString("date", date);
        outState.putString("time", time);
        outState.putString("posterUrl", posterUrl);
        outState.putStringArrayList("seats", seats);
        outState.putInt("totalPrice", totalPrice);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Khôi phục lại khi quay về
        movieTitle = savedInstanceState.getString("movieTitle");
        date = savedInstanceState.getString("date");
        time = savedInstanceState.getString("time");
        posterUrl = savedInstanceState.getString("posterUrl");
        seats = savedInstanceState.getStringArrayList("seats");
        totalPrice = savedInstanceState.getInt("totalPrice");

        // Gọi hàm hiển thị lại dữ liệu lên View
        refreshUI();
    }
    private void refreshUI() {
        TextView txtTitle = findViewById(R.id.txtTitle);
        TextView txtTime = findViewById(R.id.txtTime);
        TextView txtSeat = findViewById(R.id.txtSeat);
        TextView txtTotal = findViewById(R.id.txtTotal);
        ImageView imagePoster = findViewById(R.id.imagePoster);

        if (movieTitle != null) txtTitle.setText(movieTitle);
        if (date != null && time != null) txtTime.setText(date + "\n" + time);
        if (seats != null) txtSeat.setText(android.text.TextUtils.join(", ", seats));

        DecimalFormat formatter = new DecimalFormat("#,###");
        txtTotal.setText(formatter.format(totalPrice) + "đ");

        Glide.with(this).load(posterUrl).into(imagePoster);
    }
    // =================== UI chọn phương thức ===================
    private void setupPaymentMethodSelection() {
        rbVnpay.setChecked(true);

        View vnpayCard = (View) rbVnpay.getParent().getParent();
        View balanceCard = (View) rbBalance.getParent().getParent();

        if (vnpayCard instanceof MaterialCardView) {
            vnpayCard.setOnClickListener(v -> {
                extra_sound_manager.playUiClick(this);
                rbVnpay.setChecked(true);
                rbBalance.setChecked(false);
            });
        }

        if (balanceCard instanceof MaterialCardView) {
            balanceCard.setOnClickListener(v -> {
                extra_sound_manager.playUiClick(this);
                rbBalance.setChecked(true);
                rbVnpay.setChecked(false);
            });
        }

        rbVnpay.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            rbBalance.setChecked(false);
        });
        rbBalance.setOnClickListener(v -> {
            extra_sound_manager.playUiClick(this);
            rbVnpay.setChecked(false);
        });
    }

    // =================== Load balance ===================
    private void loadUserBalance() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            return;

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
                Log.e(TAG, "Error loading balance", error.toException());
            }
        });
    }

    // =================== Process payment ===================
    private void processPayment() {
        if (rbVnpay.isChecked()) {
            try {
                // BƯỚC 1: Tạo vé tạm trên Firebase trước để Server IPN có dữ liệu đối chiếu
                createPendingTicket("VNPAY");

                // BƯỚC 2: Sau khi đã có currentTicketId, mới tạo URL
                String paymentUrl = createVnpayUrl(totalPrice);

                if (paymentUrl != null) {
                    openSdk(paymentUrl);
                } else {
                    Toast.makeText(this, "Lỗi tạo link thanh toán", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Payment error: " + e.getMessage());
            }
        } else if (rbBalance.isChecked()) {
            payByBalance();
        } else {
            extra_sound_manager.playError(this);
            Toast.makeText(this, R.string.toast_select_payment_method, Toast.LENGTH_SHORT).show();
        }
    }

    // =================== VNPay ===================
    public void openSdk(String paymentUrl) {
        DatabaseReference ticketRef = FirebaseDatabase.getInstance()
                .getReference("tickets")
                .child(currentTicketId);

        ValueEventListener statusListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.child("status").getValue(String.class);
                if ("PAID".equals(status)) {
                    ticketRef.removeEventListener(this);

                    // KIỂM TRA: Chỉ chuyển màn hình nếu Activity vẫn còn hoạt động
                    if (!isFinishing() && !isDestroyed()) {
                        runOnUiThread(() -> {
                            Intent intent = new Intent(parthome_PaymentActivity.this, TicketDetailActivity.class);
                            intent.putExtra("ticketId", currentTicketId);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        ticketRef.addValueEventListener(statusListener);

        // Mở SDK
        Intent intent = new Intent(this, VNP_AuthenticationActivity.class);
        intent.putExtra("url", paymentUrl);
        intent.putExtra("tmn_code", "C1C16DDU");
        intent.putExtra("scheme", "resultactivity");
        intent.putExtra("is_sandbox", true);

        // QUAN TRỌNG: Không thực hiện finish() hay Toast nặng trong callback này nếu dùng IPN
        VNP_AuthenticationActivity.setSdkCompletedCallback(action -> {
            Log.d("PAYMENT_SDK", "Action: " + action);
        });

        startActivity(intent);
    }

    // =================== Load user info ===================
    private void loadUserInfo(TextView txtUser, TextView txtEmail, TextView txtPhone) {
        currentUser = auth.getCurrentUser();
        if (currentUser == null)
            return;

        String uid = currentUser.getUid();

        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                    return;

                String fullName = snapshot.child("fullName").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);

                // Lưu thông tin để dùng cho gửi email hóa đơn
                userEmail = email != null ? email : "";
                userName = fullName != null ? fullName : getString(R.string.user_name);

                txtUser.setText(userName);
                txtEmail.setText(userEmail);
                txtPhone.setText(phone != null ? phone : getString(R.string.info_not_updated));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // =================== Balance payment ===================
    private void payByBalance() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            return;

        String uid = user.getUid();

        DatabaseReference balanceRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("balance");

        balanceRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                Toast.makeText(this, getString(R.string.toast_balance_error), Toast.LENGTH_SHORT).show();
                return;
            }

            final long serverBalance = parseBalance(task.getResult().getValue());

            balanceRef.runTransaction(new Transaction.Handler() {

                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                    Object localVal = currentData.getValue();
                    long balance = (localVal == null) ? serverBalance : parseBalance(localVal);

                    if (balance < totalPrice)
                        return Transaction.abort();

                    currentData.setValue(balance - totalPrice);
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {

                    if (error != null) {
                        Toast.makeText(parthome_PaymentActivity.this,
                                getString(R.string.error_payment_prefix, error.getMessage()),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!committed) {
                        Toast.makeText(parthome_PaymentActivity.this,
                                R.string.toast_insufficient_balance,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 1. Thực hiện đặt ghế
                    bookSeats(movieTitle, date, time, seats, cinemaId);

                    // 2. Lưu vé và lấy ticketId trả về
                    String newTicketId = saveTicketSuccessByBalance();

                    extra_sound_manager.playSuccess(parthome_PaymentActivity.this);
                    Toast.makeText(parthome_PaymentActivity.this, R.string.toast_payment_success, Toast.LENGTH_SHORT)
                            .show();

                    // 3. CHUYỂN HƯỚNG VỀ TICKET DETAIL (Thay vì Movie Detail)
                    if (newTicketId != null) {
                        // GỬI PUSH NOTIFICATION VỀ VÉ MỚI
                        NotificationHelper notificationHelper = new NotificationHelper(parthome_PaymentActivity.this);
                        notificationHelper.sendNewTicketNotification(
                                movieTitle,
                                newTicketId,
                                cinemaName != null ? cinemaName : "",
                                date,
                                time);

                        // 📧 GỬI EMAIL HÓA ĐƠN TỰ ĐỘNG
                        sendEmailReceipt(newTicketId, getString(R.string.payment_balance_label));

                        Intent intent = new Intent(parthome_PaymentActivity.this, TicketDetailActivity.class);
                        intent.putExtra(TicketDetailActivity.EXTRA_TICKET_ID, newTicketId); // Dùng đúng hằng số key
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        });
    }

    // =================== Điều hướng về Movie Detail ===================
    private void goToMovieDetail() {
        Intent i = new Intent(parthome_PaymentActivity.this, parthome_movie_detail.class);
        i.putExtra("movieID", movieID);
        i.putExtra("movieTitle", movieTitle);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }

    // =================== Save ticket ===================
    private String saveTicketSuccessByBalance() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            return null;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tickets");
        String ticketId = ref.push().getKey();
        if (ticketId == null)
            return null;

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
        return ticketId; // Trả về ID để dùng ở hàm trên
    }

    // ✅ tạo pending + lưu ticketId để lúc return vẫn biết ticket nào cần update
    private void createPendingTicket(String method) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tickets");
        currentTicketId = ref.push().getKey();

        if (currentTicketId == null)
            return;

        savePendingTicketId(currentTicketId);

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
        if (ticketId == null)
            return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("tickets")
                .child(ticketId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", newStatus);
        updates.put("paidAt", System.currentTimeMillis());

        ref.updateChildren(updates);
    }

    private void updateTicketToPaid(String ticketId) {
        if (ticketId == null)
            return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("tickets")
                .child(ticketId);

        // Trước khi update, hãy lấy lại dữ liệu từ Firebase để đảm bảo có movieTitle, cinemaId...
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mTitle = snapshot.child("movieTitle").getValue(String.class);
                String mDate = snapshot.child("date").getValue(String.class);
                String mTime = snapshot.child("time").getValue(String.class);
                String mCinemaId = snapshot.child("cinemaId").getValue(String.class);
                List<String> mSeats = (List<String>) snapshot.child("seats").getValue();

                Map<String, Object> updates = new HashMap<>();
                updates.put("status", "PAID");
                updates.put("payment/status", "PAID");
                updates.put("payment/paidAt", System.currentTimeMillis());

                ref.updateChildren(updates).addOnSuccessListener(unused -> {
                    // 🔥 SỬA TẠI ĐÂY: Dùng dữ liệu vừa lấy từ Firebase để book ghế, tránh dùng biến toàn cục bị null
                    if (mTitle != null && mCinemaId != null) {
                        bookSeats(movieTitle, date, time, seats, cinemaId);
                    }
            // 2. GỬI PUSH NOTIFICATION VỀ VÉ MỚI
            NotificationHelper notificationHelper = new NotificationHelper(parthome_PaymentActivity.this);
            notificationHelper.sendNewTicketNotification(
                    movieTitle,
                    ticketId,
                    cinemaName != null ? cinemaName : "",
                    date,
                    time);

            // 📧 GỬI EMAIL HÓA ĐƠN TỰ ĐỘNG
            sendEmailReceipt(ticketId, "VNPay");

            // 3. Chuyển hướng (Dùng Context từ Activity)
            Intent intent = new Intent(parthome_PaymentActivity.this, TicketDetailActivity.class);
            intent.putExtra("ticketId", ticketId);

                    if (!isFinishing()) {
                        Intent intent = new Intent(parthome_PaymentActivity.this, TicketDetailActivity.class);
                        intent.putExtra("ticketId", ticketId);
                        startActivity(intent);
                        finish();
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void goToTicketDetail(String ticketId) {
        // Giả sử tên Activity của bạn là TicketDetailActivity
        Intent intent = new Intent(parthome_PaymentActivity.this, TicketDetailActivity.class);

        // Truyền ticketId để màn hình sau biết cần load vé nào
        intent.putExtra("TICKET_ID", ticketId);

        // Thêm Flag để xóa các Activity trung gian (như màn hình chọn ghế, thanh toán)
        // Khi nhấn back ở Ticket Detail, nó sẽ về màn hình chính hoặc Movie Detail
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
        finish(); // Đóng màn hình Payment
    }

    // Thêm tham số String targetCinemaId vào cuối
    private void bookSeats(String movieTitle, String date, String time, List<String> selectedSeats, String targetCinemaId) {
        if (movieTitle == null || date == null || time == null || targetCinemaId == null) {
            Log.e("BOOK_SEAT", "Dữ liệu bị null, không thể thực hiện khóa ghế");
            return;
        }

        String showtimeKey = date + "_" + time;

        // Sử dụng targetCinemaId được truyền vào thay vì biến toàn cục
        DatabaseReference seatsRef = FirebaseDatabase.getInstance()
                .getReference("Bookings")
                .child(movieTitle)
                .child(showtimeKey)
                .child("cinemas")
                .child(targetCinemaId)
                .child("seats");

        Map<String, Object> updates = new HashMap<>();
        for (String seat : selectedSeats) {
            if (seat != null) updates.put(seat, "booked");
        }

        seatsRef.updateChildren(updates)
                .addOnSuccessListener(unused -> Log.d("BOOK_SEAT", "Khóa ghế thành công"))
                .addOnFailureListener(e -> Log.e("BOOK_SEAT", "Lỗi khóa ghế: " + e.getMessage()));
    }

    // =================== Helpers ===================
    private long parseBalance(Object val) {
        if (val == null)
            return 0L;
        if (val instanceof Number)
            return ((Number) val).longValue();
        if (val instanceof String) {
            try {
                String s = ((String) val).replaceAll("[^0-9]", "");
                return s.isEmpty() ? 0L : Long.parseLong(s);
            } catch (Exception ignored) {
                return 0L;
            }
        }
        return 0L;
    }

    private SharedPreferences getPrefs() {
        return getSharedPreferences(PREFS, MODE_PRIVATE);
    }

    private void savePendingTicketId(String id) {
        getPrefs().edit().putString(KEY_PENDING_TICKET_ID, id).apply();
    }

    private void clearPendingTicketId() {
        getPrefs().edit().remove(KEY_PENDING_TICKET_ID).apply();
    }

    // =================== VNPay URL ===================
    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes());

            StringBuilder hash = new StringBuilder();
            for (byte b : bytes)
                hash.append(String.format("%02x", b));
            return hash.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String createVnpayUrl(int totalPrice) {
        try {
            String vnp_TmnCode = "C1C16DDU";
            String vnp_HashSecret = "8XWZ093QGUAF75SADH9B1E7KH7NM2SOR";
            String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

            if (currentTicketId == null || currentTicketId.isEmpty()) {
                currentTicketId = String.valueOf(System.currentTimeMillis()); // Backup nếu lỗi ID
            }

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(totalPrice * 100));
            vnp_Params.put("vnp_CurrCode", "VND");

// PHẢI CÓ DÒNG NÀY VÀ GIÁ TRỊ KHÔNG ĐƯỢC RỖNG
            vnp_Params.put("vnp_TxnRef", currentTicketId);

            vnp_Params.put("vnp_OrderInfo", "Thanh toan ve phim");
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", "resultactivity://sdk");
            vnp_Params.put("vnp_IpAddr", "127.0.0.1");

            // Config Thời gian (GMT+7)
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            // Expire Date (15 phút)
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            // --- BẮT ĐẦU XỬ LÝ HASH THEO ĐÚNG CODE VÍ DỤ ---
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);

                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    // Build hash data (Không encode Key, chỉ encode Value)
                    hashData.append(fieldName);
                    hashData.append('=');
                    // Quan trọng: Dùng chuẩn US-ASCII như code demo
                    hashData.append(URLEncoder.encode(fieldValue, "US-ASCII"));

                    // Build query
                    query.append(URLEncoder.encode(fieldName, "US-ASCII"));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, "US-ASCII"));

                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            String queryUrl = query.toString();
            // Dùng hàm hmacSHA512 của bạn để băm chuỗi hashData.toString()
            String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());

            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            return vnp_Url + "?" + queryUrl;

        } catch (Exception e) {
            Log.e("VNPAY_ERROR", "Lỗi tạo URL: " + e.getMessage());
            return null;
        }
    }

    // =================== GỬI EMAIL HÓA ĐƠN ===================
    /**
     * 📧 Gửi email hóa đơn vé xem phim tự động
     * 
     * @param ticketId      Mã vé
     * @param paymentMethod Phương thức thanh toán (Balance/VNPay)
     */
    private void sendEmailReceipt(String ticketId, String paymentMethod) {
        // Kiểm tra email hợp lệ
        if (userEmail == null || userEmail.isEmpty() || !userEmail.contains("@")) {
            Log.w(TAG, "Không thể gửi email hóa đơn: Email người dùng không hợp lệ");
            return;
        }

        EmailHelper emailHelper = new EmailHelper(this);
        emailHelper.sendTicketReceipt(
                userEmail,
                userName,
                movieTitle,
                cinemaName != null ? cinemaName : "N/A",
                date,
                time,
                seats,
                totalPrice,
                ticketId,
                paymentMethod,
                new EmailHelper.EmailCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "📧 Email hóa đơn đã gửi thành công đến: " + userEmail);
                        // Có thể hiển thị Toast thành công nếu muốn
                        // Toast.makeText(parthome_PaymentActivity.this,
                        // "Hóa đơn đã được gửi đến email của bạn!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e(TAG, "❌ Lỗi gửi email hóa đơn: " + errorMessage);
                        // Không hiển thị lỗi cho người dùng vì đây là tính năng phụ
                    }
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
}
