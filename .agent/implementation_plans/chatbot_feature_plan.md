# 🤖 KẾ HOẠCH TRIỂN KHAI TÍNH NĂNG CHATBOT HỖ TRỢ

**Ngày tạo:** 12/12/2024  
**Phiên bản:** 1.0  
**Trạng thái:** Đang lên kế hoạch

---

## 📋 TỔNG QUAN

### Mục tiêu
Tích hợp chatbot AI vào ứng dụng đặt vé xem phim để:
- Hỗ trợ người dùng tìm phim, đặt vé nhanh chóng
- Trả lời câu hỏi thường gặp (FAQs)
- Gợi ý phim dựa trên sở thích
- Hỗ trợ 24/7 mà không cần nhân viên

### Công nghệ đề xuất
| Lựa chọn | API | Ưu điểm | Nhược điểm |
|----------|-----|---------|------------|
| **Google Gemini** | Gemini API | Miễn phí tier đầu, hỗ trợ tiếng Việt tốt | Cần Google Cloud account |
| **OpenAI GPT** | ChatGPT API | Mạnh mẽ, phổ biến | Tốn phí, cần VPN |
| **Dialogflow** | Google Dialogflow | Dựng flow dễ, tích hợp Firebase | Học thêm nền tảng mới |
| **Rule-based** | Không cần API | Miễn phí, nhanh | Không thông minh, cứng nhắc |

**👉 Đề xuất:** Sử dụng **Google Gemini API** vì miễn phí tier đầu và hỗ trợ tiếng Việt tốt.

---

## 🏗️ KIẾN TRÚC HỆ THỐNG

```
┌─────────────────────────────────────────────────────────────┐
│                      ANDROID APP                            │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              ChatbotActivity.java                    │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │   │
│  │  │ RecyclerView│  │ EditText    │  │ Send Button │  │   │
│  │  │ (Messages)  │  │ (Input)     │  │             │  │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  │   │
│  └─────────────────────────────────────────────────────┘   │
│                            │                                │
│                            ▼                                │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              ChatbotHelper.java                      │   │
│  │  - sendMessage(String prompt)                        │   │
│  │  - parseResponse(String json)                        │   │
│  │  - buildSystemPrompt()                               │   │
│  └─────────────────────────────────────────────────────┘   │
│                            │                                │
└────────────────────────────┼────────────────────────────────┘
                             │ HTTPS Request
                             ▼
              ┌──────────────────────────────┐
              │      GEMINI API              │
              │  generativelanguage.google   │
              │  .googleapis.com             │
              └──────────────────────────────┘
```

---

## 📁 CẤU TRÚC FILES

```
app/src/main/java/com/example/app_movie_booking_ticket/
├── activities/
│   └── ChatbotActivity.java          # [MỚI] Màn hình chat
│
├── adapter/
│   └── ChatMessageAdapter.java       # [MỚI] Adapter hiển thị tin nhắn
│
├── model/
│   └── ChatMessage.java              # [MỚI] Model tin nhắn
│
├── extra/
│   └── extra_gemini_helper.java      # [MỚI] Helper gọi Gemini API
│
└── ...

app/src/main/res/
├── layout/
│   ├── activity_chatbot.xml          # [MỚI] Layout màn hình chat
│   ├── item_chat_user.xml            # [MỚI] Layout tin nhắn user
│   └── item_chat_bot.xml             # [MỚI] Layout tin nhắn bot
│
├── drawable/
│   ├── bg_chat_user.xml              # [MỚI] Background tin nhắn user
│   ├── bg_chat_bot.xml               # [MỚI] Background tin nhắn bot
│   └── ic_chatbot.xml                # [MỚI] Icon chatbot
│
├── values/
│   └── strings.xml                   # [SỬA] Thêm API key & strings
│
└── ...
```

---

## 🔧 CÁC BƯỚC TRIỂN KHAI CHI TIẾT

### Phase 1: Chuẩn bị (Ước tính: 30 phút)

#### Bước 1.1: Lấy API Key từ Google AI Studio
1. Truy cập: https://aistudio.google.com/apikey
2. Đăng nhập Google account
3. Click "Create API Key"
4. Copy API key

