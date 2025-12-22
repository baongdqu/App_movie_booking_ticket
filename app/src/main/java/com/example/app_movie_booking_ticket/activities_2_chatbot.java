package com.example.app_movie_booking_ticket;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_movie_booking_ticket.adapter.ChatMessageAdapter;
import com.example.app_movie_booking_ticket.extra.extra_gemini_cli_helper;
import com.example.app_movie_booking_ticket.model.ChatMessage;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Activity cho màn hình Chatbot
 * Cho phép người dùng chat với AI để được hỗ trợ đặt vé xem phim
 * 
 * Sử dụng Gemini CLI Server qua REST API (local server + ngrok)
 * Server chạy trên máy local và được expose qua ngrok
 * 
 * UI/UX: Modern design với gradient, cards, shadows
 */
public class activities_2_chatbot extends AppCompatActivity {

    // UI Components
    private ImageView btnBack;
    private RecyclerView recyclerChatMessages;
    private EditText inputChatMessage;
    private FloatingActionButton fabSendMessage;
    private CardView layoutTypingIndicator;
    private TextView tvTypingIndicator;

    // Quick Reply Buttons (MaterialButton thay vì Chip)
    private MaterialButton chipSuggestMovie;
    private MaterialButton chipBookTicket;
    private MaterialButton chipShowtime;
    private MaterialButton chipTicketPrice;

    // Adapter & Helper
    private ChatMessageAdapter adapter;
    private extra_gemini_cli_helper geminiHelper; // Sử dụng Gemini CLI Server

