# DANH SÁCH TÍNH NĂNG ỨNG DỤNG MOVIE BOOKING APP

Tài liệu này tổng hợp toàn bộ các tính năng **hiện có** trong mã nguồn và đề xuất các tính năng **tiềm năng** có thể phát triển trong tương lai.

## PHẦN 1: CÁC TÍNH NĂNG HIỆN CÓ (CURRENT FEATURES)

### 1. Hệ thống Xác thực & Tài khoản (Authentication)
*   **Màn hình chờ (Splash Screen)**: Tự động kiểm tra kết nối mạng (Network Check) trước khi vào ứng dụng.
*   **Đăng nhập (Login)**:
    *   Xác thực qua Email/Password với Firebase Authentication.
    *   **Google Sign-In**: Đăng nhập nhanh bằng tài khoản Google.
*   **Đăng ký (Sign Up)**: Tạo tài khoản mới, yêu cầu định dạng email hợp lệ.
*   **Quên mật khẩu (Forgot Password)**: Gửi link reset mật khẩu qua email.
*   **Tự động đăng nhập**: Ghi nhớ trạng thái đăng nhập (Session persistence).

### 2. Trang chủ & Khám phá (Discovery)
*   **Banner Slider**: Trình chiếu tự động các phim nổi bật ở đầu trang (dùng ViewPager2).
*   **Phân loại Phim**:
    *   **Top Movies**: Danh sách phim đang hot (cuộn ngang).
    *   **Upcoming Movies**: Danh sách phim sắp chiếu.
    *   **Xem tất cả**: Màn hình xem danh sách lưới (Grid) cho từng loại.
*   **Tìm kiếm (Search)**: Thanh tìm kiếm ẩn/hiện với animation, tìm phim theo tên realtime.
*   **Chi tiết Phim (Movie Details)**:
    *   Thông tin đầy đủ: Poster, Tên, Giá tiền, Điểm IMDb, Thời lượng, Năm SX.
    *   **Trailer Player**: Phát video trailer trực tuyến (link YouTube/m3u8) ngay trong ứng dụng.
    *   **Cast List**: Danh sách diễn viên có ảnh đại diện.

### 3. Hệ thống Đánh giá & Bình luận (Reviews & Ratings) (*Mới*)
*   **Xem đánh giá**: Người dùng có thể xem danh sách các bình luận từ người dùng khác về phim.
*   **Viết đánh giá**:
    *   Đánh giá theo thang điểm 5 sao.
    *   Viết nội dung bình luận chi tiết (giới hạn ký tự).
*   **Quản lý đánh giá**:
    *   Chỉnh sửa đánh giá đã viết.
    *   Xóa đánh giá của chính mình.

### 4. Tìm kiếm Rạp phim (Cinema Locator) (*Mới*)
*   **Định vị GPS**: Tự động xác định vị trí hiện tại của người dùng (Latitude/Longitude).
*   **Tìm rạp gần đây**: Tính toán và hiển thị danh sách rạp phim được sắp xếp theo khoảng cách gần nhất.
*   **Thông tin Rạp**:
    *   Hiển thị Tên rạp, Địa chỉ cụ thể.
    *   Hiển thị khoảng cách (km).
    *   Trạng thái hoạt động (Đang mở cửa / Đã đóng cửa) dựa trên giờ làm việc thực tế.
    *   Số lượng phòng chiếu (Screens) và tiện ích đi kèm.

### 5. Đặt vé & Thanh toán (Booking Flow)
*   **Chọn Suất chiếu**: Chọn Ngày và Giờ chiếu.
*   **Chọn Ghế (Seat Selection)**:
    *   Giao diện Sơ đồ ghế (Matrix 8 cột).
    *   Trạng thái ghế: Trống (Available), Đã đặt (Booked - màu xám), Đang chọn (Selected - màu hồng).
    *   Tính tiền tự động khi chọn/bỏ chọn ghế.
*   **Thanh toán (Payment)**:
    *   Chọn phương thức: **VNPay Wallet** hoặc **Số dư tài khoản**.
    *   **Deep Link**: Hỗ trợ xử lý kết quả trả về từ cổng thanh toán.
    *   **Lưu vé**: Tạo vé điện tử QR Code sau khi thanh toán thành công.

