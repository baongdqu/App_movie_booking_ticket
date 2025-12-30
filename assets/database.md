# Cấu trúc Cơ sở dữ liệu Firebase (Database Structure)

Tài liệu này mô tả chi tiết schema của Firebase Realtime Database được sử dụng trong dự án Movie Booking, dựa trên dữ liệu thực tế từ file export.

## Tổng quan Layout
Cơ sở dữ liệu bao gồm các node gốc (root nodes) chính sau:

1.  **`Banners`**: Dữ liệu cho slider quảng cáo ở màn hình chính.
2.  **`Bookings`**: Quản lý trạng thái ghế ngồi của các suất chiếu.
3.  **`Cinemas`**: Danh sách các rạp chiếu phim.
4.  **`Movies`**: Danh sách phim (bao gồm cả phim đang chiếu và sắp chiếu).
5.  **`Reviews`**: Đánh giá và bình luận của người dùng về phim.
6.  **`notifications`**: Thông báo gửi tới người dùng.
7.  **`tickets`**: Lưu trữ lịch sử vé đã đặt.
8.  **`users`**: Thông tin tài khoản người dùng và ví.

---

## Chi tiết từng Node

### 1. `Banners` (List)
Danh sách các phim nổi bật hiển thị trên banner.
*   `name` (Display Name): Tên phim.
*   `image` (URL): Đường dẫn ảnh nền chất lượng cao.
*   `genre` (String): Thể loại (VD: "War Action Adventure").
*   `age` (String): Độ tuổi (VD: "+13", "15+").
*   `time`: Thời lượng (VD: "2 Hour").
*   `year`: Năm phát hành (VD: "2023").

### 2. `Bookings` (Deep Nested Map)
Quản lý chỗ ngồi và giá vé cho từng suất chiếu.
*   **Level 1**: `Tên Phim` (VD: "Dune: Part Two", "The Gorge"). *Lưu ý: Sử dụng Tên Phim làm key thay vì MovieID.*
    *   **Level 2**: `Suất chiếu` (Format: `YYYY-MM-DD_HH:mm`, VD: `2025-11-08_18:00`)
        *   `pricePerSeat` (Number): Giá vé cụ thể cho suất này (VD: 90000).
        *   `seats`: Map trạng thái ghế.
            *   Key: Số ghế (VD: "A1", "D5").
            *   Value: Trạng thái (`"available"` hoặc `"booked"`).

### 3. `Cinemas` (List)
Danh sách các rạp chiếu phim hỗ trợ đặt vé.
*   `id` (String): ID rạp (VD: "galaxy_linh_trung").
*   `name`: Tên rạp đầy đủ.
*   `address`: Địa chỉ rạp.
*   `phone`: Số điện thoại liên hệ.
*   `image` (URL): Ảnh rạp.
*   `rating` (Number): Đánh giá trung bình (VD: 4.3).
*   `userRatingsTotal` (Number): Tổng số lượt đánh giá.
*   `latitude` (Number) & `longitude` (Number): Tọa độ bản đồ (cho tính năng chỉ đường).
*   `distanceFromUIT` (Number): Khoảng cách (ước tính).
*   `amenities` (List<String>): Tiện ích (VD: `["2D", "3D", "Dolby 7.1"]`).
*   `screens` (Number): Số lượng phòng chiếu.
*   `workingHours`: Giờ mở cửa.

