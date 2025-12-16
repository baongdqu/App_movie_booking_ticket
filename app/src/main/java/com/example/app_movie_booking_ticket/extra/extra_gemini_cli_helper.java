package com.example.app_movie_booking_ticket.extra;

import android.content.Context;
import android.content.res.Resources;
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
 * Phiên bản đơn giản: Gửi message trực tiếp lên server
 */
public class extra_gemini_cli_helper {

    private static final String TAG = "GeminiCLIHelper";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // Server URL will be fetched dynamically from resources each request
    private String getServerUrl() {
        return context.getString(R.string.gemini_cli_server_url);
    }

    private final OkHttpClient client;
    private final Handler mainHandler;
    private final Context context;
    private final Resources resources;
    private String serverUrl;

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
        this.resources = context.getResources();
        this.mainHandler = new Handler(Looper.getMainLooper());

        // Lấy server URL từ resources
        this.serverUrl = context.getString(R.string.gemini_cli_server_url);

        // Cấu hình OkHttp với timeout dài hơn cho AI processing
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    // Deprecated: server URL is fetched dynamically, no need to set it manually.
    // public void setServerUrl(String url) {
    // // this.serverUrl = url;
    // }

    /**
     * Kiểm tra server có hoạt động không
     */
    public void checkHealth(ChatCallback callback) {
        Request request = new Request.Builder()
                .url(context.getString(R.string.gemini_cli_server_url) + "/api/health")
                .addHeader("ngrok-skip-browser-warning", "true")
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
     * Message được gửi trực tiếp lên server xử lý
     *
     * @param userMessage Tin nhắn từ người dùng
     * @param history     Lịch sử hội thoại (không sử dụng)
     * @param callback    Callback trả về kết quả
     */
    public void sendMessage(String userMessage, List<ChatMessage> history, ChatCallback callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("message", userMessage);
            requestBody.put("user_id", "android_user");

            Request request = new Request.Builder()
                    .url(serverUrl + "/api/chat")
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .addHeader("Content-Type", "application/json")
                    .addHeader("ngrok-skip-browser-warning", "true")
                    .build();

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
     * Gợi ý phim với filter
     */
    public void suggestMovies(String genre, String mood, int count, ChatCallback callback) {
        StringBuilder message = new StringBuilder("Gợi ý ");
        if (count > 0)
            message.append(count).append(" ");
        message.append("phim ");
        if (genre != null && !genre.isEmpty())
            message.append("thể loại ").append(genre).append(" ");
        if (mood != null && !mood.isEmpty())
            message.append("phù hợp tâm trạng ").append(mood);

        sendMessage(message.toString().trim(), null, callback);
    }

    /**
     * Parse response từ REST API
     */
    private String parseResponse(String jsonResponse) throws JSONException {
        JSONObject obj = new JSONObject(jsonResponse);
        boolean success = obj.optBoolean("success", false);

        if (success) {
            return obj.optString("reply", context.getString(R.string.error_unknown));
        } else {
            String error = obj.optString("error", context.getString(R.string.error_api));
            throw new JSONException(error);
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
                return context.getString(R.string.error_api);
            case 502:
            case 503:
                return context.getString(R.string.error_timeout);
            default:
                return "Lỗi server: " + statusCode;
        }
    }
}
