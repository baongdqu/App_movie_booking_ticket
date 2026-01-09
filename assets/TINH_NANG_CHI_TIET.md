# TÍNH NĂNG CHI TIẾT ỨNG DỤNG ĐẶT VÉ XEM PHIM

Tài liệu này mô tả chi tiết từng tính năng của ứng dụng, bao gồm giới thiệu tổng thể, các thành phần bên trong và **luồng hoạt động**.

---

## 1. MÀN HÌNH LOADING (Màn hình chờ)

### 📂 File nguồn: `activities_0_loading.java`

### 📋 Giới thiệu tổng thể
Màn hình đầu tiên xuất hiện khi mở ứng dụng. Thực hiện các tác vụ khởi tạo quan trọng như kiểm tra kết nối mạng, kiểm tra trạng thái đăng nhập và mã PIN, đồng thời hiển thị animation logo thu hút.

### 🔧 Các tính năng bên trong

#### 1.1. Chức năng mở giao diện Loading
- `setContentView(R.layout.layouts_0_loading)`: Đoạn code để mở giao diện Loading

#### 1.2. Chức năng kiểm tra Internet

| Thành phần                  | Mô tả                                                                      |
| --------------------------- | -------------------------------------------------------------------------- |
| `ConnectivityManager`       | Thư viện kiểm tra kết nối vật lý internet                                  |
| `isNetworkAvailable()`      | Hàm kiểm tra mạng có kết nối không, kiểm tra WiFi/4G/Ethernet              |
| `hasActualInternetAccess()` | Hàm chứa các trang web máy chủ để thử kết nối internet thực sự             |
| `canReachUrl()`             | Hàm thử kết nối với các máy chủ Google, Cloudflare để xác nhận có internet |
| `startInternetCheck()`      | Hàm tổng để bắt đầu kiểm tra internet trên luồng song song                 |
| `ExecutorService`           | Thư viện để mở và chạy tác vụ trên luồng song song                         |

**🔄 Luồng hoạt động kiểm tra Internet:**
```
onCreate() 
    → **startInternetCheck()** [chạy trên luồng song song ExecutorService]     ← [1.2]
        → **hasActualInternetAccess()**                                         ← [1.2]
            → **isNetworkAvailable()** [kiểm tra phần cứng WiFi/4G/Ethernet]    ← [1.2]
                ✓ Có kết nối vật lý → tiếp tục
                ✗ Không có → return false
            → **canReachUrl**("https://clients3.google.com/generate_204")       ← [1.2]
            → **canReachUrl**("https://connectivitycheck.gstatic.com/generate_204")
            → **canReachUrl**("https://www.google.com")
            → **canReachUrl**("https://www.cloudflare.com")
                ✓ Một trong các URL phản hồi → return true
                ✗ Tất cả thất bại → return false
        → hasInternetResult.set(kết quả)
        → internetCheckCompleted.set(true)
        → mainHandler.post(tryProceed) [quay lại UI thread]
```

#### 1.3. Chức năng kiểm tra trạng thái xác thực

| Thành phần               | Mô tả                                              |
| ------------------------ | -------------------------------------------------- |
| `proceedToNextScreen()`  | Hàm quyết định chuyển tới màn hình Login hoặc Menu |
| `mAuth.getCurrentUser()` | Lấy người dùng hiện tại từ Firebase Auth           |
| `isEmailVerified()`      | Kiểm tra email đã được xác thực chưa               |

#### 1.4. Chức năng kiểm tra mã PIN

| Thành phần                               | Mô tả                                |
| ---------------------------------------- | ------------------------------------ |
| `SharedPreferences`                      | Lưu trữ cục bộ trên thiết bị Android |
| `prefs.getBoolean("pin_enabled", false)` | Kiểm tra trạng thái bật/tắt mã PIN   |
| `activities_2_a_lock_screen`             | Màn hình khóa ứng dụng               |

**🔄 Luồng hoạt động kiểm tra xác thực và PIN:**
```
tryProceed() [được gọi khi cả loading và kiểm tra mạng hoàn tất]
    → **proceedToNextScreen**(noInternet)                                       ← [1.3]
        → **mAuth.getCurrentUser()**                                            ← [1.3]
            ✓ currentUser != null && **isEmailVerified()** = true               ← [1.3]
                → Kiểm tra PIN: **prefs.getBoolean("pin_enabled")**             ← [1.4]
                    ✓ PIN bật → startActivity(**Lock Screen**)                  ← [1.4]
                    ✗ PIN tắt → startActivity(Menu) trực tiếp
            ✗ currentUser == null hoặc chưa verify email
                → mAuth.signOut() [nếu có user nhưng chưa verify]
                → startActivity(Login)
        → finish() [đóng Loading screen]
```

#### 1.5. Chức năng phát âm thanh
- `extra_sound_manager.playOpening()`: Phát âm thanh opening từ lớp `extra_sound_manager.java`

#### 1.6. Chức năng Preload dữ liệu phim
- `MovieCacheManager.getInstance().preloadData()`: Tải trước dữ liệu phim song song

