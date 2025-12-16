package com.example.app_movie_booking_ticket.extra;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.app_movie_booking_ticket.R;
import com.example.app_movie_booking_ticket.model.ChatMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Helper class để gọi Google Gemini API
 * Xử lý việc gửi tin nhắn và nhận phản hồi từ AI
 */
public class extra_gemini_helper {

    private static final String TAG = "GeminiHelper";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final String apiKey;
    private final OkHttpClient client;
    private final Handler mainHandler;

    /**
     * Interface callback cho kết quả API
     */
    public interface ChatCallback {
        void onSuccess(String response);

        void onError(String error);
    }

    /**
     * Constructor - khởi tạo với context để lấy API key
     */
    public extra_gemini_helper(Context context) {
        this.apiKey = context.getString(R.string.gemini_api_key);
        this.mainHandler = new Handler(Looper.getMainLooper());

        // Cấu hình OkHttp với timeout phù hợp cho AI response
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Gửi tin nhắn đến Gemini API
     * 
     * @param userMessage Tin nhắn từ người dùng
     * @param history     Lịch sử hội thoại (để context)
     * @param callback    Callback trả về kết quả
     */
    public void sendMessage(String userMessage, List<ChatMessage> history, ChatCallback callback) {
        try {
            // Build request body với system prompt và lịch sử
            JSONObject requestBody = buildRequestBody(userMessage, history);

            Request request = new Request.Builder()
                    .url(API_URL + "?key=" + apiKey)
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .addHeader("Content-Type", "application/json")
                    .build();

            // Gọi API async
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        String responseBody = response.body() != null ? response.body().string() : "";

                        if (response.isSuccessful()) {
                            String text = parseResponse(responseBody);
                            mainHandler.post(() -> callback.onSuccess(text));
                        } else {
                            Log.e(TAG, "API Error: " + response.code() + " - " + responseBody);
                            String errorMsg = parseErrorMessage(responseBody, response.code());
                            mainHandler.post(() -> callback.onError(errorMsg));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Parse error", e);
                        mainHandler.post(() -> callback.onError("Lỗi xử lý phản hồi: " + e.getMessage()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "Network error", e);
                    mainHandler.post(() -> callback.onError("Lỗi kết nối: " + e.getMessage()));
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Build request error", e);
            callback.onError("Lỗi tạo request: " + e.getMessage());
        }
    }

    /**
     * Xây dựng request body với system prompt và tin nhắn
     */
    private JSONObject buildRequestBody(String userMessage, List<ChatMessage> history) throws JSONException {
        JSONObject requestBody = new JSONObject();

        // System instruction (hướng dẫn cho AI)
        JSONObject systemInstruction = new JSONObject();
        JSONArray systemParts = new JSONArray();
        JSONObject systemText = new JSONObject();
        systemText.put("text", buildSystemPrompt());
        systemParts.put(systemText);
        systemInstruction.put("parts", systemParts);
        requestBody.put("system_instruction", systemInstruction);

        // Contents (lịch sử hội thoại + tin nhắn mới)
        JSONArray contents = new JSONArray();

        // Thêm lịch sử hội thoại (giới hạn 10 tin nhắn gần nhất)
        int startIndex = Math.max(0, history.size() - 10);
        for (int i = startIndex; i < history.size(); i++) {
            ChatMessage msg = history.get(i);
            JSONObject content = new JSONObject();
            content.put("role", msg.isUser() ? "user" : "model");

            JSONArray parts = new JSONArray();
            JSONObject textPart = new JSONObject();
            textPart.put("text", msg.getMessage());
            parts.put(textPart);
            content.put("parts", parts);

            contents.put(content);
        }

        // Thêm tin nhắn mới của user
        JSONObject userContent = new JSONObject();
        userContent.put("role", "user");
        JSONArray userParts = new JSONArray();
        JSONObject userText = new JSONObject();
        userText.put("text", userMessage);
        userParts.put(userText);
        userContent.put("parts", userParts);
        contents.put(userContent);

        requestBody.put("contents", contents);

        // Generation config
        JSONObject generationConfig = new JSONObject();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("topK", 40);
        generationConfig.put("topP", 0.95);
        generationConfig.put("maxOutputTokens", 1024);
        requestBody.put("generationConfig", generationConfig);

        return requestBody;
    }

    /**
     * System prompt - định nghĩa vai trò và hành vi của chatbot
     */
    private String buildSystemPrompt() {
        return "Bạn là trợ lý ảo thông minh của ứng dụng đặt vé xem phim.\n\n" +
                "🎬 VAI TRÒ CỦA BẠN:\n" +
                "- Giúp người dùng tìm phim phù hợp với sở thích\n" +
                "- Hướng dẫn cách đặt vé xem phim\n" +
                "- Trả lời câu hỏi về phim đang chiếu, sắp chiếu\n" +
                "- Gợi ý phim hay dựa trên thể loại, diễn viên\n" +
                "- Giải đáp thắc mắc về giá vé, suất chiếu, rạp\n\n" +
                "📝 QUY TẮC TRẢ LỜI:\n" +
                "1. Trả lời bằng tiếng Việt, thân thiện và nhiệt tình\n" +
                "2. Câu trả lời ngắn gọn, dễ hiểu (tối đa 3-4 câu)\n" +
                "3. Sử dụng emoji phù hợp để sinh động hơn\n" +
                "4. Nếu không biết thông tin cụ thể, hãy đề xuất người dùng kiểm tra trong app\n" +
                "5. Luôn kết thúc bằng câu hỏi hoặc gợi ý tiếp theo\n\n" +
                "💡 VÍ DỤ TRẢ LỜI:\n" +
                "- \"Bạn thích xem phim hành động hay phim tình cảm? Mình sẽ gợi ý cho bạn! 🎬\"\n" +
                "- \"Để đặt vé, bạn chọn phim → chọn suất chiếu → chọn ghế → thanh toán nhé! 🎟️\"";
    }

    /**
     * Parse response từ Gemini API
     */
    private String parseResponse(String jsonResponse) throws JSONException {
        JSONObject obj = new JSONObject(jsonResponse);

        // Check for candidates
        if (!obj.has("candidates")) {
            throw new JSONException("No candidates in response");
        }

        JSONArray candidates = obj.getJSONArray("candidates");
        if (candidates.length() == 0) {
            throw new JSONException("Empty candidates array");
        }

        JSONObject firstCandidate = candidates.getJSONObject(0);
        JSONObject content = firstCandidate.getJSONObject("content");
        JSONArray parts = content.getJSONArray("parts");

        if (parts.length() == 0) {
            throw new JSONException("Empty parts array");
        }

        return parts.getJSONObject(0).getString("text");
    }

    /**
     * Parse error message từ API response
     */
    private String parseErrorMessage(String jsonResponse, int statusCode) {
        try {
            JSONObject obj = new JSONObject(jsonResponse);
            if (obj.has("error")) {
                JSONObject error = obj.getJSONObject("error");
                return error.optString("message", "Lỗi không xác định");
            }
        } catch (JSONException e) {
            // Ignore parse error
        }

        switch (statusCode) {
            case 400:
                return "Yêu cầu không hợp lệ";
            case 401:
            case 403:
                return "API key không hợp lệ hoặc hết hạn";
            case 429:
                return "Đã vượt quá giới hạn request. Vui lòng thử lại sau.";
            case 500:
            case 503:
                return "Server đang bận. Vui lòng thử lại sau.";
            default:
                return "Lỗi server: " + statusCode;
        }
    }
}