### 6. Cá nhân hóa & Tiện ích (User Profile & Utilities)
*   **Hồ sơ người dùng**:
    *   Hiển thị Avatar, Tên, Số dư ví.
    *   **Chỉnh sửa**: Upload Avatar (qua API ImgBB), sửa Tên, Ngày sinh, Giới tính.
    *   **Đổi mật khẩu**: Thay đổi password hiện tại.
*   **Sở thích phim (Preferences)**: Lưu trữ thể loại phim yêu thích để Bot gợi ý tốt hơn.
*   **Lịch sử vé (My Tickets)**: Danh sách vé đã mua, xem lại thông tin, trạng thái vé, mã QR.
*   **Hoàn tiền (Refund)**: Tính năng hủy vé và hoàn lại tiền vào ví ảo (nếu chưa quá hạn).

### 7. Chatbot AI (Tư vấn viên ảo)
*   **AI Integration**: Kết nối với Server AI (Gemini).
*   **Context-Aware**: Bot biết thông tin người dùng (Email, Sở thích) để tư vấn cá nhân hóa.
*   **UI Chat hiện đại**:
    *   Bong bóng chat (Bubbles) cho user/bot.
    *   Trạng thái "Đang nhập..." (Typing indicator).
    *   **Quick Replies**: Các nút câu hỏi nhanh.

### 8. Cài đặt & Bảo mật (Settings & Security)
*   **App Lock (Khóa ứng dụng)**:
    *   Bảo vệ bằng mã **PIN 6 số**.
    *   Kích hoạt khi ứng dụng chạy từ nền hoặc khởi động lại.
*   **Giao diện & Ngôn ngữ**:
    *   **Dark Mode**: Bật/Tắt chế độ tối (mặc định là Dark cinematic).
    *   **Đa ngôn ngữ**: Chuyển đổi Tiếng Việt, Anh, Trung, Nhật, Hàn, Nga.
*   **Quản lý Âm thanh**: Tắt/Mở âm thanh hiệu ứng trong app.
*   **Thông báo (Notifications)**: Tab xem danh sách thông báo từ hệ thống.

---

## PHẦN 2: CÁC TÍNH NĂNG TIỀM NĂNG (FUTURE FEATURES)
*Đây là các tính năng chưa có trong mã nguồn hiện tại nhưng có thể phát triển để hoàn thiện ứng dụng:*

1.  **Dữ liệu Rạp thực tế (Cinema Integration)**:
    *   Chọn Rạp/Chi nhánh cụ thể (hiện tại chỉ có chọn phim và giờ).
    *   Bản đồ chỉ đường tới rạp gần nhất (Google Maps Integration).
2.  **Tương tác Cộng đồng (Social)**: Chia sẻ vé hoặc phim hay lên Facebook/Zalo.
3.  **Hệ thống Thông báo Nâng cao (Push Notifications)**: Thông báo nhắc nhở trước giờ chiếu (Reminder).
4.  **Admin Dashboard (Quản trị viên)**: Web riêng để thêm/xóa phim, suất chiếu thay vì chỉnh trực tiếp DB.
5.  **Offline Mode**: Cache danh sách phim để xem khi không có mạng.

---

## PHẦN 3: CHI TIẾT TRIỂN KHAI KỸ THUẬT (TECHNICAL IMPLEMENTATION)
*Bảng dưới đây ánh xạ các tính năng người dùng sang vị trí mã nguồn cụ thể để thuận tiện cho việc bảo trì và phát triển.*

### 1. Hệ thống Xác thực & Tài khoản (Authentication)
| Tính năng                        | Hàm / Logic chính                                      | Vị trí File                                                           |
| :------------------------------- | :----------------------------------------------------- | :-------------------------------------------------------------------- |
| **Màn hình chờ & Kiểm tra mạng** | `startInternetCheck()`, `hasActualInternetAccess()`    | `activities_0_loading.java`                                           |
| **Tự động đăng nhập**            | `proceedToNextScreen()` (check `mAuth.getCurrentUser`) | `activities_0_loading.java`                                           |
| **Đăng nhập**                    | `loginUser()` (Email/Pass), `signIn()` (Google)        | `activities_1_login.java`                                             |
| **Đăng ký / Quên MK**            | `registerUser()`, `resetPassword()`                    | `activities_1_signup.java` / `...forgot_password.java`                |
| **Bảo mật PIN**                  | `validatePin()`, `showSetPinDialog()`                  | `activities_2_a_lock_screen.java` / `partuser_advanced_settings.java` |