**🔄 Luồng hoạt động tổng thể màn hình Loading:**
```
[App khởi động]
    ↓
onCreate()
    → setContentView(layouts_0_loading) [hiển thị UI]                           ← [1.1]
    → executorService = Executors.newSingleThreadExecutor()                     ← [1.2]
    → **startInternetCheck()** [SONG SONG - kiểm tra mạng]                      ← [1.2]
    → Handler.postDelayed(500ms) → **playOpening()** [phát âm thanh]            ← [1.5]
    → mAuth = FirebaseAuth.getInstance() [khởi tạo Firebase]                    ← [1.3]
    → **MovieCacheManager.preloadData()** [SONG SONG - tải trước phim]          ← [1.6]
    → imgLogo.startAnimation(scale_fade_in) [animation logo]
    → Handler.postDelayed(5000ms) → loadingCompleted.set(true) → tryProceed()
    ↓
[Chờ 5 giây + kiểm tra mạng xong]
    ↓
tryProceed() → **proceedToNextScreen()**                                        ← [1.3]
    ↓
[Chuyển sang Login/Lock Screen/Menu]
```

---

## 2. MÀN HÌNH ĐĂNG NHẬP (Login)

### 📂 File nguồn: `activities_1_login.java`

### 📋 Giới thiệu tổng thể
Màn hình xác thực người dùng qua Email/Password hoặc Google Sign-In. Hỗ trợ gửi lại email xác minh, quên mật khẩu và hiển thị cảnh báo mạng.

### 🔧 Các tính năng bên trong

#### 2.1. Chức năng đăng nhập bằng Email/Password

| Thành phần                           | Mô tả                         |
| ------------------------------------ | ----------------------------- |
| `loginUser()`                        | Hàm xử lý logic đăng nhập     |
| `mAuth.signInWithEmailAndPassword()` | Gọi Firebase Auth để sign in  |
| `TextInputEditText`                  | Ô nhập liệu Email và Password |

**🔄 Luồng hoạt động đăng nhập Email/Password:**
```
btnLogin.onClick()
    → playUiClick() [phát âm thanh click]
    → **loginUser()**                                                            ← [2.1]
        → Lấy email, password từ **TextInputEditText**                           ← [2.1]
        → Validate: TextUtils.isEmpty()?
            ✗ Rỗng → Toast("Vui lòng điền đầy đủ") → return
            ✓ Có dữ liệu → tiếp tục
        → **mAuth.signInWithEmailAndPassword**(email, password)                  ← [2.1]
            → onComplete(task)
                ✓ task.isSuccessful()
                    → mAuth.getCurrentUser()
                    → user.isEmailVerified()?
                        ✓ Đã verify
                            → playSuccess()
                            → Lưu SharedPreferences(email, username, uid)
                            → startActivity(Menu)
                            → finish()
                        ✗ Chưa verify
                            → Hiển thị MaterialAlertDialog
                                [Gửi lại email] → **user.sendEmailVerification()** ← [2.3]
                                [Mở ứng dụng email] → Intent(ACTION_MAIN)
                                [Đóng] → mAuth.signOut()
                ✗ task.isFailure()
                    → playError()
                    → Toast("Đăng nhập thất bại: " + exception.getMessage())
```

#### 2.2. Chức năng đăng nhập bằng Google Sign-In

| Thành phần                     | Mô tả                               |
| ------------------------------ | ----------------------------------- |
| `extra_google_signin_helper`   | Helper class xử lý Google Sign-In   |
| `ActivityResultLauncher`       | Launcher cho kết quả Google Sign-In |
| `handleSignInResultForLogin()` | Xử lý kết quả đăng nhập Google      |
| `onLoginSuccess()`             | Callback khi đăng nhập thành công   |
| `onNeedRegistration()`         | Callback khi cần đăng ký mới        |

**🔄 Luồng hoạt động đăng nhập Google:**
```
btnGoogleSignIn.onClick()
    → playUiClick()
    → **googleSignInHelper**.revokeAccessAndSignIn(**launcher**)                 ← [2.2]
        → [Hiển thị danh sách tài khoản Google]
        → [User chọn tài khoản]
    ↓
**googleSignInLauncher**.onActivityResult(result)                                ← [2.2]
    → result.getResultCode() == RESULT_OK?
        ✓ Thành công
            → **handleSignInResultForLogin**(data, callback)                     ← [2.2]
                → [Kiểm tra email đã đăng ký trong hệ thống chưa]
                    ✓ Đã có → **onLoginSuccess**(user)                           ← [2.2]
                        → playSuccess()
                        → Lưu SharedPreferences
                        → startActivity(Menu)
                        → finish()
                    ✗ Chưa có → **onNeedRegistration**(email, name, photoUrl)    ← [2.2]
                        → Toast("Email chưa được đăng ký")
                        → startActivity(Signup) với data từ Google
        ✗ Thất bại/Hủy
            → onError() hoặc onCancelled()
            → playError()
            → Toast thông báo lỗi
```

#### 2.3. Chức năng gửi lại email xác minh
- `user.sendEmailVerification()`: Gửi email xác minh từ Firebase

#### 2.4. Chức năng hiển thị cảnh báo không có mạng
- `checkNoInternetFromLoading()`: Kiểm tra kết quả mạng từ Loading
- `showNoInternetDialog()`: Hiển thị dialog cảnh báo

---

## 3. MÀN HÌNH TRANG CHỦ (Home)

### 📂 File nguồn: `fragments_home.java`

### 📋 Giới thiệu tổng thể
Màn hình chính của ứng dụng, hiển thị banner quảng cáo, danh sách phim đang chiếu, sắp chiếu và thịnh hành. Cung cấp chức năng tìm kiếm phim theo tên.

### 🔧 Các tính năng bên trong

#### 3.1. Chức năng Banner Slider tự động

