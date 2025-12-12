package com.example.app_movie_booking_ticket.activities;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_movie_booking_ticket.R;
import com.example.app_movie_booking_ticket.adapter.ChatMessageAdapter;
import com.example.app_movie_booking_ticket.extra.extra_gemini_helper;
import com.example.app_movie_booking_ticket.model.ChatMessage;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Activity cho màn hình Chatbot
 * Cho phép người dùng chat với AI để được hỗ trợ đặt vé xem phim
 */
public class ChatbotActivity extends AppCompatActivity {

    // UI Components
    private MaterialToolbar toolbar;
    private RecyclerView recyclerChatMessages;
    private EditText inputChatMessage;
    private FloatingActionButton fabSendMessage;
    private LinearLayout layoutTypingIndicator;

    // Quick Reply Chips
    private Chip chipSuggestMovie;
    private Chip chipBookTicket;
    private Chip chipShowtime;
    private Chip chipTicketPrice;

    // Adapter & Helper
    private ChatMessageAdapter adapter;
    private extra_gemini_helper geminiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
        initGeminiHelper();

        // Tin nhắn chào mừng
        showWelcomeMessage();
    }

    /**
     * Khởi tạo các view
     */
    private void initViews() {
        toolbar = findViewById(R.id.toolbarChatbot);
        recyclerChatMessages = findViewById(R.id.recyclerChatMessages);
        inputChatMessage = findViewById(R.id.inputChatMessage);
        fabSendMessage = findViewById(R.id.fabSendMessage);
        layoutTypingIndicator = findViewById(R.id.layoutTypingIndicator);

        // Quick Reply Chips
        chipSuggestMovie = findViewById(R.id.chipSuggestMovie);
        chipBookTicket = findViewById(R.id.chipBookTicket);
        chipShowtime = findViewById(R.id.chipShowtime);
        chipTicketPrice = findViewById(R.id.chipTicketPrice);
    }

    /**
     * Setup toolbar với nút back
     */
    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
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

        // Quick Reply Chips
        chipSuggestMovie.setOnClickListener(v -> sendQuickReply(getString(R.string.chip_suggest_movie)));
        chipBookTicket.setOnClickListener(v -> sendQuickReply(getString(R.string.chip_book_ticket)));
        chipShowtime.setOnClickListener(v -> sendQuickReply(getString(R.string.chip_showtime)));
        chipTicketPrice.setOnClickListener(v -> sendQuickReply(getString(R.string.chip_ticket_price)));
    }

    /**
     * Khởi tạo Gemini Helper
     */
    private void initGeminiHelper() {
        geminiHelper = new extra_gemini_helper(this);
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
        scrollToBottom();

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
        scrollToBottom();

        // Gọi API
        callGeminiAPI(message);
    }

    /**
     * Gọi Gemini API
     */
    private void callGeminiAPI(String message) {
        // Hiển thị typing indicator
        showTypingIndicator(true);
        setInputEnabled(false);

        geminiHelper.sendMessage(message, adapter.getMessages(), new extra_gemini_helper.ChatCallback() {
            @Override
            public void onSuccess(String response) {
                showTypingIndicator(false);
                setInputEnabled(true);

                // Thêm phản hồi từ bot
                adapter.addMessage(new ChatMessage(response, ChatMessage.TYPE_BOT));
                scrollToBottom();
            }

            @Override
            public void onError(String error) {
                showTypingIndicator(false);
                setInputEnabled(true);

                // Hiển thị tin nhắn lỗi
                String errorMessage = "❌ " + error + "\n\nVui lòng thử lại sau nhé!";
                adapter.addMessage(new ChatMessage(errorMessage, ChatMessage.TYPE_BOT));
                scrollToBottom();

                Toast.makeText(ChatbotActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Hiển thị/ẩn typing indicator
     */
    private void showTypingIndicator(boolean show) {
        layoutTypingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * Bật/tắt input field và nút gửi
     */
    private void setInputEnabled(boolean enabled) {
        inputChatMessage.setEnabled(enabled);
        fabSendMessage.setEnabled(enabled);
        fabSendMessage.setAlpha(enabled ? 1.0f : 0.5f);
    }

    /**
     * Scroll đến tin nhắn cuối cùng
     */
    private void scrollToBottom() {
        if (adapter.getItemCount() > 0) {
            recyclerChatMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
        }
    }
}