#### Bước 1.2: Thêm API Key vào project
```xml
<!-- res/values/strings.xml -->
<string name="gemini_api_key">YOUR_API_KEY_HERE</string>
```

#### Bước 1.3: Thêm Internet permission (nếu chưa có)
```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET"/>
```

---

### Phase 2: Tạo Model & Helper (Ước tính: 1 giờ)

#### Bước 2.1: Tạo ChatMessage.java
```java
// model/ChatMessage.java
public class ChatMessage {
    public static final int TYPE_USER = 0;
    public static final int TYPE_BOT = 1;
    
    private String message;
    private int type;
    private long timestamp;
    
    public ChatMessage(String message, int type) {
        this.message = message;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters
    public String getMessage() { return message; }
    public int getType() { return type; }
    public long getTimestamp() { return timestamp; }
    
    public boolean isUser() { return type == TYPE_USER; }
    public boolean isBot() { return type == TYPE_BOT; }
}
```

#### Bước 2.2: Tạo extra_gemini_helper.java
```java
// extra/extra_gemini_helper.java
public class extra_gemini_helper {
    private static final String API_URL = 
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";
    
    private final String apiKey;
    private final OkHttpClient client;
    
    public interface ChatCallback {
        void onSuccess(String response);
        void onError(String error);
    }
    
    public extra_gemini_helper(Context context) {
        this.apiKey = context.getString(R.string.gemini_api_key);
        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    }
    
    public void sendMessage(String userMessage, List<ChatMessage> history, ChatCallback callback) {
        // Build request body with system prompt
        String systemPrompt = buildSystemPrompt();
        JSONObject requestBody = buildRequestBody(systemPrompt, userMessage, history);
        
        Request request = new Request.Builder()
            .url(API_URL + "?key=" + apiKey)
            .post(RequestBody.create(requestBody.toString(), 
                  MediaType.parse("application/json")))
            .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String responseBody = response.body().string();
                    String text = parseResponse(responseBody);
                    new Handler(Looper.getMainLooper()).post(() -> 
                        callback.onSuccess(text));
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> 
                        callback.onError(e.getMessage()));
                }
            }
            
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> 
                    callback.onError(e.getMessage()));
            }
        });
    }
    
    private String buildSystemPrompt() {
        return "Bạn là trợ lý ảo của ứng dụng đặt vé xem phim. " +
               "Nhiệm vụ của bạn là:\n" +
               "1. Giúp người dùng tìm phim phù hợp\n" +
               "2. Trả lời câu hỏi về phim đang chiếu\n" +
               "3. Hướng dẫn cách đặt vé\n" +
               "4. Giải đáp thắc mắc về rạp chiếu\n" +
               "Hãy trả lời ngắn gọn, thân thiện bằng tiếng Việt.";
    }
    
    private String parseResponse(String json) throws JSONException {
        JSONObject obj = new JSONObject(json);
        return obj.getJSONArray("candidates")
                  .getJSONObject(0)
                  .getJSONObject("content")
                  .getJSONArray("parts")
                  .getJSONObject(0)
                  .getString("text");
    }
}
```

---

### Phase 3: Tạo UI (Ước tính: 1.5 giờ)

#### Bước 3.1: Tạo activity_chatbot.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background_dark">

    <!-- Toolbar -->
    <com.google.android.material.appbar.MaterialToolbar
        android:id="@+id/toolbar"
        android:layout_width="0dp"
        android:layout_height="?attr/actionBarSize"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:title="Trợ lý ảo"
        app:navigationIcon="@drawable/ic_back"
        android:background="@color/primary"/>

    <!-- Chat Messages -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerChat"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:padding="16dp"
        android:clipToPadding="false"
        app:layout_constraintTop_toBottomOf="@id/toolbar"
        app:layout_constraintBottom_toTopOf="@id/inputLayout"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"/>

    <!-- Input Area -->
    <LinearLayout
        android:id="@+id/inputLayout"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="12dp"
        android:background="@color/surface"
        android:elevation="8dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/inputMessage"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:hint="Nhập tin nhắn..."
            android:maxLines="4"
            android:background="@drawable/bg_input_chat"/>

        <com.google.android.material.floatingactionbutton.FloatingActionButton
            android:id="@+id/btnSend"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="8dp"
            android:src="@drawable/ic_send"
            app:fabSize="mini"
            app:backgroundTint="@color/primary"/>
    </LinearLayout>

    <!-- Loading Indicator -->
    <ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:visibility="gone"
        app:layout_constraintBottom_toTopOf="@id/inputLayout"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginBottom="16dp"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### Bước 3.2: Tạo item_chat_user.xml & item_chat_bot.xml