    // User Preferences Context (Sở thích phim)
    private String userFavoriteGenre = "";
    private String userFavoriteLanguage = "";
    private String userSubtitlePreference = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layouts_2_chatbot);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
        initGeminiHelper();
        loadUserPreferences(); // Load sở thích phim từ Firebase

        // Tin nhắn chào mừng
        showWelcomeMessage();
    }

    /**
     * Khởi tạo các view
     */
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        recyclerChatMessages = findViewById(R.id.recyclerChatMessages);
        inputChatMessage = findViewById(R.id.inputChatMessage);
        fabSendMessage = findViewById(R.id.fabSendMessage);
        layoutTypingIndicator = findViewById(R.id.layoutTypingIndicator);
        tvTypingIndicator = findViewById(R.id.tvTypingIndicator);

        // Quick Reply Buttons
        chipSuggestMovie = findViewById(R.id.chipSuggestMovie);
        chipBookTicket = findViewById(R.id.chipBookTicket);
        chipShowtime = findViewById(R.id.chipShowtime);
        chipTicketPrice = findViewById(R.id.chipTicketPrice);
    }

    /**
     * Setup toolbar với nút back
     */
    private void setupToolbar() {
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    /**
     * Setup RecyclerView với adapter
     */
    private void setupRecyclerView() {
        adapter = new ChatMessageAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Tin nhắn mới ở dưới

        recyclerChatMessages.setLayoutManager(layoutManager);
        recyclerChatMessages.setAdapter(adapter);

        // Smooth scroll khi có tin nhắn mới
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerChatMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    /**
     * Setup các event listeners
     */
    private void setupListeners() {
        // Nút gửi tin nhắn
        fabSendMessage.setOnClickListener(v -> sendMessage());

        // Gửi bằng phím Enter trên bàn phím
        inputChatMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });

        // Quick Reply Buttons
        chipSuggestMovie.setOnClickListener(v -> sendQuickReply(getString(R.string.chip_suggest_movie)));
        chipBookTicket.setOnClickListener(v -> sendQuickReply(getString(R.string.chip_book_ticket)));
        chipShowtime.setOnClickListener(v -> sendQuickReply(getString(R.string.chip_showtime)));
        chipTicketPrice.setOnClickListener(v -> sendQuickReply(getString(R.string.chip_ticket_price)));
    }

    /**
     * Khởi tạo Gemini CLI Helper và kiểm tra kết nối server
     */
    private void initGeminiHelper() {
        geminiHelper = new extra_gemini_cli_helper(this);

        // Kiểm tra kết nối server khi khởi động
        geminiHelper.checkHealth(new extra_gemini_cli_helper.ChatCallback() {
            @Override
            public void onSuccess(String response) {
                // Server đang hoạt động
                runOnUiThread(() -> {
                    // Có thể hiển thị indicator "Online" ở đây
                });
            }

            @Override
            public void onError(String error) {
                // Server không khả dụng - thông báo cho user
                runOnUiThread(() -> {
                    Toast.makeText(activities_2_chatbot.this,
                            getString(R.string.server_not_ready),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * Hiển thị tin nhắn chào mừng
     */
    private void showWelcomeMessage() {
        String welcomeMessage = getString(R.string.chatbot_welcome_message);
        adapter.addMessage(new ChatMessage(welcomeMessage, ChatMessage.TYPE_BOT));
    }

    /**
     * Gửi tin nhắn từ input field
     */
    private void sendMessage() {
        String message = inputChatMessage.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }

        // Thêm tin nhắn user vào danh sách
        adapter.addMessage(new ChatMessage(message, ChatMessage.TYPE_USER));

        // Clear input
        inputChatMessage.setText("");

        // Gọi API
        callGeminiAPI(message);
    }

    /**
     * Gửi Quick Reply
     */
    private void sendQuickReply(String message) {
        // Thêm tin nhắn user
        adapter.addMessage(new ChatMessage(message, ChatMessage.TYPE_USER));

        // Gọi API
        callGeminiAPI(message);
    }

    /**
     * Gọi Gemini CLI Server API
     */
    private void callGeminiAPI(String message) {
        // Hiển thị typing indicator
        showTypingIndicator(true);
        setInputEnabled(false);

        // Chuẩn bị tin nhắn gửi đi (kèm thông tin người dùng)
        StringBuilder contextBuilder = new StringBuilder();

        // Thêm Email người dùng
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            contextBuilder.append("User Email: ").append(user.getEmail()).append("\n");
        }

        // Thêm sở thích phim (User Preferences Context)
        if (!userFavoriteGenre.isEmpty() || !userFavoriteLanguage.isEmpty() || !userSubtitlePreference.isEmpty()) {
            contextBuilder.append("Sở thích phim của người dùng:\n");
            if (!userFavoriteGenre.isEmpty()) {
                contextBuilder.append("- Thể loại yêu thích: ").append(userFavoriteGenre).append("\n");
            }
            if (!userFavoriteLanguage.isEmpty()) {
                contextBuilder.append("- Ngôn ngữ phim: ").append(userFavoriteLanguage).append("\n");
            }
            if (!userSubtitlePreference.isEmpty()) {
                String subtitleText;
                switch (userSubtitlePreference) {
                    case "dubbed":
                        subtitleText = "Lồng tiếng";
                        break;
                    case "subtitled":
                        subtitleText = "Phụ đề";
                        break;
                    case "both":
                        subtitleText = "Cả hai (lồng tiếng và phụ đề)";
                        break;
                    default:
                        subtitleText = userSubtitlePreference;
                }
                contextBuilder.append("- Tùy chọn: ").append(subtitleText).append("\n");
            }
        }

        // Ghép context với tin nhắn người dùng
        String messageToSend;
        if (contextBuilder.length() > 0) {
            messageToSend = contextBuilder.toString() + "\nCâu hỏi: " + message;
        } else {
            messageToSend = message;
        }

        geminiHelper.sendMessage(messageToSend, adapter.getMessages(), new extra_gemini_cli_helper.ChatCallback() {
            @Override
            public void onSuccess(String response) {
                showTypingIndicator(false);
                setInputEnabled(true);

                // Thêm phản hồi từ bot
                adapter.addMessage(new ChatMessage(response, ChatMessage.TYPE_BOT));
            }

            @Override
            public void onError(String error) {
                showTypingIndicator(false);
                setInputEnabled(true);

                // Hiển thị tin nhắn lỗi với style thân thiện
                String errorMessage = getString(R.string.error_prefix) + error + getString(R.string.please_try_again);
                adapter.addMessage(new ChatMessage(errorMessage, ChatMessage.TYPE_BOT));

                Toast.makeText(activities_2_chatbot.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Hiển thị/ẩn typing indicator với animation
     */
    private void showTypingIndicator(boolean show) {
        if (show) {
            // Set random typing text
            String[] phrases = getResources().getStringArray(R.array.chatbot_typing_phrases);
            String randomPhrase = phrases[new Random().nextInt(phrases.length)];
            tvTypingIndicator.setText(randomPhrase);

            layoutTypingIndicator.setVisibility(View.VISIBLE);
            layoutTypingIndicator.setAlpha(0f);
            layoutTypingIndicator.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start();
        } else {
            layoutTypingIndicator.animate()
                    .alpha(0f)
                    .setDuration(150)
                    .withEndAction(() -> layoutTypingIndicator.setVisibility(View.GONE))
                    .start();
        }
    }

    /**
     * Bật/tắt input field và nút gửi với animation
     */
    private void setInputEnabled(boolean enabled) {
        inputChatMessage.setEnabled(enabled);
        fabSendMessage.setEnabled(enabled);

        // Animate FAB alpha
        fabSendMessage.animate()
                .alpha(enabled ? 1.0f : 0.5f)
                .setDuration(150)
                .start();
    }

    /**
     * Load sở thích phim của người dùng từ Firebase
     * Dữ liệu này sẽ được đưa vào prompt để AI cá nhân hóa câu trả lời
     */
    private void loadUserPreferences() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid())
                .child("moviePreferences");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Load thể loại yêu thích
                    String genre = snapshot.child("favoriteGenre").getValue(String.class);
                    if (genre != null && !genre.isEmpty()) {
                        userFavoriteGenre = genre;
                    }

                    // Load ngôn ngữ phim yêu thích
                    String language = snapshot.child("favoriteLanguage").getValue(String.class);
                    if (language != null && !language.isEmpty()) {
                        userFavoriteLanguage = language;
                    }

                    // Load tùy chọn lồng tiếng/phụ đề
                    String subtitle = snapshot.child("subtitlePreference").getValue(String.class);
                    if (subtitle != null && !subtitle.isEmpty()) {
                        userSubtitlePreference = subtitle;
                    }
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                // Không hiển thị lỗi cho user, chỉ log
                android.util.Log.w("ChatbotPrefs", "Failed to load preferences: " + error.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