| Thành phần                    | Mô tả                              |
| ----------------------------- | ---------------------------------- |
| `ViewPager2`                  | Component hiển thị slide banner    |
| `SliderAdapter`               | Adapter cho banner slider          |
| `initBanner()`                | Hàm khởi tạo banner từ Firebase    |
| `setupBanners()`              | Hàm cấu hình hiệu ứng chuyển trang |
| `sliderHandler.postDelayed()` | Tự động chuyển trang mỗi 3 giây    |

**🔄 Luồng hoạt động Banner Slider:**
```
onViewCreated()
    → **initBanner()**                                                           ← [3.1]
        → progressBarSlider.setVisibility(VISIBLE) [hiện loading]
        → database.getReference("Banners").addListenerForSingleValueEvent()
            → onDataChange(snapshot)
                → Duyệt snapshot.getChildren()
                    → SliderItems item = getValue(SliderItems.class)
                    → lists.add(item)
                → progressBarSlider.setVisibility(GONE) [ẩn loading]
                → **setupBanners**(lists)                                        ← [3.1]
                    → **viewPager2**.setAdapter(new **SliderAdapter**(lists))    ← [3.1]
                    → Cấu hình CompositePageTransformer (hiệu ứng scale)
                    → viewPager2.setCurrentItem(giữa danh sách) [infinite scroll]
                    → registerOnPageChangeCallback()
                        → onPageSelected(position)
                            → sliderHandler.removeCallbacks(sliderRunnable)
                            → **sliderHandler.postDelayed**(sliderRunnable, 3000) ← [3.1]
                                → [Sau 3s] viewPager2.setCurrentItem(current + 1)
```

#### 3.2. Chức năng hiển thị danh sách phim

| Thành phần              | Mô tả                                 |
| ----------------------- | ------------------------------------- |
| `loadMoviesFromCache()` | Tải phim từ cache đã preload          |
| `TopMovieAdapter`       | Adapter hiển thị danh sách phim ngang |
| `RecyclerView`          | Component hiển thị danh sách          |
| `MovieCacheManager`     | Quản lý cache dữ liệu phim            |
| `nowShowingAdapter`     | Adapter phim đang chiếu               |
| `upcomingAdapter`       | Adapter phim sắp chiếu                |
| `topMovieAdapter`       | Adapter phim thịnh hành               |

**🔄 Luồng hoạt động tải danh sách phim:**
```
onViewCreated()
    → **movieCacheManager** = **MovieCacheManager**.getInstance()                ← [3.2]
    → setupRecyclerViews()
        → Khởi tạo **TopMovieAdapter** cho mỗi loại phim                         ← [3.2]
        → Gán LayoutManager (HORIZONTAL) cho **RecyclerView**                    ← [3.2]
        → Gán Adapter cho RecyclerView
    → **loadMoviesFromCache()**                                                  ← [3.2]
        → movieCacheManager.getFilteredMovies(callback)
            → callback(nowShowing, upcoming, trending, allMovies)
                → nowShowingMoviesList.clear()
                → nowShowingMoviesList.addAll(nowShowing)
                → **nowShowingAdapter**.notifyDataSetChanged()                   ← [3.2]
                
                → upcomingMoviesList.clear()
                → upcomingMoviesList.addAll(upcoming)
                → **upcomingAdapter**.notifyDataSetChanged()                     ← [3.2]
                
                → movieListTop.clear()
                → movieListTop.addAll(trending)
                → **topMovieAdapter**.notifyDataSetChanged()                     ← [3.2]
                
                → allMoviesList.addAll(allMovies)
                → Collections.shuffle(allMoviesList)
                → allMoviesAdapter.notifyDataSetChanged()
```

#### 3.3. Chức năng tìm kiếm phim

| Thành phần       | Mô tả                                           |
| ---------------- | ----------------------------------------------- |
| `filterMovies()` | Hàm lọc phim theo từ khóa                       |
| `TextWatcher`    | Lắng nghe sự thay đổi text để tìm kiếm realtime |
| `searchAdapter`  | Adapter hiển thị kết quả tìm kiếm               |

**🔄 Luồng hoạt động tìm kiếm phim:**
```
btnSearch.onClick()
    → playUiClick()
    → searchBox.getVisibility() == GONE?
        ✓ Đang ẩn → Hiển thị searchBox với animation fadeIn
        ✗ Đang hiện → Ẩn searchBox với animation fadeOut
            → inputSearch.setText("") [xóa text]
            → Reset UI về trạng thái ban đầu
    ↓
inputSearch.**TextWatcher**.onTextChanged(keyword)                               ← [3.3]
    → **filterMovies**(keyword)                                                  ← [3.3]
        → keyword rỗng?
            ✓ Rỗng → Hiện lại các RecyclerView gốc, ẩn search results
            ✗ Có keyword
                → Duyệt allMoviesList
                    → movie.getTitle().toLowerCase().contains(keyword)?
                        ✓ Khớp → filteredList.add(movie)
                → filteredList.isEmpty()?
                    ✓ Không có kết quả → tvNoResults.setVisibility(VISIBLE)
                    ✗ Có kết quả
                        → Ẩn các RecyclerView gốc
                        → recyclerSearchResults.setVisibility(VISIBLE)
                        → **searchAdapter**.updateList(filteredList)             ← [3.3]
```

#### 3.4. Chức năng hiển thị thông tin người dùng
- `loadUserInfo()`: Load thông tin từ Firebase
- `binding.tvFullName`, `binding.tvBalance`, `binding.imgAvatar`: Hiển thị tên, số dư, avatar

---

## 4. MÀN HÌNH CHỌN GHẾ (Seat Selection)

### 📂 File nguồn: `parthome_SeatSelectionActivity.java`