```xml
<!-- item_chat_user.xml -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:gravity="end"
    android:paddingVertical="4dp">
    
    <TextView
        android:id="@+id/tvMessage"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:maxWidth="280dp"
        android:padding="12dp"
        android:background="@drawable/bg_chat_user"
        android:textColor="@android:color/white"/>
</LinearLayout>

<!-- item_chat_bot.xml -->
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:gravity="start"
    android:paddingVertical="4dp">
    
    <ImageView
        android:layout_width="32dp"
        android:layout_height="32dp"
        android:src="@drawable/ic_chatbot"/>
    
    <TextView
        android:id="@+id/tvMessage"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:maxWidth="280dp"
        android:padding="12dp"
        android:layout_marginStart="8dp"
        android:background="@drawable/bg_chat_bot"
        android:textColor="@color/text_primary"/>
</LinearLayout>
```

---

### Phase 4: Tạo Activity & Adapter (Ước tính: 1.5 giờ)

#### Bước 4.1: Tạo ChatMessageAdapter.java
```java
// adapter/ChatMessageAdapter.java
public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private List<ChatMessage> messages = new ArrayList<>();
    
    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }
    
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ChatMessage.TYPE_USER) {
            View view = inflater.inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_chat_bot, parent, false);
            return new BotViewHolder(view);
        }
    }
    
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).bind(message);
        } else {
            ((BotViewHolder) holder).bind(message);
        }
    }
    
    @Override
    public int getItemCount() {
        return messages.size();
    }
    
    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }
    
    public List<ChatMessage> getMessages() {
        return messages;
    }
    
    // ViewHolders...
}
```

#### Bước 4.2: Tạo ChatbotActivity.java
```java
// activities/ChatbotActivity.java
public class ChatbotActivity extends AppCompatActivity {
    
    private RecyclerView recyclerChat;
    private TextInputEditText inputMessage;
    private FloatingActionButton btnSend;
    private ProgressBar progressBar;
    
    private ChatMessageAdapter adapter;
    private extra_gemini_helper geminiHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        
        initViews();
        setupRecyclerView();
        setupListeners();
        
        geminiHelper = new extra_gemini_helper(this);
        
        // Welcome message
        adapter.addMessage(new ChatMessage(
            "Xin chào! Tôi là trợ lý ảo của rạp phim. Tôi có thể giúp gì cho bạn?",
            ChatMessage.TYPE_BOT
        ));
    }
    
    private void sendMessage() {
        String message = inputMessage.getText().toString().trim();
        if (message.isEmpty()) return;
        
        // Add user message
        adapter.addMessage(new ChatMessage(message, ChatMessage.TYPE_USER));
        inputMessage.setText("");
        scrollToBottom();
        
        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        btnSend.setEnabled(false);
        
        // Call Gemini API
        geminiHelper.sendMessage(message, adapter.getMessages(), 
            new extra_gemini_helper.ChatCallback() {
                @Override
                public void onSuccess(String response) {
                    progressBar.setVisibility(View.GONE);
                    btnSend.setEnabled(true);
                    adapter.addMessage(new ChatMessage(response, ChatMessage.TYPE_BOT));
                    scrollToBottom();
                }
                
                @Override
                public void onError(String error) {
                    progressBar.setVisibility(View.GONE);
                    btnSend.setEnabled(true);
                    Toast.makeText(ChatbotActivity.this, 
                        "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
    
    private void scrollToBottom() {
        recyclerChat.smoothScrollToPosition(adapter.getItemCount() - 1);
    }
}
```

---

### Phase 5: Tích hợp vào App (Ước tính: 30 phút)

