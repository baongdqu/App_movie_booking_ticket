# Cấu trúc toàn bộ Dự án & Danh sách Tệp tin (Project Structure & File List)

Tài liệu này liệt kê toàn bộ các file quan trọng trong mã nguồn dự án "App_movie_booking_ticket", phân loại theo chức năng và ngôn ngữ.

## 1. Mức Dự án (Project Level)

Những file cấu hình chung cho toàn bộ dự án.

*   `build.gradle.kts` (Project): Cấu hình build script gốc, repositories, và dependencies chung.
*   `settings.gradle.kts`: Định nghĩa tên dự án và các module (app).
*   `gradle.properties`: Cấu hình tham số JVM và các thuộc tính Gradle.
*   `local.properties`: Chứa đường dẫn SDK (thường không commit lên git).
*   `README.md`: Tài liệu hướng dẫn chung của dự án.
*   `report.md`: Báo cáo đồ án chi tiết (Đã có sẵn).

## 2. Module App (App Level)

Thư mục `app/` chứa mã nguồn chính của ứng dụng.

*   `build.gradle.kts` (Module): Cấu hình plugin, dependencies (thư viện), version code, minSdk, targetSdk cho module app.
*   `google-services.json`: File cấu hình kết nối Firebase (quan trọng).
*   `proguard-rules.pro`: Quy tắc tối ưu hóa và làm xáo trộn code (obfuscation) khi release.

### 2.1 Mã nguồn Java (`app/src/main/java/com/example/app_movie_booking_ticket/`)

#### A. Activities (Màn hình chính)
*   **Loading & Auth:**
    *   `activities_0_loading.java`: Màn hình chờ, kiểm tra mạng, điều hướng login/main.
    *   `activities_1_login.java`: Xử lý đăng nhập.
    *   `activities_1_signup.java`: Xử lý đăng ký tài khoản.
    *   `activities_1_forgot_password.java`: Xử lý quên mật khẩu.
    *   `activities_2_a_lock_screen.java`: Màn hình khóa PIN bảo mật.
*   **Main Navigation:**
    *   `activities_2_a_menu_manage_fragments.java`: Activity chứa Bottom Navigation, quản lý các Fragment chính.
*   **Feature Activities:**
    *   `activities_2_chatbot.java`: Màn hình chat AI (Gemini).
    *   `parthome_movie_detail.java`: Chi tiết phim.
    *   `parthome_SeatSelectionActivity.java`: Chọn ghế ngồi.
    *   `parthome_PaymentActivity.java`: Thanh toán (VNPAY/Balance).
    *   `parthome_AllMoviesActivity.java`: Danh sách tất cả phim.
    *   `parthome_AllUpcomingActivity.java`: Danh sách phim sắp chiếu.
    *   `extra_ResultActivity.java`: (Ít dùng) Màn hình kết quả.
    *   `partuser_edit_profile.java`: Sửa hồ sơ cá nhân.
    *   `partuser_change_password.java`: Đổi mật khẩu.
    *   `partuser_advanced_settings.java`: Cài đặt nâng cao (mã PIN).
    *   `partuser_movie_preferences.java`: Cài đặt sở thích phim.

#### B. Fragments (Màn hình con trong Menu)
*   `fragments_home.java`: Trang chủ (Carousel, Top movies).
*   `fragments_mail.java`: "Vé của tôi" (Lịch sử đặt vé).
*   `fragments_notifications.java`: Thông báo hệ thống.
*   `fragments_user.java`: Trang cá nhân và menu cài đặt.

#### C. Models (Dữ liệu) (`/model`)
*   `model/Movie.java`: Đối tượng phim.
*   `model/TicketSimple.java`: Đối tượng vé đơn giản.
*   `model/ChatMessage.java`: Tin nhắn chat.
*   `model/AppNotification.java`: Thông báo.
*   `model/SliderItems.kt`: (Kotlin) Item cho banner slide. (Lưu ý: File Kotlin duy nhất trong source Java).

#### D. Adapters (Bộ chuyển đổi dữ liệu cho List/Recycler View) (`/adapter`)
*   `adapter/AllMoviesAdapter.java`: List phim.
*   `adapter/TicketAdapter.java`: List vé.
*   *(Các adapter khác có thể nằm chung hoặc chưa liệt kê chi tiết trong list logic cũ)*

#### E. Helpers & Utilities (Tiện ích) (`/extra` & root)
*   `extra_user.java`: Model User với các logic phụ.
*   `extra_sound_manager.java`: Quản lý âm thanh.
*   `extra_gemini_cli_helper.java`: Gọi API Chatbot.
*   `extra_firebase_helper.java`: Hỗ trợ Firebase.
*   `extra_language_helper.java`: Hỗ trợ đa ngôn ngữ (nếu có).
*   `extra_themeutils.java`: Hỗ trợ giao diện.

### 2.2 Tài nguyên (`app/src/main/res/`)

#### A. Layouts (Giao diện XML) (`/layout`)
*   **Main Screens:**
    *   `layouts_0_loading.xml`, `layouts_1_login.xml`, `layouts_1_signup.xml`...
    *   `layouts_2_a_menu_manage_fragments.xml`
    *   `layouts_fragments_home.xml`, `layouts_fragments_mail.xml`...
*   **Feature Screens:**
    *   `parthome_movie_details.xml`, `parthome_seat_selection.xml`, `parthome_payment.xml`.
    *   `layouts_2_chatbot.xml`.
    *   `partuser_edit_profile.xml`...
*   **Items (Dùng trong danh sách):**
    *   `parthome_item_all_movie.xml`: Item phim dọc.
    *   `parthome_item_cast.xml`: Item diễn viên.
    *   `partmail_item_ticket.xml`: Item vé.
    *   `partchatbot_item_chat_bot.xml`, `partchatbot_item_chat_user.xml`: Bong bóng chat.

#### B. Android Manifest
*   `AndroidManifest.xml`: Khai báo quyền (Internet, VNPAY...), danh sách Activity, và Service của ứng dụng.

## 3. Thư mục `assets/`
Chứa các file tài nguyên thô và tài liệu.

*   `info_FEATURES.md`: Danh sách tính năng.
*   `info_FILM.md`: Dữ liệu phim (JSON/MD).
*   `info_FILES_LOGIC.md`: Tài liệu logic (Vừa tạo).
*   `API_INTEGRATION.md`: Tài liệu tích hợp API.
*   `FILES_LIST.md`: Tài liệu này.