### 📋 Giới thiệu tổng thể
Màn hình cho phép người dùng chọn ngày, giờ chiếu và vị trí ghế ngồi. Sơ đồ ghế 8 cột với các trạng thái: trống, đang chọn, đã đặt.

### 🔧 Các tính năng bên trong

#### 4.1. Chức năng load danh sách ngày chiếu

| Thành phần             | Mô tả                                |
| ---------------------- | ------------------------------------ |
| `loadAvailableDates()` | Lấy danh sách ngày chiếu từ Firebase |
| `layoutDates`          | LinearLayout chứa các nút chọn ngày  |
| `selectedDate`         | Biến lưu ngày đã chọn                |

#### 4.2. Chức năng load giờ chiếu

| Thành phần               | Mô tả                              |
| ------------------------ | ---------------------------------- |
| `loadShowtimesForDate()` | Load giờ chiếu theo ngày           |
| `layoutTimes`            | LinearLayout chứa các nút chọn giờ |
| `selectedShowtime`       | Biến lưu giờ đã chọn               |

#### 4.3. Chức năng load sơ đồ ghế

| Thành phần       | Mô tả                          |
| ---------------- | ------------------------------ |
| `loadSeats()`    | Load ghế của ngày + giờ cụ thể |
| `GridLayout`     | Hiển thị sơ đồ ghế 8 cột       |
| `displaySeats()` | Hiển thị các ghế lên giao diện |

#### 4.4. Chức năng chọn/bỏ chọn ghế và tính tiền

| Thành phần      | Mô tả                       |
| --------------- | --------------------------- |
| `toggleSeat()`  | Chọn/bỏ chọn ghế            |
| `selectedSeats` | Danh sách ghế đã chọn       |
| `pricePerSeat`  | Giá mỗi ghế                 |
| `tvTotalPrice`  | Hiển thị tổng tiền realtime |

**🔄 Luồng hoạt động tổng thể chọn ghế:**
```
onCreate()
    → Nhận Intent data (movieTitle, posterUrl, movieID)
    → fromCinemaSelection?
        ✓ Từ Cinema Selection → loadSeatsFromCinema() [đã có ngày/giờ]
        ✗ Từ Movie Detail → **loadAvailableDates()**                             ← [4.1]
    ↓
**loadAvailableDates()**                                                         ← [4.1]
    → dbRef.addListenerForSingleValueEvent()
        → Duyệt snapshot.getChildren()
            → key = "2025-11-08_15:15"
            → date = key.split("_")[0] → uniqueDates.add(date)
        → Tạo Button cho mỗi ngày trong **layoutDates**                          ← [4.1]
            → btnDate.onClick()
                → Reset các nút ngày khác (setSelected = false)
                → btnDate.setSelected(true)
                → **selectedDate** = date                                        ← [4.1]
                → Reset **layoutTimes**, gridSeats, **selectedSeats**            ← [4.2, 4.4]
                → **loadShowtimesForDate**(date)                                 ← [4.2]
    ↓
**loadShowtimesForDate**(date)                                                   ← [4.2]
    → Duyệt snapshot tìm key bắt đầu bằng date
        → time = key.split("_")[1]
        → Tạo Button cho mỗi giờ trong **layoutTimes**                           ← [4.2]
            → btnTime.onClick()
                → Reset các nút giờ khác
                → btnTime.setSelected(true)
                → **selectedShowtime** = time                                    ← [4.2]
                → Reset gridSeats, **selectedSeats**                             ← [4.4]
                → **loadSeats**(date, time)                                      ← [4.3]
    ↓
**loadSeats**(date, time)                                                        ← [4.3]
    → seatRef = dbRef.child(date + "_" + time)
    → seatRef.addListenerForSingleValueEvent()
        → **pricePerSeat** = snapshot.child("pricePerSeat").getValue()           ← [4.4]
        → Duyệt snapshot.child("seats").getChildren()
            → seatName = seat.getKey() (vd: "A1", "B2")
            → status = seat.getValue() ("available" hoặc "booked")
            → Tạo Button seatBtn
                → status == "booked"?
                    ✓ → seatBtn.setEnabled(false) [màu xám]
                    ✗ → seatBtn.setOnClickListener → **toggleSeat()**            ← [4.4]
            → **GridLayout**.addView(seatBtn)                                    ← [4.3]
    ↓
**toggleSeat**(seatBtn, seatName)                                                ← [4.4]
    → **selectedSeats**.contains(seatName)?                                      ← [4.4]
        ✓ Đã chọn → selectedSeats.remove() + seatBtn.setSelected(false)
        ✗ Chưa chọn → selectedSeats.add() + seatBtn.setSelected(true)
    → **tvTotalPrice**.setText(selectedSeats.size() * **pricePerSeat**)          ← [4.4]
    ↓
btnContinue.onClick()
    → **selectedSeats**.isEmpty()?                                               ← [4.4]
        ✓ → Toast("Vui lòng chọn ghế")
        ✗ → startActivity(PaymentActivity) với data (movieTitle, date, time, seats, totalPrice)
```

---

## 5. MÀN HÌNH CHATBOT AI

### 📂 File nguồn: `activities_2_chatbot.java`

### 📋 Giới thiệu tổng thể
Trợ lý ảo thông minh tích hợp mô hình ngôn ngữ lớn (LLM) qua REST API. Hỗ trợ tư vấn phim dựa trên sở thích cá nhân với giao diện chat hiện đại.

### 🔧 Các tính năng bên trong

