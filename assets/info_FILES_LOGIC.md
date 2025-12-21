# Cấu trúc Logic và Mối quan hệ giữa các Tệp tin (Project Logic Mapping)

Tài liệu này mô tả chi tiết luồng xử lý (logic flow) và mối quan hệ phụ thuộc giữa các class, activity, fragment và file hỗ trợ trong dự án "App Movie Booking Ticket".

## 1. Luồng Khởi động & Xác thực (Authentication Flow)
Các màn hình này xử lý việc vào ứng dụng và xác thực người dùng.

*   **`activities_0_loading.java` (Màn hình Chờ):** 
    *   **Logic:** Là điểm vào đầu tiên (`Main Launcher`). Kiểm tra kết nối Internet và trạng thái đăng nhập Firebase (`FirebaseAuth`).
    *   **Quan hệ:**
        *   Nếu chưa đăng nhập: Chuyển hướng sang `activities_1_login.java`.
        *   Nếu đã đăng nhập: Kiểm tra xem có bật mã PIN không (`extra_user/shared_prefs`). Nếu có -> `activities_2_a_lock_screen.java`, nếu không -> `activities_2_a_menu_manage_fragments.java`.
*   **`activities_1_login.java` (Đăng nhập):**
    *   **Logic:** Xác thực email/password với Firebase.
    *   **Quan hệ:**
        *   Thành công -> `activities_2_a_menu_manage_fragments.java`.
        *   Quên mật khẩu -> `activities_1_forgot_password.java`.
        *   Chưa có tài khoản -> `activities_1_signup.java`.
*   **`activities_1_signup.java` (Đăng ký):**
    *   **Logic:** Tạo user mới trên Firebase Auth và lưu thông tin chi tiết vào Firebase Realtime Database (`nodes: users`).
    *   **Quan hệ:** Thành công -> Quay lại Login hoặc tự động đăng nhập.
*   **`activities_2_a_lock_screen.java` (Màn hình khóa PIN):**
    *   **Logic:** Bảo vệ ứng dụng khi mở lại. Xác thực mã PIN 6 số.
    *   **Quan hệ:** Nhập đúng -> Mở `activities_2_a_menu_manage_fragments.java` (hoặc activity đích được lưu trong Intent).

## 2. Luồng Chính (Main Navigation Flow)
Đây là xương sống của ứng dụng, nơi người dùng tương tác chính.

*   **`activities_2_a_menu_manage_fragments.java` (Activity Chủ):**
    *   **Logic:** Chứa `BottomNavigationView` để điều hướng giữa các Fragment. Không chứa logic nghiệp vụ nặng, chỉ quản lý ẩn/hiện fragment.
    *   **Quan hệ:** Quản lý 4 Fragment:
        1.  `fragments_home`
        2.  `fragments_mail`
        3.  `fragments_notifications`
        4.  `fragments_user`
*   **`fragments_home.java` (Trang chủ):**
    *   **Logic:** Hiển thị danh sách phim (Đang chiếu, Sắp chiếu, Banner). Lấy dữ liệu từ Firebase (`nodes: Items, Upcomming, Banners`).
    *   **Quan hệ:**
        *   Sử dụng `AllMoviesAdapter` để hiển thị list phim.
        *   Click vào phim -> Mở `parthome_movie_detail.java`.
        *   Click icon Chat -> Mở `activities_2_chatbot.java`.

## 3. Luồng Đặt vé (Booking Flow)
Quy trình cốt lõi của ứng dụng: Xem chi tiết -> Chọn ghế -> Thanh toán -> Kết quả.

*   **`parthome_movie_detail.java` (Chi tiết phim):**
    *   **Logic:** Hiển thị thông tin chi tiết (Cast, Trailer, Nội dung).
    *   **Quan hệ:**
        *   Nhận dữ liệu (Movie Object) từ Intent.
        *   Nút "Đặt vé" -> Mở `parthome_SeatSelectionActivity.java`.
*   **`parthome_SeatSelectionActivity.java` (Chọn ghế):**
    *   **Logic:** Hiển thị sơ đồ ghế. Kiểm tra ghế đã đặt từ Firebase (`nodes: Bookings`). Tính tổng tiền.
    *   **Quan hệ:**
        *   Nhận thông tin phim từ Intent.
        *   Xác nhận chọn ghế -> Mở `parthome_PaymentActivity.java`.
*   **`parthome_PaymentActivity.java` (Thanh toán):**
    *   **Logic:**
        *   Chọn phương thức: VNPAY (SDk) hoặc Ví nội bộ (Balance).
        *   Xử lý giao dịch: Trừ tiền (nếu dùng ví), gọi SDK VNPAY.
        *   Lưu vé: Ghi dữ liệu vào Firebase (`nodes: tickets`) và cập nhật trạng thái ghế (`nodes: Bookings`).
    *   **Quan hệ:**
        *   Thành công -> Hiển thị Toast/Thông báo -> Kết thúc Activity (Trở về trước hoặc về Home).
        *   (Kế hoạch cũ: Có thể mở `ResultActivity` nhưng hiện tại logic xử lý trực tiếp và đóng activity).

## 4. Quản lý Người dùng & Tiện ích (User & Tools)

*   **`fragments_user.java` (Hồ sơ cá nhân):**
    *   **Logic:** Hiển thị thông tin user, số dư ví. Menu cài đặt.
    *   **Quan hệ:** Dẫn tới các activity con:
        *   `partuser_edit_profile.java`: Sửa thông tin.
        *   `partuser_change_password.java`: Đổi mật khẩu.
        *   `activities_lock_screen.java` (để cài đặt PIN).
        *   `partuser_movie_preferences.java`: Sở thích phim.
*   **`fragments_mail.java` (Vé của tôi):**
    *   **Logic:** Hiển thị danh sách vé đã đặt từ Firebase (`nodes: tickets` where `userId` matches).
    *   **Quan hệ:** Sử dụng `TicketAdapter` để hiển thị vé. Xử lý hoàn vé (Refund) -> cập nhật lại số dư ví.
*   **`activities_2_chatbot.java`:**
    *   **Logic:** Giao diện chat AI. Gọi API tới Server Gemini (ngrok).
    *   **Quan hệ:** Độc lập tương đối, được gọi từ Home.
*   **`extra_sound_manager.java`:**
    *   **Logic:** Singleton quản lý phát âm thanh hiệu ứng (click, success, error) toàn app.
*   **`extra_user.java` & `model/Movie.java`:**
    *   **Logic:** Các class Model (POJO) định nghĩa cấu trúc dữ liệu mapping với Firebase.

## 5. Cấu trúc Thư mục & Tiền tố (Naming Convention Logic)
*   **`activities_...`**: Các màn hình cấp cao (Top-level Activities).
*   **`fragments_...`**: Các màn hình con nằm trong Menu chính.
*   **`parthome_...`**: Các màn hình thuộc luồng "Home" (Xem phim, đặt vé).
*   **`partuser_...`**: Các màn hình thuộc luồng "User" (Cài đặt, hồ sơ).
*   **`extra_...`**: Các lớp tiện ích, xử lý logic phụ trợ.
*   **`model.Is...`**: (Dự đoán) chứa các model data object.