### 2. Trang chủ & Khám phá (Discovery)
| Tính năng                    | Hàm / Logic chính                                  | Vị trí File                  |
| :--------------------------- | :------------------------------------------------- | :--------------------------- |
| **Banner Slider**            | `initBanner()` & `setupBanners()` (node `Banners`) | `fragments_home.java`        |
| **List Phim (Top/Upcoming)** | `loadTopMovies()`, `loadUpcomingMovies()`          | `fragments_home.java`        |
| **Tìm kiếm (Search)**        | `filterMovies(String keyword)`                     | `fragments_home.java`        |
| **Chi tiết phim**            | `getIntent().getSerializableExtra("object")`       | `parthome_movie_detail.java` |

### 3. Tìm kiếm Rạp (Cinema Locator)
| Tính năng            | Hàm / Logic chính                                     | Vị trí File             |
| :------------------- | :---------------------------------------------------- | :---------------------- |
| **Lấy vị trí GPS**   | `FusedLocationProviderClient`, `getCurrentLocation()` | `fragments_cinema.java` |
| **Tính khoảng cách** | Haversine formula (`distanceTo`)                      | `model/Cinema.java`     |
| **Sắp xếp gần nhất** | `Collections.sort(cinemaList)`                        | `fragments_cinema.java` |
| **Check giờ mở cửa** | `isCinemaOpen()` (Parse string "HH:mm - HH:mm")       | `fragments_cinema.java` |

### 4. Đánh giá & Bình luận (Reviews)
| Tính năng       | Hàm / Logic chính                          | Vị trí File                        |
| :-------------- | :----------------------------------------- | :--------------------------------- |
| **Lưu Review**  | `mDatabase.child("Reviews").setValue()`    | `parthome_WriteReview.java`        |
| **Load Review** | `reviewRef.addListenerForSingleValueEvent` | `parthome_RatingListActivity.java` |
| **Xóa Review**  | `removeValue()` (kiểm tra `userId` khớp)   | `parthome_WriteReview.java`        |

### 5. Đặt vé & Thanh toán
| Tính năng      | Hàm / Logic chính                                       | Vị trí File                           |
| :------------- | :------------------------------------------------------ | :------------------------------------ |
| **Chọn Ghế**   | `loadSeats()` (logic `booked`, `selected`, `available`) | `parthome_SeatSelectionActivity.java` |
| **Tính tiền**  | `toggleSeat()` (cập nhật `tvTotalPrice`)                | `parthome_SeatSelectionActivity.java` |
| **Thanh toán** | `performPayment()` (VNPAY/Balance)                      | `parthome_PaymentActivity.java`       |
| **Lưu vé**     | `databaseReference.child("tickets").push()`             | `parthome_PaymentActivity.java`       |

### 6. Cá nhân hóa & Tiện ích
| Tính năng      | Hàm / Logic chính                                         | Vị trí File                 |
| :------------- | :-------------------------------------------------------- | :-------------------------- |
| **Vé của tôi** | `loadTickets()` (query node `tickets` theo `userId`)      | `fragments_mail.java`       |
| **Hoàn tiền**  | `refundTicket()` (cộng tiền `users/balance` & set status) | `fragments_mail.java`       |
| **Thông báo**  | `createRefundNotification()`                              | `fragments_mail.java`       |
| **Chatbot AI** | `callGeminiAPI()`, `sendMessage()`                        | `activities_2_chatbot.java` |

### 7. Cài đặt Hệ thống
| Tính năng              | Hàm / Logic chính                   | Vị trí File                       |
| :--------------------- | :---------------------------------- | :-------------------------------- |
| **Đổi ngôn ngữ**       | `extra_language_helper.setLocale()` | `partuser_advanced_settings.java` |
| **Giao diện Tối/Sáng** | `extra_themeutils.applyTheme()`     | `partuser_advanced_settings.java` |
| **Quản lý Âm thanh**   | `SoundManager.playSound()`          | `extra_sound_manager.java`        |