#### 5.1. Chức năng khởi tạo và kết nối AI Server

| Thành phần                | Mô tả                                  |
| ------------------------- | -------------------------------------- |
| `extra_gemini_cli_helper` | Helper class giao tiếp với AI Server   |
| `initGeminiHelper()`      | Khởi tạo helper                        |
| `checkHealth()`           | Kiểm tra kết nối server                |
| `ngrok`                   | Tunnel expose server local ra Internet |

#### 5.2. Chức năng gửi tin nhắn với context người dùng

| Thành phần              | Mô tả                      |
| ----------------------- | -------------------------- |
| `sendMessage()`         | Gửi tin nhắn từ input      |
| `callGeminiAPI()`       | Gửi tin nhắn đến AI Server |
| `userFavoriteGenre`     | Thể loại phim yêu thích    |
| `userFavoriteLanguage`  | Ngôn ngữ phim yêu thích    |
| `loadUserPreferences()` | Load sở thích từ Firebase  |
| 451:                    | **Privacy Note**           | **Metadata gửi đi**: Email, Thể loại yêu thích, Ngôn ngữ phim, Tùy chọn Sub/Dub |

#### 5.3. Chức năng Quick Replies

| Thành phần         | Mô tả              |
| ------------------ | ------------------ |
| `chipSuggestMovie` | Nút gợi ý phim     |
| `chipBookTicket`   | Nút đặt vé         |
| `chipShowtime`     | Nút hỏi lịch chiếu |
| `chipTicketPrice`  | Nút hỏi giá vé     |
| `sendQuickReply()` | Gửi tin nhắn nhanh |

#### 5.4. Chức năng hiển thị UI chat

| Thành phần              | Mô tả                              |
| ----------------------- | ---------------------------------- |
| `ChatMessageAdapter`    | Adapter hiển thị tin nhắn          |
| `showTypingIndicator()` | Hiển thị trạng thái "Đang nhập..." |
| `setInputEnabled()`     | Bật/tắt input khi chờ phản hồi     |

**🔄 Luồng hoạt động gửi tin nhắn Chatbot:**
```
onCreate()
    → initViews()
    → setupRecyclerView() [LayoutManager stackFromEnd = true]
    → setupListeners()
    → **initGeminiHelper()**                                                     ← [5.1]
        → geminiHelper = new **extra_gemini_cli_helper**(this)                   ← [5.1]
        → geminiHelper.**checkHealth**(callback)                                 ← [5.1]
            ✓ Server OK → [có thể hiển thị indicator "Online"]
            ✗ Server lỗi → Toast("Server chưa sẵn sàng")
    → **loadUserPreferences()**                                                  ← [5.2]
        → Firebase.getReference("users/{uid}/moviePreferences")
            → Lưu **userFavoriteGenre**, **userFavoriteLanguage**                ← [5.2]
    → showWelcomeMessage()
        → adapter.addMessage(welcomeMessage, TYPE_BOT)
    ↓
fabSendMessage.onClick() hoặc Enter
    → **sendMessage()**                                                          ← [5.2]
        → message = inputChatMessage.getText()
        → message.isEmpty()? → return
        → adapter.addMessage(message, TYPE_USER) [hiển thị tin user]
        → inputChatMessage.setText("") [xóa input]
        → **callGeminiAPI**(message)                                             ← [5.2]
    ↓
**callGeminiAPI**(message)                                                       ← [5.2]
    → **showTypingIndicator**(true) [hiện "Bot đang nhập..."]                    ← [5.4]
    → **setInputEnabled**(false) [disable input]                                 ← [5.4]
    → Xây dựng context:
        → contextBuilder.append("User Email: " + user.getEmail())
        → contextBuilder.append("Thể loại yêu thích: " + **userFavoriteGenre**)  ← [5.2]
        → contextBuilder.append("Ngôn ngữ phim: " + **userFavoriteLanguage**)    ← [5.2]
        → messageToSend = context + "\nCâu hỏi: " + message
    → geminiHelper.sendMessage(messageToSend, history, callback)
        → [REST API call đến AI Server qua **ngrok**]                            ← [5.1]
        ↓
        onSuccess(response)
            → **showTypingIndicator**(false)                                     ← [5.4]
            → **setInputEnabled**(true)                                          ← [5.4]
            → **ChatMessageAdapter**.addMessage(response, TYPE_BOT)              ← [5.4]
        ↓
        onError(error)
            → **showTypingIndicator**(false)                                     ← [5.4]
            → **setInputEnabled**(true)                                          ← [5.4]
            → adapter.addMessage("Lỗi: " + error, TYPE_BOT)
            → Toast(error)
```

**🔄 Luồng hoạt động Quick Replies:**
```
**chipSuggestMovie**.onClick()                                                   ← [5.3]
    → **sendQuickReply**("Gợi ý phim cho tôi")                                   ← [5.3]
        → sendMessage() với tin nhắn quick reply
        
**chipBookTicket**.onClick()                                                     ← [5.3]
    → **sendQuickReply**("Hướng dẫn đặt vé")                                     ← [5.3]

**chipShowtime**.onClick()                                                       ← [5.3]
    → **sendQuickReply**("Lịch chiếu hôm nay")                                   ← [5.3]

**chipTicketPrice**.onClick()                                                    ← [5.3]
    → **sendQuickReply**("Giá vé bao nhiêu?")                                    ← [5.3]
```

---

## 6. MÀN HÌNH TÌM RẠP GẦN ĐÂY (Cinema Finder)

### 📂 File nguồn: `fragments_cinema.java`

