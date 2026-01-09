package com.example.app_movie_booking_ticket;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 📧 EmailHelper - Utility class for sending email receipts
 * 
 * Gửi email hóa đơn vé xem phim tự động đến email người dùng.
 * Sử dụng Gmail SMTP để gửi email.
 * 
 * ⚠️ LƯU Ý QUAN TRỌNG:
 * 1. Cần tạo App Password cho tài khoản Gmail gửi (không dùng password thông
 * thường)
 * 2. Tài khoản Gmail gửi cần bật 2FA và tạo App Password tại:
 * https://myaccount.google.com/apppasswords
 * 3. Thay đổi SENDER_EMAIL và SENDER_PASSWORD bằng thông tin thực tế
 */
public class EmailHelper {

    private static final String TAG = "EmailHelper";

    // ==================== CẤU HÌNH EMAIL GỬI ĐI ====================
    // ⚠️ THAY ĐỔI THÔNG TIN NÀY BẰNG TÀI KHOẢN GMAIL CỦA BẠN
    // Để bảo mật, nên lưu trong BuildConfig hoặc Firebase Remote Config
    private static final String SENDER_EMAIL = "baongdqu@gmail.com"; // Email thực
    private static final String SENDER_PASSWORD = "nxrcynwtpkiksegs"; // App Password
    private static final String SENDER_NAME = "Cinema App"; // Tên hiển thị

    // SMTP Settings cho Gmail
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    private final Context context;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    /**
     * Callback interface for email sending result
     */
    public interface EmailCallback {
        void onSuccess();

        void onFailure(String errorMessage);
    }

    public EmailHelper(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 📧 GỬI EMAIL HÓA ĐƠN VÉ XEM PHIM
     * 
     * @param recipientEmail Email người nhận (người mua vé)
     * @param customerName   Tên khách hàng
     * @param movieTitle     Tên phim
     * @param cinemaName     Tên rạp
     * @param showDate       Ngày chiếu
     * @param showTime       Giờ chiếu
     * @param seats          Danh sách ghế
     * @param totalPrice     Tổng tiền
     * @param ticketId       Mã vé
     * @param paymentMethod  Phương thức thanh toán
     * @param callback       Callback kết quả
     */
    public void sendTicketReceipt(
            String recipientEmail,
            String customerName,
            String movieTitle,
            String cinemaName,
            String showDate,
            String showTime,
            List<String> seats,
            long totalPrice,
            String ticketId,
            String paymentMethod,
            EmailCallback callback) {

        executorService.execute(() -> {
            try {
                // Tạo nội dung email
                String subject = "🎬 Hóa đơn đặt vé xem phim - " + movieTitle;
                String htmlContent = buildTicketReceiptHtml(
                        customerName, movieTitle, cinemaName, showDate, showTime,
                        seats, totalPrice, ticketId, paymentMethod);

                // Gửi email
                sendEmail(recipientEmail, subject, htmlContent);

                // Callback success trên main thread
                mainHandler.post(() -> {
                    if (callback != null)
                        callback.onSuccess();
                });

                Log.d(TAG, "Email hóa đơn đã gửi thành công đến: " + recipientEmail);

            } catch (Exception e) {
                Log.e(TAG, "Lỗi gửi email: " + e.getMessage(), e);

                // Callback failure trên main thread
                mainHandler.post(() -> {
                    if (callback != null)
                        callback.onFailure(e.getMessage());
                });
            }
        });
    }

    /**
     * Gửi email qua Gmail SMTP
     */
    private void sendEmail(String recipientEmail, String subject, String htmlContent)
            throws MessagingException {

        // Cấu hình SMTP Properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        // Tạo session với authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        // Tạo message
        Message message = new MimeMessage(session);

        // Xử lý tên hiển thị: Nếu có tên thì dùng, không thì chỉ để email
        try {
            if (SENDER_NAME != null && !SENDER_NAME.isEmpty()) {
                message.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME));
            } else {
                message.setFrom(new InternetAddress(SENDER_EMAIL));
            }
        } catch (java.io.UnsupportedEncodingException e) {
            message.setFrom(new InternetAddress(SENDER_EMAIL));
        }

        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject(subject);
        message.setContent(htmlContent, "text/html; charset=utf-8");