#### Bước 5.1: Đăng ký Activity trong AndroidManifest.xml
```xml
<activity
    android:name=".activities.ChatbotActivity"
    android:label="Trợ lý ảo"
    android:theme="@style/Theme.AppMovieBookingTicket"/>
```

#### Bước 5.2: Thêm nút Chatbot vào trang chủ
```xml
<!-- Floating Action Button ở góc phải dưới -->
<com.google.android.material.floatingactionbutton.FloatingActionButton
    android:id="@+id/fabChatbot"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom|end"
    android:layout_margin="16dp"
    android:src="@drawable/ic_chatbot"
    app:backgroundTint="@color/primary"/>
```

#### Bước 5.3: Xử lý click mở Chatbot
```java
// Trong fragments_home.java hoặc MainActivity
fabChatbot.setOnClickListener(v -> {
    Intent intent = new Intent(getContext(), ChatbotActivity.class);
    startActivity(intent);
});
```

---

### Phase 6: Tính năng Nâng cao (Tuỳ chọn)

#### 6.1. Tích hợp dữ liệu phim thực
```java
// Inject danh sách phim vào system prompt
private String buildSystemPrompt() {
    StringBuilder prompt = new StringBuilder();
    prompt.append("Bạn là trợ lý ảo của ứng dụng đặt vé xem phim.\n\n");
    prompt.append("Danh sách phim đang chiếu:\n");
    
    for (Movie movie : currentMovies) {
        prompt.append("- ").append(movie.getTitle())
              .append(" (").append(movie.getGenre()).append(")\n");
    }
    
    return prompt.toString();
}
```

#### 6.2. Quick Replies (Gợi ý nhanh)
```xml
<!-- Thêm HorizontalScrollView với các chip gợi ý -->
<HorizontalScrollView>
    <com.google.android.material.chip.ChipGroup>
        <Chip android:text="Phim hay nhất hôm nay"/>
        <Chip android:text="Cách đặt vé"/>
        <Chip android:text="Giá vé"/>
        <Chip android:text="Gợi ý phim cho tôi"/>
    </ChipGroup>
</HorizontalScrollView>
```

#### 6.3. Lưu lịch sử chat
```java
// Lưu vào SharedPreferences hoặc Room Database
private void saveChatHistory() {
    Gson gson = new Gson();
    String json = gson.toJson(adapter.getMessages());
    prefs.edit().putString("chat_history", json).apply();
}
```

---

## ⏱️ TIMELINE TỔNG QUAN

| Phase | Công việc | Thời gian |
|-------|-----------|-----------|
| 1 | Chuẩn bị (API Key, permissions) | 30 phút |
| 2 | Model & Helper | 1 giờ |
| 3 | UI (Layouts, Drawables) | 1.5 giờ |
| 4 | Activity & Adapter | 1.5 giờ |
| 5 | Tích hợp vào App | 30 phút |
| 6 | Tính năng nâng cao (tuỳ chọn) | 2+ giờ |
| **Tổng** | **MVP hoàn chỉnh** | **~5 giờ** |

---

## ✅ CHECKLIST TRIỂN KHAI

- [ ] Lấy Gemini API Key
- [ ] Thêm API Key vào strings.xml
- [ ] Tạo ChatMessage.java
- [ ] Tạo extra_gemini_helper.java
- [ ] Tạo activity_chatbot.xml
- [ ] Tạo item_chat_user.xml
- [ ] Tạo item_chat_bot.xml
- [ ] Tạo drawable backgrounds
- [ ] Tạo ChatMessageAdapter.java
- [ ] Tạo ChatbotActivity.java
- [ ] Đăng ký Activity trong Manifest
- [ ] Thêm FAB chatbot vào trang chủ
- [ ] Test end-to-end
- [ ] Dịch strings đa ngôn ngữ

---

## 🚀 BƯỚC TIẾP THEO

Khi bạn sẵn sàng, hãy nói: **"Bắt đầu triển khai chatbot"** và tôi sẽ:
1. Tạo tất cả các file cần thiết
2. Hướng dẫn bạn lấy API key
3. Test và debug cùng bạn
