# DANH SÁCH TÍNH NĂNG ỨNG DỤNG MOVIE BOOKING APP

Tài liệu này tổng hợp toàn bộ các tính năng **hiện có** trong mã nguồn và đề xuất các tính năng **tiềm năng** có thể phát triển trong tương lai.

## PHẦN 1: CÁC TÍNH NĂNG HIỆN CÓ (CURRENT FEATURES)

### 1. Hệ thống Xác thực & Tài khoản (Authentication)
*   **Màn hình chờ (Splash Screen)**: Tự động kiểm tra kết nối mạng (Network Check) trước khi vào ứng dụng.
*   **Đăng nhập (Login)**: Xác thực qua Email/Password với Firebase Authentication.
*   **Đăng ký (Sign Up)**: Tạo tài khoản mới, yêu cầu định dạng email hợp lệ.
*   **Quên mật khẩu (Forgot Password)**: Gửi link reset mật khẩu qua email.
*   **Tự động đăng nhập**: Ghi nhớ trạng thái đăng nhập (Session persistence).

### 2. Trang chủ & Khám phá (Discovery)
*   **Banner Slider**: Trình chiếu tự động các phim nổi bật ở đầu trang (dùng ViewPager2).
*   **Phân loại Phim**:
    *   **Top Movies**: Danh sách phim đang hot (cuộn ngang).
    *   **Upcoming Movies**: Danh sách phim sắp chiếu.
    *   **Xem tất cả**: Màn hình xem danh sách lưới (Grid) cho từng loại.
*   **Tìm kiếm (Search)**: Thanh tìm kiếm ẩn/hiện với animation, tìm phim theo tên.
*   **Chi tiết Phim (Movie Details)**:
    *   Thông tin đầy đủ: Poster, Tên, Giá tiền, Điểm IMDb, Thời lượng, Năm SX.
    *   **Trailer Player**: Phát video trailer trực tuyến (link m3u8/mp4) ngay trong ứng dụng.
    *   **Cast List**: Danh sách diễn viên có ảnh đại diện.

### 3. Đặt vé & Thanh toán (Booking Flow)
*   **Chọn Suất chiếu**: Chọn Ngày và Giờ chiếu (dữ liệu demo).
*   **Chọn Ghế (Seat Selection)**:
    *   Giao diện Sơ đồ ghế (Matrix 8 cột).
    *   Trạng thái ghế: Trống (Available), Đã đặt (Booked - màu xám), Đang chọn (Selected - màu hồng).
    *   Tính tiền tự động khi chọn/bỏ chọn ghế.
*   **Thanh toán (Payment)**:
    *   Chọn phương thức: **VNPay Wallet** hoặc **Số dư tài khoản**.
    *   **Deep Link**: Hỗ trợ xử lý kết quả trả về (`ResultActivity`) từ cổng thanh toán.

### 4. Cá nhân hóa & Tiện ích (User Profile & Utilities)
*   **Hồ sơ người dùng**:
    *   Hiển thị Avatar, Tên, Số dư ví.
    *   **Chỉnh sửa**: Upload Avatar (qua API ImgBB), sửa Tên, Ngày sinh, Giới tính.
    *   **Đổi mật khẩu**: Thay đổi password hiện tại.
    *   **Xác thực SĐT**: Quy trình nhập SĐT và xác thực mã OTP.
*   **Sở thích phim (Preferences)**: Lưu trữ thể loại phim yêu thích để Bot gợi ý tốt hơn.
*   **Lịch sử vé (My Tickets)**: Danh sách vé đã mua, bao gồm Mã QR giả lập.
*   **Hoàn tiền (Refund)**: Tính năng hủy vé và hoàn lại tiền vào ví ảo.

### 5. Chatbot AI (Tư vấn viên ảo)
*   **AI Integration**: Kết nối với Server AI (Ollama + Ngrok).
*   **Context-Aware**: Bot biết thông tin người dùng (Email, Sở thích) để tư vấn cá nhân hóa.
*   **UI Chat hiện đại**:
    *   Bong bóng chat (Bubbles) cho user/bot.
    *   Trạng thái "Đang nhập..." (Typing indicator).
    *   **Quick Replies**: Các nút câu hỏi nhanh (Gợi ý phim, Giá vé...).

