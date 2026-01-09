package com.example.app_movie_booking_ticket;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

/**
 * NotificationHelper - Quản lý việc gửi thông báo hệ thống Android (Push
 * Notification)
 * 
 * Các loại thông báo:
 * - NEW_TICKET: Khi có vé mới được mua thành công
 * - REFUND: Khi hoàn vé thành công
 * - SYSTEM: Thông báo hệ thống chung
 */
public class NotificationHelper {

    // Channel IDs
    public static final String CHANNEL_TICKETS = "channel_tickets";
    public static final String CHANNEL_NOTIFICATIONS = "channel_notifications";
    public static final String CHANNEL_SYSTEM = "channel_system";

    // Notification IDs (để có thể update hoặc cancel thông báo sau này)
    private static int notificationIdCounter = 1000;

    private final Context context;
    private final NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context.getApplicationContext();
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannels();
    }

    /**
     * Tạo các Notification Channels (bắt buộc cho Android 8.0+)
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel cho thông báo vé
            NotificationChannel ticketChannel = new NotificationChannel(
                    CHANNEL_TICKETS,
                    context.getString(R.string.notification_channel_tickets),
                    NotificationManager.IMPORTANCE_HIGH);
            ticketChannel.setDescription(context.getString(R.string.notification_channel_tickets_desc));
            ticketChannel.enableLights(true);
            ticketChannel.setLightColor(Color.RED);
            ticketChannel.enableVibration(true);
            ticketChannel.setVibrationPattern(new long[] { 0, 300, 200, 300 });

            // Channel cho thông báo chung
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_NOTIFICATIONS,
                    context.getString(R.string.notification_channel_general),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(context.getString(R.string.notification_channel_general_desc));
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);

            // Channel cho thông báo hệ thống
            NotificationChannel systemChannel = new NotificationChannel(
                    CHANNEL_SYSTEM,
                    context.getString(R.string.notification_channel_system),
                    NotificationManager.IMPORTANCE_LOW);
            systemChannel.setDescription(context.getString(R.string.notification_channel_system_desc));

            notificationManager.createNotificationChannel(ticketChannel);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.createNotificationChannel(systemChannel);
        }
    }

    /**
     * Kiểm tra quyền gửi thông báo (Android 13+)
     */
    public boolean hasNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Các phiên bản Android cũ hơn không cần permission này
    }

    /**
     * Gửi thông báo khi có vé mới
     * 
     * @param movieTitle Tên phim
     * @param ticketId   ID của vé (để mở chi tiết vé khi nhấn vào thông báo)
     * @param cinemaName Tên rạp
     * @param date       Ngày chiếu
     * @param time       Giờ chiếu
     */
    public void sendNewTicketNotification(String movieTitle, String ticketId,
            String cinemaName, String date, String time) {
        if (!hasNotificationPermission())
            return;

        String title = context.getString(R.string.push_notification_new_ticket_title);
        String message = context.getString(R.string.push_notification_new_ticket_body,
                movieTitle, cinemaName, date, time);

        // Intent mở TicketDetailActivity khi nhấn vào thông báo
        Intent intent = new Intent(context, TicketDetailActivity.class);
        intent.putExtra(TicketDetailActivity.EXTRA_TICKET_ID, ticketId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                getNextNotificationId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_TICKETS)
                .setSmallIcon(R.drawable.ic_ticket) // Cần có icon này
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setVibrate(new long[] { 0, 300, 200, 300 })
                .setColor(ContextCompat.getColor(context, R.color.netflix_red));

        notificationManager.notify(getNextNotificationId(), builder.build());
    }

    /**
     * Gửi thông báo khi hoàn vé thành công
     * 
     * @param movieTitle   Tên phim
     * @param refundAmount Số tiền hoàn
     */
    public void sendRefundNotification(String movieTitle, long refundAmount) {
        if (!hasNotificationPermission())
            return;

        String title = context.getString(R.string.notification_refund_success_title);
        String message = context.getString(R.string.notification_refund_success_body_with_amount,
                String.valueOf(refundAmount), movieTitle);

        // Intent mở màn hình thông báo khi nhấn vào
        Intent intent = new Intent(context, activities_2_a_menu_manage_fragments.class);
        intent.putExtra("open_tab", "notifications"); // Mở tab thông báo
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                getNextNotificationId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_TICKETS)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.netflix_green));

        notificationManager.notify(getNextNotificationId(), builder.build());
    }

    /**
     * Gửi thông báo chung từ hệ thống
     * 
     * @param title   Tiêu đề thông báo
     * @param message Nội dung thông báo
     * @param type    Loại thông báo (LOGIN, PROFILE, REFUND, SYSTEM)
     */
    public void sendGeneralNotification(String title, String message, String type) {
        if (!hasNotificationPermission())
            return;

        // Intent mở màn hình thông báo
        Intent intent = new Intent(context, activities_2_a_menu_manage_fragments.class);
        intent.putExtra("open_tab", "notifications");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                getNextNotificationId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Chọn icon dựa trên loại thông báo
        int iconRes = getIconForType(type);
        int color = getColorForType(type);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_NOTIFICATIONS)
                .setSmallIcon(iconRes)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(color);

        notificationManager.notify(getNextNotificationId(), builder.build());
    }

    /**
     * Lấy icon phù hợp cho loại thông báo
     */
    private int getIconForType(String type) {
        if (type == null)
            return R.drawable.ic_notifications;

        switch (type) {
            case "REFUND":
                return R.drawable.ic_notifications; // Có thể thay bằng icon hoàn tiền
            case "LOGIN":
                return R.drawable.ic_notifications;
            case "PROFILE":
                return R.drawable.ic_notifications;
            default:
                return R.drawable.ic_notifications;
        }
    }

    /**
     * Lấy màu phù hợp cho loại thông báo
     */
    private int getColorForType(String type) {
        if (type == null)
            return ContextCompat.getColor(context, R.color.accent_blue);

        switch (type) {
            case "REFUND":
                return ContextCompat.getColor(context, R.color.netflix_green);
            case "LOGIN":
                return ContextCompat.getColor(context, R.color.accent_blue);
            case "PROFILE":
                return ContextCompat.getColor(context, R.color.accent_blue);
            default:
                return ContextCompat.getColor(context, R.color.netflix_red);
        }
    }

    /**
     * Tạo ID duy nhất cho mỗi thông báo
     */
    private synchronized int getNextNotificationId() {
        return notificationIdCounter++;
    }

    /**
     * Hủy tất cả thông báo của ứng dụng
     */
    public void cancelAllNotifications() {
        notificationManager.cancelAll();
    }

    /**
     * Hủy một thông báo cụ thể
     */
    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }
}