### 4. `Movies` (List)
Lưu trữ toàn bộ thông tin phim. Node này thay thế cho `Items` và `Upcomming` bằng cách sử dụng cờ `isUpcoming`.
*   `Title`: Tên phim.
*   `movieID` (Unique ID): Mã định danh phim (VD: `movie001`, `vn_1247240`).
*   `Poster` (URL): Ảnh bìa phim dọc.
*   `Pcitures` (Array of URLs): Danh sách ảnh chi tiết *[Optional]*.
*   `Trailer` (URL): Link video trailer (Youtube/Imdb).
*   `Description`: Mô tả nội dung phim (có thể dài).
*   `Imdb` (Number): Điểm đánh giá (VD: 8.5).
*   `Time`: Thời lượng (VD: "2h 46m").
*   `Year`: Năm sản xuất.
*   `price` hoặc `Price` (Number): Giá vé cơ bản (Lưu ý: Có sự không nhất quán về viết hoa chữ cái đầu `p/P`).
*   `Genre` (Array): Danh sách thể loại (VD: `["Action", "Adventure"]`).
*   `isUpcoming` (Boolean): `true` nếu là phim sắp chiếu, `false` nếu đang chiếu.
*   `Casts` (Array): Danh sách diễn viên.
    *   `Actor`: Tên diễn viên.
    *   `Character`: Tên nhân vật.
    *   `PicUrl`: Ảnh đại diện diễn viên (có thể rỗng).

### 5. `Reviews` (Map)
Lưu trữ đánh giá phim.
*   **Key Level 1**: `MovieID` (VD: `movie001`).
    *   **Key Level 2**: `UserID` (VD: `xS5e...`).
        *   `comment`: Nội dung bình luận.
        *   `rating` (Number): Số sao (1-5).
        *   `timestamp`: Thời gian đánh giá.
        *   `userId`: ID người dùng.
        *   `userName`: Tên hiển thị của người dùng.
        *   `userAvatar`: Ảnh đại diện của người dùng.

### 6. `notifications` (Map)
Key cấp 1 là `UserID`, cấp 2 là `NotificationID`.
*   `title`: Tiêu đề thông báo (VD: "Hoàn tiền thành công", "Cập nhật tài khoản").
*   `message`: Nội dung chi tiết.
*   `type`: Loại thông báo (`"REFUND"`, `"PROFILE"`, ...).
*   `read` (Boolean): Đã đọc (`true`) hay chưa (`false`).
*   `timestamp`: Thời gian tạo.
*   `ticketId` (Optional): ID vé liên quan.

### 7. `tickets` (Map)
Lưu trữ toàn bộ vé đã đặt. Key là Ticket ID (auto-generated, VD: `-Og__RX1...`).
*   `ticketId`: ID vé (có thể có hoặc không, thường dùng key của map làm ID).
*   `userId`: ID người đặt.
*   `movieTitle`: Tên phim.
*   `posterUrl`: Ảnh phim.
*   `date`: Ngày chiếu (Format: `YYYY-MM-DD`).
*   `time`: Giờ chiếu (Format: `HH:mm`).
*   `seats` (Array): Danh sách ghế đã đặt (VD: `["D5", "D7"]`).
*   `totalPrice`: Tổng tiền thanh toán.
*   `status`: Trạng thái vé (`"PAID"`, `"REFUNDED"`, `"CANCELLED"`).
*   `createdAt`: Timestamp lúc đặt.
*   `paidAt`: Timestamp lúc thanh toán.
*   `payment` (Object): Thông tin thanh toán.
    *   `method`: Phương thức (`"VNPAY"`, `"BALANCE"`).
    *   `status`: Trạng thái thanh toán (`"PAID"`, `"PENDING"`).
    *   `paidAt`: Thời gian thanh toán.

### 8. `users` (Map)
Thông tin người dùng, key là User UID.
*   `uid`: User ID.
*   `email`: Email đăng nhập.
*   `fullName`: Tên hiển thị.
*   `phone`: Số điện thoại.
*   `avatarUrl`: Ảnh đại diện.
*   `balance` (Number): Số dư ví nội bộ.
*   `dateOfBirth`: Ngày sinh.
*   `gender`: Giới tính.
*   `isPhoneVerified` (Boolean): Trạng thái xác thực số điện thoại.
*   `phoneVerified` (Boolean): (Legacy) Trạng thái xác thực.
*   `moviePreferences` (Object): Sở thích xem phim.
    *   `favoriteGenre`, `favoriteLanguage`, `subtitlePreference`, `genreIndex`, `languageIndex`.