### 6. Cài đặt & Bảo mật (Settings & Security)
*   **App Lock (Khóa ứng dụng)**:
    *   Bảo vệ bằng mã **PIN 6 số**.
    *   Kích hoạt khi ứng dụng chạy từ nền hoặc khởi động lại.
    *   Tự động xóa PIN khi đăng xuất.
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

2.  **Tương tác Cộng đồng (Social)**:
    *   **Đánh giá & Bình luận**: Cho phép người dùng viết review và chấm điểm phim.
    *   **Chia sẻ**: Share vé hoặc phim hay lên Facebook/Zalo.

3.  **Hệ thống Thông báo Nâng cao (Push Notifications)**:
    *   Thông báo nhắc nhở trước giờ chiếu (Reminder).
    *   Thông báo khi có phim mới thuộc thể loại yêu thích.
    *   *Hiện tại chỉ mới có giao diện xem thông báo, chưa có cơ chế Push từ server.*

4.  **Admin Dashboard (Quản trị viên)**:
    *   App hoặc Web riêng để thêm/xóa phim, suất chiếu thay vì chỉnh trực tiếp DB.
    *   Thống kê doanh thu.

5.  **Vé & Check-in thật**:
    *   Tích hợp mã QR chuẩn để máy quét tại rạp đọc được.
    *   Lưu vé vào Apple Wallet / Google Wallet.

6.  **Thanh toán Thực tế**:
    *   Tích hợp SDK chính thức của MoMo, ZaloPay (hiện tại mới đang ở mức mô phỏng giao diện/logic).
    *   Lưu thẻ thanh toán quốc tế (Visa/Mastercard).

7.  **Offline Mode**:
    *   Cache danh sách phim để xem khi không có mạng (hiện tại app yêu cầu mạng để load ảnh/data).

---

## PHẦN 3: CHI TIẾT TRIỂN KHAI KỸ THUẬT (TECHNICAL IMPLEMENTATION)
*Bảng dưới đây ánh xạ các tính năng người dùng sang vị trí mã nguồn cụ thể để thuận tiện cho việc bảo trì và phát triển.*

### 1. Hệ thống Xác thực & Tài khoản (Authentication)
| Tính năng                        | Hàm / Logic chính                                           | Vị trí File (`.../com/example/app_movie_booking_ticket/...`) |
| :------------------------------- | :---------------------------------------------------------- | :----------------------------------------------------------- |
| **Màn hình chờ & Kiểm tra mạng** | `startInternetCheck()`, `hasActualInternetAccess()`         | `activities_0_loading.java`                                  |
| **Tự động đăng nhập**            | `proceedToNextScreen()` (kiểm tra `mAuth.getCurrentUser()`) | `activities_0_loading.java`                                  |
| **Đăng nhập**                    | `loginUser()` (gọi `mAuth.signInWithEmailAndPassword`)      | `activities_1_login.java`                                    |
| **Gửi lại Email xác thực**       | `txtResendVerify.setOnClickListener`                        | `activities_1_login.java`                                    |
| **Điều hướng Đăng ký / Quên MK** | `btnSignup.setOnClickListener`, `btntxtForgotPassword...`   | `activities_1_login.java`                                    |
| **Xóa tài khoản**                | `dialogConfirmDelete()` (xóa data và user khỏi Firebase)    | `partuser_advanced_settings.java`                            |

