# Cấu trúc toàn bộ Dự án & Danh sách Tệp tin (Project Structure & File List)

Tài liệu này liệt kê toàn bộ các file quan trọng trong mã nguồn dự án "App_movie_booking_ticket", phân loại theo chức năng và ngôn ngữ.

## 1. Mức Dự án (Project Level)

Những file cấu hình chung cho toàn bộ dự án.

*   `build.gradle.kts` (Project): Cấu hình build script gốc, repositories, và dependencies chung.
*   `settings.gradle.kts`: Định nghĩa tên dự án và các module (app).
*   `gradle.properties`: Cấu hình tham số JVM và các thuộc tính Gradle.
*   `local.properties`: Chứa đường dẫn SDK (thường không commit lên git).
*   `README.md`: Tài liệu hướng dẫn chung của dự án.
*   `report.md`: Báo cáo đồ án chi tiết.

## 2. Module App (App Level)

Thư mục `app/` chứa mã nguồn chính của ứng dụng.

*   `build.gradle.kts` (Module): Cấu hình plugin, dependencies (thư viện), version code, minSdk, targetSdk cho module app.
*   `google-services.json`: File cấu hình kết nối Firebase.
*   `proguard-rules.pro`: Quy tắc tối ưu hóa và làm xáo trộn code (obfuscation).

### 2.1 Mã nguồn Java (`app/src/main/java/com/example/app_movie_booking_ticket/`)

#### A. Activities (Màn hình chính)
*   **Loading & Auth:**
    *   `activities_0_loading.java`: Màn hình chờ, kiểm tra mạng, điều hướng.
    *   `activities_1_login.java`: Xử lý đăng nhập.
    *   `activities_1_signup.java`: Xử lý đăng ký tài khoản.
    *   `activities_1_forgot_password.java`: Xử lý quên mật khẩu.
    *   `activities_2_a_lock_screen.java`: Màn hình khóa PIN bảo mật.
*   **Main Navigation:**
    *   `activities_2_a_menu_manage_fragments.java`: Activity chứa Bottom Navigation, quản lý các Fragment.
*   **Movie & Booking:**
    *   `parthome_movie_detail.java`: Chi tiết phim.
    *   `parthome_SeatSelectionActivity.java`: Chọn ghế ngồi.
    *   `parthome_PaymentActivity.java`: Thanh toán (VNPAY/Balance).
    *   `parthome_AllMoviesActivity.java`: Danh sách tất cả phim.
    *   `parthome_AllMoviesFullActivity.java`: Danh sách đầy đủ (Full).
    *   `parthome_AllUpcomingActivity.java`: Danh sách phim sắp chiếu.
    *   `parthome_WriteReview.java`: Viết/Sửa đánh giá phim.
    *   `parthome_RatingListActivity.java`: Xem danh sách đánh giá.
*   **Cinema Feature:**
    *   `CinemaDetailActivity.java`: Chi tiết rạp phim.
*   **Utilities & Chat:**
    *   `activities_2_chatbot.java`: Màn hình chat AI (Gemini).
    *   `extra_ResultActivity.java`: Màn hình kết quả.
*   **User Settings:**
    *   `partuser_edit_profile.java`: Sửa hồ sơ cá nhân.
    *   `partuser_change_password.java`: Đổi mật khẩu.
    *   `partuser_advanced_settings.java`: Cài đặt nâng cao (mã PIN, ngôn ngữ, giao diện).
    *   `partuser_movie_preferences.java`: Cài đặt sở thích phim.

#### B. Fragments (Màn hình con trong Menu)
*   `fragments_home.java`: Trang chủ (Carousel, Top movies).
*   `fragments_cinema.java`: (*Mới*) Tìm kiếm rạp chiếu phim gần đây (Location based).
*   `fragments_mail.java`: "Vé của tôi" (Lịch sử đặt vé).
*   `fragments_notifications.java`: Thông báo hệ thống.
*   `fragments_user.java`: Trang cá nhân và menu cài đặt.

