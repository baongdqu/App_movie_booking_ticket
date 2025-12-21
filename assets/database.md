# Cấu trúc Cơ sở dữ liệu Firebase (Database Structure)

Tài liệu này mô tả chi tiết schema của Firebase Realtime Database được sử dụng trong dự án Movie Booking.

## Tổng quan Layout
Cơ sở dữ liệu bao gồm các node gốc (root nodes) chính sau:

1.  **`Banners`**: Dữ liệu cho slider quảng cáo ở màn hình chính.
2.  **`Items`**: Danh sách phim đang chiếu.
3.  **`Upcomming`**: Danh sách phim sắp chiếu.
4.  **`Bookings`**: Quản lý trạng thái ghế ngồi của các suất chiếu.
5.  **`users`**: Thông tin tài khoản người dùng và ví.
6.  **`tickets`**: Lưu trữ lịch sử vé đã đặt.
7.  **`notifications`**: Thông báo gửi tới người dùng.

---

## Chi tiết từng Node

### 1. `Banners` (List)
Danh sách các phim nổi bật hiển thị trên banner.
*   `name` (Display Name): Tên phim.
*   `image` (URL): Đường dẫn ảnh nền.
*   `genre` (String): Thể loại.
*   `age` (String): Độ tuổi (VD: "+13").
*   `time`: Thời lượng.
*   `year`: Năm phát hành.

### 2. `Items` (List) & `Upcomming` (List)
Chứa thông tin chi tiết về phim. Cấu trúc của 2 node này tương tự nhau.
*   `Title`: Tên phim.
*   `movieID` (Unique ID): Mã định danh phim (VD: `movie001`).
*   `Poster` (URL): Ảnh bìa phim dọc.
*   `Pcitures` (Array of URLs): Danh sách ảnh chi tiết (chỉ có ở `Items`).
*   `Trailer` (URL): Link video trailer (Youtube/Imdb).
*   `Description`: Mô tả nội dung phim.
*   `Imdb` (Number): Điểm đánh giá (VD: 8.5).
*   `Time`: Thời lượng (VD: "2h 46m").
*   `Year`: Năm sản xuất.
*   `price` (Number): Giá vé cơ bản hoặc giá vé tham khảo.
*   `Genre` (Array): Danh sách thể loại (VD: `["Action", "Adventure"]`).
*   `Casts` (Array): Danh sách diễn viên.
    *   `Actor`: Tên diễn viên.
    *   `PicUrl`: Ảnh đại diện diễn viên.

### 3. `Bookings` (Deep Nested Map)
Quản lý chỗ ngồi.
*   **Level 1**: `Tên Phim` (VD: "Dune: Part Two")
    *   **Level 2**: `Suất chiếu` (Format: `YYYY-MM-DD_HH:mm`, VD: `2025-11-08_18:00`)
        *   `pricePerSeat`: Giá vé cụ thể cho suất này.
        *   `seats`: Map trạng thái ghế.
            *   Key: Số ghế (VD: "A1", "B4").
            *   Value: Trạng thái (`"available"` hoặc `"booked"`).

### 4. `users` (Map)
Thông tin người dùng, key là User UID (từ Firebase Auth).
*   `uid`: User ID.
*   `email`: Email đăng nhập.
*   `fullName`: Tên hiển thị.
*   `phone`: Số điện thoại.
*   `avatarUrl`: Ảnh đại diện.
*   `balance` (Number): Số dư ví nội bộ (VD: 475000).
*   `dateOfBirth`: Ngày sinh.
*   `gender`: Giới tính.
*   `moviePreferences` (Object): Sở thích xem phim.
    *   `favoriteGenre`, `favoriteLanguage`, `subtitlePreference`, ...

### 5. `tickets` (Map)
Lưu trữ toàn bộ vé đã đặt. Key là Ticket ID (auto-generated).
*   `ticketId`: ID vé.
*   `userId`: ID người đặt.
*   `movieTitle`: Tên phim.
*   `posterUrl`: Ảnh phim.
*   `date`: Ngày chiếu.
*   `time`: Giờ chiếu.
*   `seats` (Array): Danh sách ghế đã đặt (VD: `["D5", "D7"]`).
*   `totalPrice`: Tổng tiền thanh toán.
*   `status`: Trạng thái vé (`"PAID"`, `"REFUNDED"`).
*   `createdAt`: Timestamp lúc đặt.
*   `payment` (Object): Thông tin thanh toán.
    *   `method`: Phương thức (`"VNPAY"`, `"BALANCE"`).
    *   `status`: Trạng thái thanh toán.
    *   `paidAt`: Thời gian thanh toán.

### 6. `notifications` (Map)
Key cấp 1 là `UserID`, cấp 2 là `NotificationID`.
*   `title`: Tiêu đề thông báo.
*   `message`: Nội dung.
*   `type`: Loại thông báo (`"REFUND"`, `"PROFILE"`, ...).
*   `read` (Boolean): Đã đọc hay chưa.
*   `timestamp`: Thời gian tạo.
*   `ticketId` (Optional): ID vé liên quan (nếu là thông báo hoàn tiền).