        // Gửi email
        Transport.send(message);
    }

    /**
     * 🎨 TẠO NỘI DUNG EMAIL HTML ĐẸP MẮT
     */
    private String buildTicketReceiptHtml(
            String customerName,
            String movieTitle,
            String cinemaName,
            String showDate,
            String showTime,
            List<String> seats,
            long totalPrice,
            String ticketId,
            String paymentMethod) {

        // Format tiền VND
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(totalPrice) + "đ";

        // Format danh sách ghế
        String seatsStr = seats != null ? String.join(", ", seats) : "N/A";

        // Format thời gian gửi
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String currentDateTime = dateFormat.format(new Date());

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "</head>" +
                "<body style='margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;'>" +
                "    <table width='100%' cellpadding='0' cellspacing='0' style='max-width: 600px; margin: 0 auto; background-color: #ffffff;'>"
                +
                "        <!-- Header -->" +
                "        <tr>" +
                "            <td style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center;'>"
                +
                "                <h1 style='color: #ffffff; margin: 0; font-size: 28px;'>🎬 Cinema App</h1>" +
                "                <p style='color: #ffffff; margin: 10px 0 0 0; opacity: 0.9;'>Hóa đơn đặt vé xem phim</p>"
                +
                "            </td>" +
                "        </tr>" +

                "        <!-- Greeting -->" +
                "        <tr>" +
                "            <td style='padding: 30px 30px 20px 30px;'>" +
                "                <p style='margin: 0; font-size: 16px; color: #333;'>Xin chào <strong>" + customerName
                + "</strong>,</p>" +
                "                <p style='margin: 10px 0 0 0; font-size: 14px; color: #666;'>Cảm ơn bạn đã đặt vé tại Cinema App! Dưới đây là thông tin chi tiết về vé của bạn:</p>"
                +
                "            </td>" +
                "        </tr>" +

                "        <!-- Ticket Info Box -->" +
                "        <tr>" +
                "            <td style='padding: 0 30px;'>" +
                "                <table width='100%' cellpadding='0' cellspacing='0' style='background-color: #f8f9fa; border-radius: 10px; overflow: hidden;'>"
                +
                "                    <tr>" +
                "                        <td style='padding: 20px; border-left: 4px solid #667eea;'>" +
                "                            <h2 style='margin: 0 0 15px 0; color: #333; font-size: 20px;'>📽️ "
                + movieTitle + "</h2>" +
                "                            <table width='100%' cellpadding='5' cellspacing='0'>" +
                "                                <tr>" +
                "                                    <td style='color: #888; width: 120px;'>🎬 Rạp:</td>" +
                "                                    <td style='color: #333; font-weight: bold;'>" + cinemaName
                + "</td>" +
                "                                </tr>" +
                "                                <tr>" +
                "                                    <td style='color: #888;'>📅 Ngày chiếu:</td>" +
                "                                    <td style='color: #333; font-weight: bold;'>" + showDate + "</td>"
                +
                "                                </tr>" +
                "                                <tr>" +
                "                                    <td style='color: #888;'>🕐 Giờ chiếu:</td>" +
                "                                    <td style='color: #333; font-weight: bold;'>" + showTime + "</td>"
                +
                "                                </tr>" +
                "                                <tr>" +
                "                                    <td style='color: #888;'>💺 Ghế:</td>" +
                "                                    <td style='color: #333; font-weight: bold;'>" + seatsStr + "</td>"
                +
                "                                </tr>" +
                "                            </table>" +
                "                        </td>" +
                "                    </tr>" +
                "                </table>" +
                "            </td>" +
                "        </tr>" +

                "        <!-- Payment Info -->" +
                "        <tr>" +
                "            <td style='padding: 20px 30px;'>" +
                "                <table width='100%' cellpadding='0' cellspacing='0' style='background-color: #e8f5e9; border-radius: 10px;'>"
                +
                "                    <tr>" +
                "                        <td style='padding: 20px;'>" +
                "                            <table width='100%' cellpadding='5' cellspacing='0'>" +
                "                                <tr>" +
                "                                    <td style='color: #666;'>Mã vé:</td>" +
                "                                    <td style='color: #333; text-align: right; font-family: monospace;'>"
                + ticketId + "</td>" +
                "                                </tr>" +
                "                                <tr>" +
                "                                    <td style='color: #666;'>Phương thức:</td>" +
                "                                    <td style='color: #333; text-align: right;'>" + paymentMethod
                + "</td>" +
                "                                </tr>" +
                "                                <tr>" +
                "                                    <td colspan='2'><hr style='border: none; border-top: 1px dashed #ccc; margin: 10px 0;'></td>"
                +
                "                                </tr>" +
                "                                <tr>" +
                "                                    <td style='color: #333; font-size: 18px; font-weight: bold;'>TỔNG CỘNG:</td>"
                +
                "                                    <td style='color: #2e7d32; text-align: right; font-size: 22px; font-weight: bold;'>"
                + formattedPrice + "</td>" +
                "                                </tr>" +
                "                            </table>" +
                "                        </td>" +
                "                    </tr>" +
                "                </table>" +
                "            </td>" +
                "        </tr>" +

                "        <!-- QR Code Reminder -->" +
                "        <tr>" +
                "            <td style='padding: 0 30px 20px 30px;'>" +
                "                <div style='background-color: #fff3e0; border-radius: 10px; padding: 15px; text-align: center;'>"
                +
                "                    <img src='https://api.qrserver.com/v1/create-qr-code/?size=150x150&data="
                + ticketId
                + "' alt='Mã QR Vé' width='150' height='150' style='margin-bottom: 10px; border: 4px solid white; border-radius: 5px;' />"
                +
                "                    <p style='margin: 0; color: #e65100; font-size: 14px;'>" +
                "                        📱 <strong>Lưu ý:</strong> Vui lòng quét mã QR này hoặc mã trong ứng dụng tại quầy để nhận vé!"
                +
                "                    </p>" +
                "                </div>" +
                "            </td>" +
                "        </tr>" +

                "        <!-- Footer -->" +
                "        <tr>" +
                "            <td style='background-color: #333; padding: 20px; text-align: center;'>" +
                "                <p style='margin: 0; color: #999; font-size: 12px;'>Email được gửi tự động lúc "
                + currentDateTime + "</p>" +
                "                <p style='margin: 10px 0 0 0; color: #999; font-size: 12px;'>© 2024 Cinema App. All rights reserved.</p>"
                +
                "            </td>" +
                "        </tr>" +
                "    </table>" +
                "</body>" +
                "</html>";
    }

    /**
     * Cleanup resources
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