### 📋 Giới thiệu tổng thể
Tính năng tìm kiếm rạp phim gần vị trí hiện tại dựa trên GPS. Tính toán khoảng cách thời gian thực và hiển thị trạng thái mở/đóng cửa.

### 🔧 Các tính năng bên trong

#### 6.1. Chức năng định vị GPS

| Thành phần                    | Mô tả                                       |
| ----------------------------- | ------------------------------------------- |
| `FusedLocationProviderClient` | Client lấy vị trí từ Google                 |
| `getCurrentLocation()`        | Lấy vị trí hiện tại                         |
| `getLastKnownLocation()`      | Lấy vị trí đã biết gần nhất                 |
| `useDefaultLocation()`        | Dùng vị trí mặc định (UIT) nếu không có GPS |
| `Geocoder`                    | Chuyển tọa độ thành địa chỉ                 |
| `getAddressFromLocation()`    | Lấy địa chỉ từ tọa độ                       |

#### 6.2. Chức năng quyền truy cập vị trí

| Thành phần                    | Mô tả                     |
| ----------------------------- | ------------------------- |
| `locationPermissionLauncher`  | Launcher yêu cầu quyền    |
| `hasLocationPermission()`     | Kiểm tra đã có quyền chưa |
| `requestLocationPermission()` | Yêu cầu quyền vị trí      |

#### 6.3. Chức năng load và sắp xếp rạp

| Thành phần                  | Mô tả                                  |
| --------------------------- | -------------------------------------- |
| `loadCinemasFromFirebase()` | Load danh sách rạp từ Firebase         |
| `calculateDistance()`       | Tính khoảng cách từ người dùng đến rạp |
| `Collections.sort()`        | Sắp xếp rạp theo khoảng cách gần nhất  |
| `CinemaAdapter`             | Adapter hiển thị danh sách rạp         |

#### 6.4. Chức năng kiểm tra giờ mở cửa

| Thành phần       | Mô tả                                        |
| ---------------- | -------------------------------------------- |
| `isCinemaOpen()` | Kiểm tra rạp đang mở hay đóng                |
| `workingHours`   | Chuỗi giờ làm việc (format: "09:00 - 23:30") |

**🔄 Luồng hoạt động tìm rạp gần đây:**
```
onViewCreated()
    → initViews()
    → cinemasRef = FirebaseDatabase.getReference("Cinemas")
    → **fusedLocationClient** = LocationServices.getFusedLocationProviderClient()    ← [6.1]
    → setupRecyclerView()
    → setupSwipeRefresh()
    → checkPermissionAndGetLocation()
    ↓
checkPermissionAndGetLocation()
    → showLocationLoading() [hiện progress, text "Đang xác định vị trí..."]
    → **hasLocationPermission()**?                                                   ← [6.2]
        ✓ Có quyền → **getCurrentLocation()**                                        ← [6.1]
        ✗ Chưa có → **requestLocationPermission()**                                  ← [6.2]
            → **locationPermissionLauncher**.launch([FINE, COARSE])                  ← [6.2]
                → onResult
                    ✓ Granted → **getCurrentLocation()**                             ← [6.1]
                    ✗ Denied → **useDefaultLocation()**                              ← [6.1]
    ↓
**getCurrentLocation()**                                                             ← [6.1]
    → **fusedLocationClient**.getCurrentLocation(PRIORITY_BALANCED)                  ← [6.1]
        → onSuccessListener(location)
            → location != null?
                ✓ Có vị trí
                    → userLatitude = location.getLatitude()
                    → userLongitude = location.getLongitude()
                    → hasRealLocation = true
                    → updateLocationDisplay(location)
                        → **Geocoder**.getFromLocation() [lấy địa chỉ]               ← [6.1]
                        → tvCurrentLocation.setText(address)
                        → tvCoordinates.setText(lat, lng)
                    → **loadCinemasFromFirebase()**                                  ← [6.3]
                ✗ null → **getLastKnownLocation()**                                  ← [6.1]
                    → Cũng null → **useDefaultLocation()**                           ← [6.1]
    ↓
**loadCinemasFromFirebase()**                                                        ← [6.3]
    → showLoading()
    → cinemasRef.addListenerForSingleValueEvent()
        → Duyệt snapshot.getChildren()
            → cinema = new Cinema()
            → Gán name, address, latitude, longitude, rating, phone, screens, **workingHours**, amenities
            → cinema.**calculateDistance**(userLatitude, userLongitude) [Haversine]  ← [6.3]
            → cinema.setOpenNow(**isCinemaOpen**(**workingHours**))                  ← [6.4]
            → cinemaList.add(cinema)
        → **Collections.sort**(cinemaList, by distance ASC) [gần nhất trước]         ← [6.3]
        → cinemaList.subList(0, 7) [giới hạn 7 rạp]
        → showCinemas(cinemaList)
            → **cinemaAdapter**.setCinemaList(cinemas)                               ← [6.3]
            → tvCinemaCount.setText("hiện đang có X rạp gần bạn")
            → tvLastUpdated.setText("Cập nhật lúc HH:mm")
```

---

## 7. CÀI ĐẶT NÂNG CAO (Advanced Settings)

### 📂 File nguồn: `partuser_advanced_settings.java`

### 📋 Giới thiệu tổng thể
Màn hình quản lý các thiết lập ứng dụng bao gồm: Dark Mode, thông báo, âm thanh, khóa PIN, đổi ngôn ngữ và xóa tài khoản.

### 🔧 Các tính năng bên trong

