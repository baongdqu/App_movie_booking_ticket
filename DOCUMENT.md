# TÀI LIỆU CHI TIẾT - ỨNG DỤNG ĐẶT VÉ XEM PHIM

**Tên ứng dụng:** App Movie Booking Ticket  
**Ngày cập nhật:** 03/12/2025  
**Phiên bản:** 1.0  
**Nền tảng:** Android  

---

## 📋 MỤC LỤC

1. [Tổng Quan Ứng Dụng](#1-tổng-quan-ứng-dụng)
2. [Công Nghệ Sử Dụng](#2-công-nghệ-sử-dụng)
3. [Cấu Trúc Dự Án](#3-cấu-trúc-dự-án)
4. [Chức Năng Chi Tiết](#4-chức-năng-chi-tiết)
5. [Cơ Sở Dữ Liệu](#5-cơ-sở-dữ-liệu)
6. [Các Màn Hình Chính](#6-các-màn-hình-chính)
7. [Hướng Dẫn Cài Đặt](#7-hướng-dẫn-cài-đặt)
8. [Hướng Dẫn Sử Dụng](#8-hướng-dẫn-sử-dụng)

---

## 1. TỔNG QUAN ỨNG DỤNG

### 1.1. Giới Thiệu
Ứng dụng đặt vé xem phim là một nền tảng di động cho phép người dùng:
- Xem danh sách phim đang chiếu và sắp chiếu
- Đặt vé xem phim trực tuyến
- Quản lý thông tin cá nhân
- Xem thông tin chi tiết về phim, diễn viên
- Chọn ghế ngồi trong rạp

### 1.2. Đối Tượng Người Dùng
- Người yêu thích xem phim
- Người muốn đặt vé trực tuyến tiện lợi
- Độ tuổi: 16+

### 1.3. Mục Tiêu
- Cung cấp trải nghiệm đặt vé nhanh chóng, dễ dàng
- Giao diện thân thiện, hiện đại
- Tích hợp Firebase cho bảo mật cao
- Hỗ trợ đa ngôn ngữ (Tiếng Việt)

---

## 2. CÔNG NGHỆ SỬ DỤNG

### 2.1. Ngôn Ngữ Lập Trình
- **Java**: Ngôn ngữ chính cho Android
- **Kotlin**: Sử dụng cho một số model (SliderItems)
- **XML**: Thiết kế giao diện

### 2.2. Framework & Libraries

#### Firebase Services
```gradle
- Firebase Authentication: Xác thực người dùng
- Firebase Realtime Database: Lưu trữ dữ liệu realtime
- Firebase Storage: Lưu trữ hình ảnh
```

#### UI/UX Libraries
```gradle
- Material Design Components: Giao diện hiện đại
- Glide: Tải và hiển thị hình ảnh
- CircleImageView: Hiển thị avatar dạng tròn
- RecyclerView: Danh sách động
- ViewPager2: Slider hình ảnh
```

#### Network & Image Upload
```gradle
- OkHttp3: HTTP client
- ImgBB API: Upload hình ảnh avatar
```

#### Other Libraries
```gradle
- AndroidX AppCompat: Tương thích ngược
- ConstraintLayout: Layout linh hoạt
- CardView: Hiển thị thẻ
```

### 2.3. API Bên Ngoài
- **ImgBB API**: Upload và lưu trữ hình ảnh avatar
  - Endpoint: `https://api.imgbb.com/1/upload`
  - Chức năng: Upload ảnh, trả về URL ảnh

---

## 3. CẤU TRÚC DỰ ÁN

### 3.1. Package Structure

```
com.example.app_movie_booking_ticket/
│
├── activities/
│   ├── activities_0_loading.java              # Màn hình loading
│   ├── activities_1_login.java                # Màn hình đăng nhập
│   ├── activities_1_signup.java               # Màn hình đăng ký
│   ├── activities_1_forgot_password.java      # Quên mật khẩu
│   ├── activities_2_menu_manage_fragments.java # Menu chính
│   ├── activities_3_edit_profile.java         # Chỉnh sửa hồ sơ
│   ├── activities_3_change_password.java      # Đổi mật khẩu
│   ├── activities_3_advanced_settings.java    # Cài đặt nâng cao
│   ├── activities_4_movie_detail.java         # Chi tiết phim
│   ├── SeatSelectionActivity.java             # Chọn ghế
│   ├── AllMoviesActivity.java                 # Tất cả phim
│   └── AllUpcomingActivity.java               # Phim sắp chiếu
│
├── fragments/
│   ├── fragments_home.java                    # Fragment trang chủ
│   ├── fragments_user.java                    # Fragment người dùng
│   ├── fragments_mail.java                    # Fragment tin nhắn
│   └── fragments_notifications.java           # Fragment thông báo
│
├── adapter/
│   ├── [Các adapter cho RecyclerView]
│
├── model/
│   ├── Movie.java                             # Model phim
│   ├── MovieTest.java                         # Test model
│   └── SliderItems.kt                         # Model slider
│
├── extra/
│   ├── extra_user.java                        # Model người dùng
│   ├── extra_firebase_helper.java             # Firebase helper
│   ├── extra_sound_manager.java               # Quản lý âm thanh
│   └── extra_themeutils.java                  # Quản lý theme
│
└── [Other files]
```

### 3.2. Layout Structure

```
res/layout/
├── layouts_0_loading.xml
├── layouts_1_login.xml
├── layouts_1_signup.xml
├── layouts_1_forgot_password.xml
├── layouts_2_menu_manage_fragments.xml
├── layouts_3_edit_profile.xml
├── layouts_3_change_password.xml
├── layouts_3_advanced_settings.xml
├── layouts_fragments_home.xml
├── layouts_fragments_user.xml
├── layouts_fragments_mail.xml
├── layouts_fragments_notifications.xml
├── activity_4_movie_details.xml
├── activity_seat_selection.xml
├── activity_all_movies.xml
├── activity_all_upcoming.xml
├── item_all_movie.xml
├── item_cast.xml
├── item_movieimages.xml
├── item_top_movie.xml
└── viewholder_slider.xml
```

---

## 4. CHỨC NĂNG CHI TIẾT

### 4.1. Xác Thực & Quản Lý Tài Khoản

#### 4.1.1. Đăng Ký (`activities_1_signup.java`)
**Chức năng:**
- Đăng ký tài khoản mới với email & mật khẩu
- Xác thực email thông qua Firebase Authentication
- Lưu thông tin người dùng vào Firebase Realtime Database

**Dữ liệu thu thập:**
```java
- uid: String (ID duy nhất)
- fullName: String (Họ và tên)
- email: String (Email)
- phone: String (Số điện thoại)
- dateOfBirth: String (Ngày sinh - format: dd/MM/yyyy)
- gender: String (Giới tính)
- avatarUrl: String (URL avatar - mặc định)
```

**Quy trình:**
1. Người dùng nhập thông tin
2. Validation dữ liệu đầu vào
3. Tạo tài khoản Firebase Auth
4. Gửi email xác thực
5. Lưu thông tin vào Database
6. Chuyển về màn hình đăng nhập

**Avatar mặc định:**
```
https://i.ibb.co/C3JdHS1r/Avatar-trang-den.png
```

#### 4.1.2. Đăng Nhập (`activities_1_login.java`)
**Chức năng:**
- Đăng nhập bằng email & mật khẩu
- Lưu trạng thái đăng nhập (SharedPreferences)
- Xác thực email trước khi cho phép đăng nhập
- Hiệu ứng âm thanh khi thành công/thất bại

**Tính năng đặc biệt:**
- Tích hợp Sound Manager cho UX tốt hơn
- Lưu thông tin người dùng local
- Kiểm tra trạng thái xác thực email

#### 4.1.3. Quên Mật Khẩu (`activities_1_forgot_password.java`)
**Chức năng:**
- Gửi email reset mật khẩu
- Sử dụng Firebase Auth Password Reset

### 4.2. Quản Lý Hồ Sơ

#### 4.2.1. Chỉnh Sửa Hồ Sơ (`activities_3_edit_profile.java`)

**Chức năng chính:**
1. **Xem thông tin cá nhân**
   - Họ và tên
   - Số điện thoại
   - Ngày sinh
   - Giới tính
   - Avatar

2. **Chỉnh sửa thông tin**
   - Cập nhật họ tên
   - Cập nhật số điện thoại
   - Chọn ngày sinh qua DatePickerDialog
   - Chọn giới tính qua dropdown
   - Thay đổi avatar

3. **Upload Avatar**
   - Chọn ảnh từ thư viện
   - Tự động resize (max 1024px)
   - Nén JPEG (quality 80%)
   - Upload lên ImgBB
   - Lưu URL vào Firebase

**Code quan trọng:**

```java
// Date Picker
private void setupDatePicker() {
    inputDob.setOnClickListener(v -> {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        // Parse existing date if available
        String currentDob = inputDob.getText().toString();
        if (!currentDob.isEmpty()) {
            String[] parts = currentDob.split("/");
            if (parts.length == 3) {
                day = Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1]) - 1;
                year = Integer.parseInt(parts[2]);
            }
        }
        
        DatePickerDialog dialog = new DatePickerDialog(
            this,
            (view, year, month, day) -> {
                String date = String.format("%02d/%02d/%d", day, month + 1, year);
                inputDob.setText(date);
            },
            year, month, day
        );
        dialog.show();
    });
}

// Gender Dropdown
private void setupGenderDropdown() {
    String[] genders = {"Nam", "Nữ", "Khác"};
    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        this, 
        android.R.layout.simple_dropdown_item_1line, 
        genders
    );
    inputGender.setAdapter(adapter);
}

// Upload to ImgBB
private void uploadToImgBB(Uri imageUri, String uid, Map<String, Object> updates) {
    // Resize image
    Bitmap original = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
    int maxDim = 1024;
    float scale = Math.min(1f, (float) maxDim / Math.max(width, height));
    Bitmap scaled = Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
    
    // Compress to JPEG
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    scaled.compress(Bitmap.CompressFormat.JPEG, 80, baos);
    String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
    
    // Upload via OkHttp
    RequestBody formBody = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("key", getString(R.string.imgbb_api_key))
        .addFormDataPart("image", encodedImage)
        .build();
        
    Request request = new Request.Builder()
        .url("https://api.imgbb.com/1/upload")
        .post(formBody)
        .build();
    // ... handle response
}
```

#### 4.2.2. Đổi Mật Khẩu (`activities_3_change_password.java`)
**Chức năng:**
- Nhập mật khẩu cũ
- Nhập mật khẩu mới
- Xác nhận mật khẩu mới
- Cập nhật qua Firebase Auth

#### 4.2.3. Cài Đặt Nâng Cao (`activities_3_advanced_settings.java`)
**Chức năng:**
- Thay đổi theme (sáng/tối)
- Cài đặt thông báo
- Ngôn ngữ
- Âm thanh
- Các tùy chọn khác

### 4.3. Xem Phim

#### 4.3.1. Trang Chủ (`fragments_home.java`)
**Hiển thị:**
- Slider phim nổi bật (ViewPager2)
- Danh sách phim đang chiếu
- Danh sách phim sắp chiếu
- Top phim được yêu thích

**Tính năng:**
- Tải dữ liệu từ Firebase
- Hiển thị hình ảnh qua Glide
- Click để xem chi tiết

#### 4.3.2. Chi Tiết Phim (`activities_4_movie_detail.java`)
**Hiển thị:**
- Poster phim
- Tên phim
- Thể loại, thời lượng
- Mô tả
- Danh sách diễn viên
- Trailer (nếu có)

**Tính năng:**
- Nút "Đặt vé" → Chuyển đến chọn ghế
- Gallery hình ảnh phim

#### 4.3.3. Chọn Ghế (`SeatSelectionActivity.java`)
**Chức năng:**
- Hiển thị sơ đồ ghế rạp
- Chọn/bỏ chọn ghế
- Hiển thị trạng thái ghế (trống/đã đặt/đang chọn)
- Tính tổng tiền
- Xác nhận đặt vé

**Loại ghế:**
- Standard: Ghế thường
- VIP: Ghế VIP
- Couple: Ghế đôi

### 4.4. Quản Lý Người Dùng

#### 4.4.1. Fragment User (`fragments_user.java`)
**Hiển thị:**
- Avatar người dùng
- Tên người dùng
- Email
- Menu tùy chọn:
  - Chỉnh sửa hồ sơ
  - Đổi mật khẩu
  - Lịch sử đặt vé
  - Cài đặt
  - Đăng xuất

---

## 5. CƠ SỞ DỮ LIỆU

### 5.1. Firebase Realtime Database Structure

```
app-movie-booking/
│
├── users/
│   ├── {uid}/
│   │   ├── uid: String
│   │   ├── fullName: String
│   │   ├── email: String
│   │   ├── phone: String
│   │   ├── dateOfBirth: String (format: dd/MM/yyyy)
│   │   ├── gender: String ("Nam", "Nữ", "Khác")
│   │   └── avatarUrl: String
│   │
│
├── movies/
│   ├── {movieId}/
│   │   ├── title: String
│   │   ├── description: String
│   │   ├── genre: String
│   │   ├── duration: Number
│   │   ├── rating: Number
│   │   ├── posterUrl: String
│   │   ├── trailerUrl: String
│   │   ├── releaseDate: String
│   │   ├── status: String (now_showing, upcoming)
│   │   └── cast: Array
│   │
│
├── bookings/
│   ├── {bookingId}/
│   │   ├── userId: String
│   │   ├── movieId: String
│   │   ├── seats: Array
│   │   ├── showtime: String
│   │   ├── totalPrice: Number
│   │   ├── status: String
│   │   └── createdAt: Timestamp
│   │
│
└── settings/
    ├── {userId}/
    │   ├── theme: String
    │   ├── notifications: Boolean
    │   └── sound: Boolean
```

### 5.2. Model Classes

#### 5.2.1. User Model (`extra_user.java`)

```java
public class extra_user {
    public String uid;
    public String fullName;
    public String email;
    public String phone;
    public String dateOfBirth;
    public String gender;
    private String avatarUrl;
    
    // Constructor rỗng - bắt buộc cho Firebase
    public extra_user() {}
    
    // Constructor đầy đủ
    public extra_user(String uid, String fullName, String email, 
                     String phone, String dateOfBirth, String gender) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }
    
    // Getters & Setters
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
```

#### 5.2.2. Movie Model (`model/Movie.java`)

```java
public class Movie {
    private String id;
    private String title;
    private String description;
    private String genre;
    private int duration;
    private double rating;
    private String posterUrl;
    private String trailerUrl;
    private String releaseDate;
    private String status;
    private List<String> cast;
    
    // Constructors, Getters & Setters
}
```

### 5.3. SharedPreferences

**Lưu trữ local:**
```java
SharedPreferences: "UserPrefs"
├── uid: String          (User ID)
├── username: String     (Tên người dùng)
├── email: String        (Email)
└── isLoggedIn: Boolean  (Trạng thái đăng nhập)
```

---

## 6. CÁC MÀN HÌNH CHÍNH

### 6.1. Loading Screen
- **File:** `layouts_0_loading.xml`, `activities_0_loading.java`
- **Mục đích:** Màn hình khởi động ứng dụng
- **Thời gian:** 2-3 giây
- **Chuyển tiếp:** → Login hoặc Menu (nếu đã đăng nhập)

### 6.2. Authentication Screens

#### Login Screen
- **File:** `layouts_1_login.xml`, `activities_1_login.java`
- **Components:**
  - Email input
  - Password input
  - Login button
  - Forgot password link
  - Sign up link
- **Validation:** Email format, password không trống

#### Sign Up Screen
- **File:** `layouts_1_signup.xml`, `activities_1_signup.java`
- **Components:**
  - Full name input
  - Email input
  - Phone input
  - Password input
  - Confirm password input
  - Sign up button
- **Validation:** Tất cả fields, password match

#### Forgot Password Screen
- **File:** `layouts_1_forgot_password.xml`
- **Components:**
  - Email input
  - Send reset link button

### 6.3. Main Menu Screen
- **File:** `layouts_2_menu_manage_fragments.xml`
- **Structure:** Bottom Navigation với 4 tabs
  1. Home (Trang chủ)
  2. Mail (Tin nhắn)
  3. Notifications (Thông báo)
  4. User (Người dùng)

### 6.4. Profile Screens

#### Edit Profile Screen
- **File:** `layouts_3_edit_profile.xml`, `activities_3_edit_profile.java`
- **Components:**
  - Avatar (CircleImageView) - có thể thay đổi
  - Change Avatar button
  - Full Name (TextInputEditText)
  - Phone (TextInputEditText)
  - Date of Birth (TextInputEditText + DatePickerDialog)
  - Gender (AutoCompleteTextView - Dropdown)
  - Save button
  - Cancel button

**Layout Code:**
```xml
<!-- Date of Birth -->
<com.google.android.material.textfield.TextInputLayout
    android:id="@+id/tilDobEdit"
    style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:hint="Ngày sinh (dd/MM/yyyy)">
    
    <com.google.android.material.textfield.TextInputEditText
        android:id="@+id/inputDobEdit"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:focusable="false"
        android:clickable="true"
        android:inputType="none"/>
</com.google.android.material.textfield.TextInputLayout>

<!-- Gender -->
<com.google.android.material.textfield.TextInputLayout
    android:id="@+id/tilGenderEdit"
    style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox.ExposedDropdownMenu"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:hint="Giới tính">
    
    <AutoCompleteTextView
        android:id="@+id/inputGenderEdit"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="none"/>
</com.google.android.material.textfield.TextInputLayout>
```

**Quy trình sử dụng:**
1. Người dùng mở "Edit Profile"
2. Click vào field "Ngày sinh" → DatePickerDialog hiện lên
3. Chọn ngày/tháng/năm → Click OK → Ngày được hiển thị dạng dd/MM/yyyy
4. Click vào field "Giới tính" → Dropdown hiện 3 options: Nam, Nữ, Khác
5. Chọn một option
6. Click "Save" → Dữ liệu được lưu vào Firebase

#### Change Password Screen
- **File:** `layouts_3_change_password.xml`
- **Components:**
  - Current password
  - New password
  - Confirm new password
  - Change button

#### Advanced Settings Screen
- **File:** `layouts_3_advanced_settings.xml`
- **Components:**
  - Theme selector (Light/Dark)
  - Notification settings
  - Sound settings
  - Language settings

### 6.5. Movie Screens

#### Home Fragment
- **File:** `layouts_fragments_home.xml`
- **Components:**
  - Welcome text
  - Search bar
  - Slider (ViewPager2)
  - "Now Showing" section (RecyclerView)
  - "Upcoming" section (RecyclerView)
  - "Top Movies" section (RecyclerView)

#### Movie Details Screen
- **File:** `activity_4_movie_details.xml`
- **Components:**
  - Poster image
  - Title
  - Rating, Genre, Duration
  - Description
  - Cast list (RecyclerView)
  - Image gallery
  - Book ticket button

#### All Movies Screen
- **File:** `activity_all_movies.xml`
- **Components:**
  - RecyclerView grid layout
  - Movie cards with poster & title

#### Seat Selection Screen
- **File:** `activity_seat_selection.xml`
- **Components:**
  - Screen indicator
  - Seat grid
  - Legend (Available/Selected/Booked)
  - Selected seats info
  - Total price
  - Confirm button

---

## 7. HƯỚNG DẪN CÀI ĐẶT

### 7.1. Yêu Cầu Hệ Thống
- Android Studio: Arctic Fox trở lên
- JDK: 11 trở lên
- Android SDK: API 24+ (Android 7.0+)
- Gradle: 7.0+

### 7.2. Các Bước Cài Đặt

#### Bước 1: Clone Project
```bash
git clone <repository-url>
cd App_movie_booking_ticket
```

#### Bước 2: Cấu Hình Firebase
1. Truy cập [Firebase Console](https://console.firebase.google.com/)
2. Tạo project mới hoặc sử dụng project có sẵn
3. Thêm Android app với package name: `com.example.app_movie_booking_ticket`
4. Tải file `google-services.json`
5. Đặt file vào: `app/google-services.json`

#### Bước 3: Cấu Hình ImgBB API
1. Đăng ký tài khoản tại [ImgBB](https://imgbb.com/)
2. Lấy API key từ dashboard
3. Thêm vào `res/values/strings.xml`:
```xml
<string name="imgbb_api_key">YOUR_API_KEY_HERE</string>
```

#### Bước 4: Sync & Build
1. Mở project trong Android Studio
2. Sync Gradle files
3. Build → Clean Project
4. Build → Rebuild Project

#### Bước 5: Run
1. Kết nối thiết bị Android hoặc khởi động emulator
2. Run app (Shift + F10)

### 7.3. Cấu Hình Firebase Chi Tiết

#### Authentication
1. Firebase Console → Authentication
2. Sign-in method → Enable "Email/Password"
3. Templates → Customize email verification template

#### Realtime Database
1. Firebase Console → Realtime Database
2. Create Database
3. Setup Rules:
```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    },
    "movies": {
      ".read": true,
      ".write": "auth != null"
    },
    "bookings": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    }
  }
}
```

---

## 8. HƯỚNG DẪN SỬ DỤNG

### 8.1. Cho Người Dùng Mới

#### Đăng Ký Tài Khoản
1. Mở ứng dụng
2. Click "Sign Up"
3. Nhập thông tin:
   - Họ và tên
   - Email
   - Số điện thoại
   - Mật khẩu (tối thiểu 6 ký tự)
4. Click "Sign Up"
5. Kiểm tra email → Click link xác thực
6. Quay lại app → Đăng nhập

#### Đăng Nhập
1. Nhập email & mật khẩu
2. Click "Login"
3. Nếu quên mật khẩu → Click "Forgot Password?"

### 8.2. Cập Nhật Hồ Sơ

#### Chỉnh Sửa Thông Tin
1. Đăng nhập vào app
2. Chuyển đến tab "User"
3. Click "Edit Profile"
4. Cập nhật các thông tin:
   - **Họ tên**: Nhập trực tiếp
   - **Số điện thoại**: Nhập trực tiếp
   - **Ngày sinh**:
     - Click vào field
     - Chọn ngày/tháng/năm từ calendar
     - Click OK
   - **Giới tính**:
     - Click vào dropdown
     - Chọn: Nam / Nữ / Khác
5. Click "Save" để lưu

#### Thay Đổi Avatar
1. Trong màn hình "Edit Profile"
2. Click "Change Avatar"
3. Chọn ảnh từ thư viện
4. Ảnh tự động resize & upload
5. Click "Save"

### 8.3. Đặt Vé Xem Phim

#### Tìm Phim
1. Vào tab "Home"
2. Duyệt qua:
   - Slider phim nổi bật
   - Phim đang chiếu
   - Phim sắp chiếu
3. Hoặc dùng thanh tìm kiếm

#### Xem Chi Tiết
1. Click vào poster phim
2. Xem thông tin:
   - Mô tả
   - Diễn viên
   - Thời lượng
   - Đánh giá
3. Xem trailer (nếu có)

#### Đặt Vé
1. Trong màn hình chi tiết phim
2. Click "Book Ticket"
3. Chọn suất chiếu
4. Chọn ghế ngồi:
   - Xanh: Trống
   - Đỏ: Đã đặt
   - Vàng: Đang chọn
5. Xác nhận & thanh toán
6. Nhận vé điện tử

### 8.4. Các Tính Năng Khác

#### Xem Lịch Sử
1. Tab "User" → "Booking History"
2. Xem danh sách vé đã đặt

#### Thay Đổi Theme
1. Tab "User" → "Settings"
2. Chọn "Theme"
3. Light / Dark / Auto

#### Đổi Mật Khẩu
1. Tab "User" → "Change Password"
2. Nhập mật khẩu cũ
3. Nhập mật khẩu mới
4. Xác nhận
5. Click "Change"

---

## PHỤ LỤC

### A. Troubleshooting

#### Lỗi Đăng Nhập
- **Vấn đề:** "Email chưa được xác thực"
- **Giải pháp:** Kiểm tra email, click link xác thực

#### Lỗi Upload Avatar
- **Vấn đề:** "Upload failed"
- **Giải pháp:** 
  - Kiểm tra kết nối internet
  - Kiểm tra ImgBB API key
  - Kiểm tra kích thước ảnh (< 32MB)

#### Lỗi Date Picker không hiển thị
- **Vấn đề:** Click vào Date of Birth không có gì xảy ra
- **Giải pháp:**
  - Kiểm tra `inputDob.setClickable(true)`
  - Kiểm tra `inputDob.setFocusable(false)`
  - Kiểm tra `setupDatePicker()` đã được gọi

### B. API Keys

**ImgBB API:**
- Đăng ký: https://api.imgbb.com/
- Tạo API key
- Thêm vào: `res/values/strings.xml`

### C. Contact & Support

- **Developer:** [Tên của bạn]
- **Email:** [Email của bạn]
- **GitHub:** [GitHub repository]

---

**© 2025 App Movie Booking Ticket. All rights reserved.**
