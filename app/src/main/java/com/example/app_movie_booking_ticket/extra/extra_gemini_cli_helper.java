package com.example.app_movie_booking_ticket.extra;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.app_movie_booking_ticket.R;
import com.example.app_movie_booking_ticket.model.ChatMessage;

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
 * Helper class để gọi Gemini CLI REST API Server
 * Server chạy local và được expose qua ngrok
 * 
 * Ưu điểm so với Gemini API trực tiếp:
 * - Sử dụng Gemini CLI với đầy đủ tính năng
 * - Có thể customize prompt templates trên server
 * - Không cần API key trong app (bảo mật hơn)
 */
public class extra_gemini_cli_helper {

    private static final String TAG = "GeminiCLIHelper";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // Server URL - thay đổi theo ngrok URL
    // Có thể lưu vào SharedPreferences hoặc strings.xml để dễ cập nhật
    private String serverUrl;

    private final OkHttpClient client;
    private final Handler mainHandler;
    private final Context context;

    /**
     * Interface callback cho kết quả API
     */
    public interface ChatCallback {
        void onSuccess(String response);

        void onError(String error);
    }

    /**
     * Constructor - khởi tạo với context
     */
    public extra_gemini_cli_helper(Context context) {
        this.context = context;
        this.mainHandler = new Handler(Looper.getMainLooper());

        // Lấy server URL từ resources
        this.serverUrl = context.getString(R.string.gemini_cli_server_url);

        // Cấu hình OkHttp với timeout dài hơn cho AI processing
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS) // Gemini CLI có thể mất thời gian
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Cập nhật server URL (khi ngrok URL thay đổi)
     */
    public void setServerUrl(String url) {
        this.serverUrl = url;
    }

    /**
     * Kiểm tra server có hoạt động không
     */
    public void checkHealth(ChatCallback callback) {
        Request request = new Request.Builder()
                .url(serverUrl + "/health")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    mainHandler.post(() -> callback.onSuccess(context.getString(R.string.server_healthy)));
                } else {
                    mainHandler.post(
                            () -> callback.onError(context.getString(R.string.server_not_responding, response.code())));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mainHandler.post(() -> callback.onError(context.getString(R.string.connect_failed, e.getMessage())));
            }
        });
    }

    /**
     * Gửi tin nhắn đến Gemini CLI Server
     *
     * @param userMessage Tin nhắn từ người dùng
     * @param history     Lịch sử hội thoại (không sử dụng, server quản lý context)
     * @param callback    Callback trả về kết quả
     */
    public void sendMessage(String userMessage, List<ChatMessage> history, ChatCallback callback) {
        try {
            // Build request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("message", userMessage);
            // System prompt mặc định cho chatbot phim
            requestBody.put("system_prompt",
                    "Bạn là trợ lý ảo của rạp chiếu phim. Hãy trả lời khán giả một cách thân thiện, ngắn gọn và hữu ích. Sử dụng tiếng Việt.");

            Request request = new Request.Builder()
                    .url(serverUrl + "/chat")
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
                        mainHandler.post(() -> callback
                                .onError(context.getString(R.string.error_processing_response, e.getMessage())));
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "Network error", e);
                    String errorMsg = context.getString(R.string.server_connect_error_title) + "\n";
                    errorMsg += context.getString(R.string.server_check_list);
                    final String finalError = errorMsg;
                    mainHandler.post(() -> callback.onError(finalError));
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Build request error", e);
            callback.onError(context.getString(R.string.error_creating_request, e.getMessage()));
        }
    }

    /**
     * Gợi ý phim
     */
    public void suggestMovies(String genre, String mood, int count, ChatCallback callback) {
        try {
            // Chuyển sang dùng endpoint /chat vì server không có /suggest
            JSONObject requestBody = new JSONObject();
            String prompt = String.format(
                    "Gợi ý giúp tôi %d phim thể loại %s phù hợp với tâm trạng %s. Chỉ liệt kê tên phim và năm sản xuất.",
                    count, genre, mood);

            requestBody.put("message", prompt);
            requestBody.put("system_prompt", "Bạn là chuyên gia điện ảnh am hiểu các bộ phim.");

            Request request = new Request.Builder()
                    .url(serverUrl + "/chat")
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        if (response.isSuccessful()) {
                            // Sử dụng lại logic parseResponse
                            String suggestions = parseResponse(responseBody);
                            mainHandler.post(() -> callback.onSuccess(suggestions));
                        } else {
                            mainHandler.post(() -> callback.onError("Lỗi gợi ý phim: " + response.code()));
                        }
                    } catch (Exception e) {
                        mainHandler.post(() -> callback.onError("Lỗi: " + e.getMessage()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    mainHandler.post(() -> callback.onError("Lỗi kết nối: " + e.getMessage()));
                }
            });

        } catch (Exception e) {
            callback.onError("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Parse response từ REST API
     */
    private String parseResponse(String jsonResponse) throws JSONException {
        JSONObject obj = new JSONObject(jsonResponse);

        // API mới trả về field "response"
        if (obj.has("response")) {
            return obj.getString("response");
        } else if (obj.has("reply")) { // Fallback, just in case
            return obj.getString("reply");
        } else {
            // Check for error field
            if (obj.has("error")) {
                throw new JSONException(obj.getString("error"));
            }
            return "Không có nội dung phản hồi từ server";
        }
    }

    /**
     * Parse error message từ API response
     */
    private String parseErrorMessage(String jsonResponse, int statusCode) {
        try {
            JSONObject obj = new JSONObject(jsonResponse);
            if (obj.has("error")) {
                return obj.getString("error");
            }
        } catch (JSONException e) {
            // Ignore parse error
        }

        switch (statusCode) {
            case 400:
                return "Yêu cầu không hợp lệ";
            case 404:
                return "API endpoint không tồn tại";
            case 500:
                return "Lỗi server AI";
            case 502:
            case 503:
                return "Server AI đang bận. Vui lòng thử lại sau.";
            default:
                return "Lỗi server: " + statusCode;
        }
    }
}