#### 7.1. Chức năng khóa mã PIN

| Thành phần                         | Mô tả                                  |
| ---------------------------------- | -------------------------------------- |
| `switchPinLock`                    | Switch bật/tắt khóa PIN                |
| `showSetPinDialog()`               | Dialog đặt PIN mới                     |
| `showConfirmPinDialogForSetup()`   | Dialog xác nhận PIN                    |
| `showChangePinDialog()`            | Dialog đổi PIN                         |
| `showConfirmPinDialogForDisable()` | Dialog xác nhận tắt PIN                |
| `updatePinUI()`                    | Cập nhật giao diện theo trạng thái PIN |

**🔄 Luồng hoạt động bật/tắt PIN:**
```
**switchPinLock**.onClick()                                                      ← [7.1]
    → playToggle()
    → isChecked = switchPinLock.isChecked()
    → switchPinLock.setChecked(!isChecked) [revert tạm thời]
    → isChecked?
        ✓ Muốn BẬT PIN → **showSetPinDialog()**                                  ← [7.1]
            → Dialog nhập PIN 6 số
            → [OK] → pin.length() == 6?
                ✓ → **showConfirmPinDialogForSetup**(pin)                        ← [7.1]
                    → Dialog nhập lại PIN
                    → [Lưu] → confirmPin == firstPin?
                        ✓ Khớp
                            → prefs.putBoolean("pin_enabled", true)
                            → prefs.putString("app_pin", pin)
                            → playSuccess()
                            → Toast("Đặt mã PIN thành công")
                            → **updatePinUI()**                                  ← [7.1]
                        ✗ Không khớp
                            → playError()
                            → Toast("Mã PIN không khớp")
                            → **updatePinUI()** [revert]                         ← [7.1]
                ✗ → Toast("PIN phải đủ 6 số")
        ✗ Muốn TẮT PIN → **showConfirmPinDialogForDisable()**                    ← [7.1]
            → Dialog nhập PIN hiện tại
            → [Xóa] → enteredPin == storedPin?
                ✓ Đúng
                    → prefs.putBoolean("pin_enabled", false)
                    → prefs.remove("app_pin")
                    → playSuccess()
                    → Toast("Đã xóa mã PIN")
                    → **updatePinUI()**                                          ← [7.1]
                ✗ Sai
                    → playError()
                    → Toast("Mã PIN sai")
                    → **updatePinUI()** [revert]                                 ← [7.1]
```

#### 7.2. Chức năng đổi ngôn ngữ

| Thành phần                            | Mô tả                            |
| ------------------------------------- | -------------------------------- |
| `btnChangeLanguage`                   | Nút mở dialog chọn ngôn ngữ      |
| `extra_language_helper.setLocale()`   | Đặt ngôn ngữ mới                 |
| `extra_language_helper.getLanguage()` | Lấy ngôn ngữ hiện tại            |
| 6 ngôn ngữ                            | Việt, Anh, Nga, Nhật, Hàn, Trung |

**🔄 Luồng hoạt động đổi ngôn ngữ:**
```
**btnChangeLanguage**.onClick()                                                  ← [7.2]
    → playUiClick()
    → currentLang = **extra_language_helper.getLanguage()**                      ← [7.2]
    → MaterialAlertDialogBuilder.setSingleChoiceItems([**6 ngôn ngữ**], checkedItem) ← [7.2]
        → onItemSelected(which)
            → selectedLang = getLanguageCode(which) // "vi", "en", "ru", "ja", "ko", "zh"
            → currentLang != selectedLang?
                ✓ Khác
                    → **extra_language_helper.setLocale**(this, selectedLang)    ← [7.2]
                    → dialog.dismiss()
                    → Lấy Intent khởi động lại app
                    → i.addFlags(CLEAR_TOP | NEW_TASK)
                    → startActivity(i)
                    → finish()
                ✗ Giống → dialog.dismiss()
```

#### 7.3. Chức năng Dark Mode và âm thanh

| Thành phần                       | Mô tả                    |
| -------------------------------- | ------------------------ |
| `switchDarkMode`                 | Switch bật/tắt Dark Mode |
| `extra_themeutils.setDarkMode()` | Đặt chế độ tối           |
| `switchSound`                    | Switch bật/tắt âm thanh  |
| `switchNotification`             | Switch bật/tắt thông báo |

**🔄 Luồng hoạt động Dark Mode:**
```
**switchDarkMode**.onClick()                                                     ← [7.3]
    → playToggle()
    → isChecked = switchDarkMode.isChecked()
    → **extra_themeutils.setDarkMode**(this, isChecked)                          ← [7.3]
        → prefs.putBoolean("dark_mode", isChecked)
        → AppCompatDelegate.setDefaultNightMode(isChecked ? MODE_NIGHT_YES : MODE_NIGHT_NO)
        → [Activity recreate để áp dụng theme mới]
```

#### 7.4. Chức năng xóa tài khoản

| Thành phần                          | Mô tả                       |
| ----------------------------------- | --------------------------- |
| `btnDeleteAccount`                  | Nút xóa tài khoản           |
| `dialogConfirmDelete()`             | Dialog xác nhận xóa         |
| `user.reauthenticate()`             | Xác thực lại trước khi xóa  |
| `usersRef.child(uid).removeValue()` | Xóa dữ liệu người dùng      |
| `user.delete()`                     | Xóa tài khoản Firebase Auth |