### 2. Trang chủ & Khám phá (Discovery)
| Tính năng                     | Hàm / Logic chính                                               | Vị trí File (`.../com/example/app_movie_booking_ticket/...`) |
| :---------------------------- | :-------------------------------------------------------------- | :----------------------------------------------------------- |
| **Banner Slider**             | `initBanner()` & `setupBanners()` (tải từ node `Banners`)       | `fragments_home.java`                                        |
| **Top Movies List**           | `loadTopMovies()` (tải từ node `Items`)                         | `fragments_home.java`                                        |
| **Upcoming Movies List**      | `loadUpcomingMovies()` (tải từ node `Upcomming`)                | `fragments_home.java`                                        |
| **Tìm kiếm (Search)**         | `filterMovies(String keyword)` & `inputSearch.addTextChange...` | `fragments_home.java`                                        |
| **Hiển thị Info User (Mini)** | `loadUserInfo()` (hiển thị tên, số dư ví trên header)           | `fragments_home.java`                                        |

### 3. Đặt vé & Thanh toán (Booking Flow)
| Tính năng               | Hàm / Logic chính                                                    | Vị trí File (`.../com/example/app_movie_booking_ticket/...`) |
| :---------------------- | :------------------------------------------------------------------- | :----------------------------------------------------------- |
| **Chọn Ngày/Giờ chiếu** | `loadAvailableDates()` & `loadShowtimesForDate()`                    | `parthome_SeatSelectionActivity.java`                        |
| **Chọn Ghế & Hiển thị** | `loadSeats()` (logic `booked`, `selected`, `available`)              | `parthome_SeatSelectionActivity.java`                        |
| **Xử lý chọn ghế**      | `toggleSeat()` (cập nhật danh sách chọn và tổng tiền `tvTotalPrice`) | `parthome_SeatSelectionActivity.java`                        |
| **Thanh toán & Vé**     | (Xử lý `PaymentActivity` và lưu vào node `tickets`)                  | `PaymentActivity.java`                                       |

### 4. Cá nhân hóa & Tiện ích (User Utilities)
| Tính năng               | Hàm / Logic chính                                                      | Vị trí File (`.../com/example/app_movie_booking_ticket/...`) |
| :---------------------- | :--------------------------------------------------------------------- | :----------------------------------------------------------- |
| **Danh sách vé đã mua** | `loadTickets()` (query node `tickets` theo `userId`)                   | `fragments_mail.java`                                        |
| **Hoàn tiền (Refund)**  | `refundTicket()` (cộng tiền `users/balance` & set status `REFUNDED`)   | `fragments_mail.java`                                        |
| **Thông báo hoàn tiền** | `createRefundNotification()` (tạo noti mới trong node `notifications`) | `fragments_mail.java`                                        |

### 5. Chatbot AI
| Tính năng              | Hàm / Logic chính                                            | Vị trí File (`.../com/example/app_movie_booking_ticket/...`) |
| :--------------------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| **Gửi/Nhận tin nhắn**  | `sendMessage()` & `callGeminiAPI()`                          | `activities_2_chatbot.java`                                  |
| **Gắn ngữ cảnh User**  | `messageToSend = "User Email: " ...` (trong `callGeminiAPI`) | `activities_2_chatbot.java`                                  |
| **Kiểm tra Server AI** | `initGeminiHelper()` -> `checkHealth()`                      | `activities_2_chatbot.java`                                  |

### 6. Cài đặt & Bảo mật (Settings)
| Tính năng                 | Hàm / Logic chính                                                    | Vị trí File (`.../com/example/app_movie_booking_ticket/...`) |
| :------------------------ | :------------------------------------------------------------------- | :----------------------------------------------------------- |
| **App Lock (Mã PIN)**     | `switchPinLock`, `showSetPinDialog` (Lưu Prefs `app_pin`)            | `partuser_advanced_settings.java`                            |
| **Check Lock lúc mở App** | `proceedToNextScreen()` (check `pin_enabled` trong `AppSettings`)    | `activities_0_loading.java`                                  |
| **Đổi ngôn ngữ**          | `btnChangeLanguage.setOnClickListener` (gọi `extra_language_helper`) | `partuser_advanced_settings.java`                            |
| **Giao diện Tối/Sáng**    | `switchDarkMode` (gọi `extra_themeutils`)                            | `partuser_advanced_settings.java`                            |