#### C. Models (Dữ liệu) (`/model`)
*   `model/Movie.java`: Đối tượng phim.
*   `model/Cinema.java`: Đối tượng rạp phim.
*   `model/TicketSimple.java`: Đối tượng vé đơn giản.
*   `model/ChatMessage.java`: Tin nhắn chat.
*   `model/AppNotification.java`: Thông báo.
*   `model/ReviewModel.java`: Đối tượng đánh giá/bình luận.
*   `model/SliderItems.kt`: (Kotlin) Item cho banner slide.

#### D. Adapters (Bộ chuyển đổi dữ liệu) (`/adapter`)
*   `adapter/AllMoviesAdapter.java`: List phim (Home).
*   `adapter/TopMovieAdapter.java`: List phim hot.
*   `adapter/TicketAdapter.java`: List vé của tôi.
*   `adapter/CinemaAdapter.java`: List rạp phim.
*   `adapter/ReviewAdapter.java`: List đánh giá.
*   `adapter/CastListAdapter.java`: List diễn viên.
*   `adapter/MovieImageAdapter.java`: List ảnh liên quan.
*   `adapter/NotificationAdapter.java`: List thông báo.
*   `adapter/ChatMessageAdapter.java`: List tin nhắn chat.
*   `adapter/SliderAdapter.kt`: Adapter cho banner slider.

#### E. Helpers & Utilities (`/extra` & root)
*   `extra/extra_user.java`: Helper quản lý User.
*   `extra/extra_sound_manager.java`: Quản lý âm thanh.
*   `extra/extra_gemini_helper.java`: Helper gọi API Gemini AI.
*   `extra/extra_gemini_cli_helper.java`: Helper CLI cho AI.
*   `extra/extra_firebase_helper.java`: Hỗ trợ Firebase.
*   `extra/extra_language_helper.java`: Hỗ trợ đa ngôn ngữ.
*   `extra/extra_manager_language.java`: Quản lý ngôn ngữ.
*   `extra/extra_themeutils.java`: Hỗ trợ giao diện (Dark/Light).
*   `extra/extra_google_signin_helper.java`: Logic đăng nhập Google.

### 2.2 Tài nguyên (`app/src/main/res/`)

#### A. Layouts (Giao diện XML) (`/layout`)
*   **Main & Auth:**
    *   `layouts_0_loading.xml`, `layouts_1_login.xml`, `layouts_1_signup.xml`, `layouts_1_forgot_password.xml`
    *   `layouts_2_a_menu_manage_fragments.xml`, `layouts_2_a_lock_screen.xml`
*   **Fragments:**
    *   `layouts_fragments_home.xml`, `layouts_fragments_cinema.xml`, `layouts_fragments_mail.xml`, `layouts_fragments_notifications.xml`, `layouts_fragments_user.xml`
*   **Modules:**
    *   `parthome_movie_details.xml`, `parthome_seat_selection.xml`, `parthome_payment.xml`
    *   `parthome_write_review.xml`, `parthome_rating_list.xml`
    *   `layouts_cinema_detail.xml`
    *   `layouts_2_chatbot.xml`
    *   `partuser_edit_profile.xml`, `partuser_change_password.xml`, `partuser_advanced_settings.xml`, `partuser_movie_preferences.xml`
*   **Items (Recycler Items):**
    *   `parthome_item_all_movie.xml`, `parthome_item_top_movie.xml`, `parthome_item_cast.xml`, `parthome_item_movieimages.xml`
    *   `partcinema_item_cinema.xml`
    *   `parthome_viewholder_review.xml`
    *   `partmail_item_ticket.xml`
    *   `partnortifications_item_notification.xml`
    *   `partchatbot_item_chat_bot.xml`, `partchatbot_item_chat_user.xml`

#### B. Android Manifest
*   `AndroidManifest.xml`: Khai báo quyền (Location, Internet...), Activity, Service.

## 3. Thư mục `assets/`
*   `info_FEATURES.md`: Danh sách tính năng.
*   `info_FILM.md`: Dữ liệu phim.
*   `info_FILES_LOGIC.md`: Tài liệu logic hệ thống.
*   `info_COUNT_CODE.md`: Thống kê dòng code.
*   `info_FILES_LIST.md`: Tài liệu này.