**🔄 Luồng hoạt động xóa tài khoản:**
```
**btnDeleteAccount**.onClick()                                                   ← [7.4]
    → playUiClick()
    → Vibrate(100ms) [rung cảnh báo]
    → MaterialAlertDialogBuilder (Warning)
        → setTitle("Xác nhận xóa tài khoản")
        → setMessage("Hành động này không thể hoàn tác...")
        → [Xóa] → Dialog nhập mật khẩu
            → **dialogConfirmDelete**(user, password)                            ← [7.4]
                → password.isEmpty()? → Toast("Nhập mật khẩu") → return
                → credential = EmailAuthProvider.getCredential(email, password)
                → **user.reauthenticate**(credential)                            ← [7.4]
                    → onSuccess
                        → **usersRef.child(uid).removeValue()** [xóa data]       ← [7.4]
                            → onSuccess
                                → **user.delete()** [xóa tài khoản Auth]         ← [7.4]
                                    → onSuccess
                                        → playSuccess()
                                        → Clear SharedPreferences("UserPrefs")
                                        → Clear SharedPreferences("AppSettings")
                                        → mAuth.signOut()
                                        → Toast("Đã xóa tài khoản")
                                        → startActivity(Login) [CLEAR_TASK]
                                        → finish()
                                    → onFailure → Toast(error)
                            → onFailure → Toast(error)
                    → onFailure → Toast("Phiên hết hạn")
        → [Hủy] → Vibrate(80ms) → dismiss()
```

---

## 8. CÁC HELPER CLASSES PHỤ TRỢ

### 8.1. `extra_sound_manager.java` - Quản lý âm thanh

| Hàm                | Mô tả                       |
| ------------------ | --------------------------- |
| `playOpening()`    | Phát âm thanh mở app        |
| `playOpeningApp()` | Phát âm thanh vào app       |
| `playSuccess()`    | Phát âm thanh thành công    |
| `playError()`      | Phát âm thanh lỗi           |
| `playUiClick()`    | Phát âm thanh click         |
| `playToggle()`     | Phát âm thanh toggle switch |

### 8.2. `extra_language_helper.java` - Quản lý ngôn ngữ

| Hàm             | Mô tả                    |
| --------------- | ------------------------ |
| `setLocale()`   | Đặt ngôn ngữ mới cho app |
| `getLanguage()` | Lấy ngôn ngữ hiện tại    |

### 8.3. `extra_themeutils.java` - Quản lý giao diện

| Hàm                 | Mô tả                     |
| ------------------- | ------------------------- |
| `applySavedTheme()` | Áp dụng theme đã lưu      |
| `setDarkMode()`     | Bật/tắt Dark Mode         |
| `isDarkMode()`      | Kiểm tra đang ở Dark Mode |

### 8.4. `extra_firebase_helper.java` - Hỗ trợ Firebase
- Các hàm hỗ trợ truy vấn Firebase Realtime Database

### 8.5. `extra_google_signin_helper.java` - Hỗ trợ Google Sign-In

| Hàm                            | Mô tả                      |
| ------------------------------ | -------------------------- |
| `handleSignInResultForLogin()` | Xử lý kết quả đăng nhập    |
| `revokeAccessAndSignIn()`      | Đăng xuất và đăng nhập lại |

### 8.6. `MovieCacheManager.java` - Quản lý Cache phim

| Hàm                   | Mô tả                  |
| --------------------- | ---------------------- |
| `preloadData()`       | Tải trước dữ liệu phim |
| `getFilteredMovies()` | Lấy phim đã phân loại  |

---

## 📊 TỔNG KẾT CÁC FILE VÀ CHỨC NĂNG

| Màn hình      | File chính                                  | Chức năng chính                  |
| ------------- | ------------------------------------------- | -------------------------------- |
| Loading       | `activities_0_loading.java`                 | Kiểm tra mạng, Auth, PIN         |
| Login         | `activities_1_login.java`                   | Đăng nhập Email/Google           |
| Signup        | `activities_1_signup.java`                  | Đăng ký tài khoản                |
| Quên MK       | `activities_1_forgot_password.java`         | Reset mật khẩu                   |
| Lock Screen   | `activities_2_a_lock_screen.java`           | Nhập PIN mở khóa                 |
| Menu          | `activities_2_a_menu_manage_fragments.java` | Quản lý Fragments                |
| Trang chủ     | `fragments_home.java`                       | Banner, Danh sách phim, Tìm kiếm |
| Rạp phim      | `fragments_cinema.java`                     | GPS, Tìm rạp gần đây             |
| Thông báo     | `fragments_notifications.java`              | Danh sách thông báo              |
| Vé của tôi    | `fragments_mail.java`                       | Lịch sử vé, Hoàn tiền            |
| Cá nhân       | `fragments_user.java`                       | Thông tin cá nhân                |
| Chi tiết phim | `parthome_movie_detail.java`                | Thông tin phim, Trailer          |
| Chọn ghế      | `parthome_SeatSelectionActivity.java`       | Sơ đồ ghế, Chọn suất             |
| Thanh toán    | `parthome_PaymentActivity.java`             | VNPay, Số dư                     |
| Chatbot       | `activities_2_chatbot.java`                 | Tư vấn AI                        |
| Cài đặt       | `partuser_advanced_settings.java`           | PIN, Ngôn ngữ, Theme             |
| Sửa hồ sơ     | `partuser_edit_profile.java`                | Cập nhật thông tin cá nhân       |
| Đổi MK        | `partuser_change_password.java`             | Thay đổi mật khẩu                |
| Sở thích phim | `partuser_movie_preferences.java`           | Thể loại, ngôn ngữ yêu thích     |
