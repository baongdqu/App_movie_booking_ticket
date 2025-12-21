# BÁO CÁO CUỐI KỲ ĐỒ ÁN MÔN HỌC
# ĐỀ TÀI: ỨNG DỤNG ĐẶT VÉ XEM PHIM (MOVIE BOOKING APP)

---

**Môn học:** NT118.Q13 - Phát triển ứng dụng trên thiết bị di động

**Giảng viên hướng dẫn:** ThS. Trần Hồng Nghi

**Thực hiện bởi Nhóm 11:**

| Họ và tên           | MSSV     | Vai trò     |
| ------------------- | -------- | ----------- |
| Nguyễn Trường Duy   | 23520380 | Trưởng nhóm |
| Võ Minh Dương       | 23520358 | Thành viên  |
| Nguyễn Duy Quốc Bảo | 23520119 | Thành viên  |

**Thời gian thực hiện:** 15/11/2025 - 23/12/2025

**Khoa:** Mạng máy tính và Truyền thông

**Trường:** Đại học Công nghệ Thông tin - Đại học Quốc gia Thành phố Hồ Chí Minh (UIT - VNU-HCM)

---

## Mục lục

1. [Lời mở đầu](#lời-mở-đầu)
2. [Lời cảm ơn](#lời-cảm-ơn)
3. [Tóm tắt đồ án môn học](#tóm-tắt-đồ-án-môn-học)
4. [Danh mục hình ảnh](#danh-mục-hình-ảnh)
5. [Danh mục bảng](#danh-mục-bảng)
6. [Danh mục từ viết tắt](#danh-mục-từ-viết-tắt)
7. [Chương 1: Giới thiệu và tổng quan đề tài](#chương-1-giới-thiệu-và-tổng-quan-đề-tài)
8. [Chương 2: Cơ sở lý thuyết](#chương-2-cơ-sở-lý-thuyết)
9. [Chương 3: Phân tích, thiết kế](#chương-3-phân-tích-thiết-kế)
10. [Chương 4: Hiện thực đề tài](#chương-4-hiện-thực-đề-tài)
11. [Chương 5: Kiểm thử đề tài](#chương-5-kiểm-thử-đề-tài)
12. [Chương 6: Kết luận và hướng phát triển](#chương-6-kết-luận-và-hướng-phát-triển)
13. [Tài liệu tham khảo](#tài-liệu-tham-khảo)
14. [Phụ lục](#phụ-lục)

---

## Lời mở đầu

Trong bối cảnh cuộc cách mạng công nghiệp 4.0 đang diễn ra mạnh mẽ, công nghệ thông tin đã len lỏi vào mọi ngóc ngách của đời sống xã hội, làm thay đổi thói quen sinh hoạt và hành vi tiêu dùng của con người. Trong đó, nhu cầu giải trí, đặc biệt là điện ảnh, không còn chỉ dừng lại ở việc thưởng thức các tác phẩm tại rạp mà còn đòi hỏi sự tiện lợi, nhanh chóng và chủ động trong các khâu dịch vụ đi kèm. Việc tối ưu hóa trải nghiệm khách hàng từ bước tra cứu thông tin đến khi sở hữu tấm vé trên tay đã trở thành một yêu cầu cấp thiết đối với các đơn vị vận hành rạp chiếu phim.

Tuy nhiên, phương thức mua vé truyền thống tại quầy vẫn còn tồn tại nhiều hạn chế đáng kể. Khách hàng thường xuyên phải đối mặt với việc mất thời gian di chuyển đến rạp, xếp hàng chờ đợi lâu trong những khung giờ cao điểm, hoặc thậm chí là gặp tình trạng hết vé sau khi đã đến nơi. Điều này không chỉ gây ra sự bất tiện, mệt mỏi cho người xem mà còn khiến các rạp chiếu phim khó lòng quản lý lưu lượng khách hàng một cách hiệu quả và bỏ lỡ nhiều cơ hội kinh doanh tiềm năng.

Nhận thấy những thách thức đó, đồ án "Xây dựng ứng dụng đặt vé xem phim trên nền tảng Android" được thực hiện với mục tiêu mang đến một giải pháp công nghệ toàn diện và hiện đại. Ứng dụng được thiết kế nhằm giúp người dùng có thể chủ động hoàn toàn trong việc tìm kiếm các bộ phim đang hot, cập nhật lịch chiếu thời gian thực, lựa chọn vị trí chỗ ngồi ưng ý và thực hiện thanh toán trực tuyến một cách an toàn, bảo mật ngay trên thiết bị di động cá nhân. Qua đó, ứng dụng không chỉ giúp tiết kiệm thời gian, công sức cho khách hàng mà còn góp phần nâng cao chất lượng dịch vụ và tính chuyên nghiệp trong quản lý.

Báo cáo này sẽ trình bày một cách hệ thống và chi tiết toàn bộ quá trình nghiên cứu, phân tích yêu cầu người dùng, thiết kế kiến trúc hệ thống, xây dựng giao diện UI/UX và hiện thực hóa các chức năng cốt lõi của ứng dụng. Đồng thời, báo cáo cũng đánh giá kết quả đạt được, những khó khăn trong quá trình triển khai và định hướng phát triển ứng dụng trong tương lai.

## Lời cảm ơn

Lời đầu tiên, nhóm em xin bày tỏ lòng biết ơn sâu sắc và chân thành nhất tới Cô hướng dẫn **ThS. Trần Hồng Nghi**. Trong suốt quá trình thực hiện đồ án môn học NT118.Q13 - Phát triển ứng dụng trên thiết bị di động, Cô đã dành rất nhiều thời gian và tâm huyết để chỉ dẫn, định hướng cho nhóm từ những bước đầu tiên của đề tài cho đến khi hoàn thiện sản phẩm. Tuy ban đầu nhóm còn bỡ ngỡ với môn học này, nhưng nhờ sự hướng dẫn tận tình của Cô, nhóm đã dần có định hướng rõ ràng hơn cho đồ án. Mặc dù tiến độ có phần chậm hơn so với kế hoạch ban đầu, nhóm đã nhận ra những giá trị quý báu mà môn học mang lại và ngày càng yêu thích nội dung đề tài này.

Nhóm cũng xin gửi lời cảm ơn trân trọng đến quý Thầy/Cô thuộc khoa **Mạng máy tính và Truyền thông**, trường **Đại học Công nghệ Thông tin - Đại học Quốc gia Thành phố Hồ Chí Minh (UIT - VNU-HCM)**. Trong suốt những năm tháng học tập tại giảng đường, nhóm đã được truyền đạt những kiến thức nền tảng vững chắc và tiếp cận với những công nghệ hiện đại. Sự tận tâm trong giảng dạy và môi trường học tập năng động mà quý Thầy/Cô tạo ra chính là bệ phóng quan trọng giúp nhóm có đủ tự tin và kỹ năng để hiện thực hóa ý tưởng trong đồ án này.

Bên cạnh đó, nhóm xin dành lời cảm ơn đặc biệt đến gia đình – điểm tựa tinh thần vững chắc nhất. Cảm ơn bố mẹ và người thân đã luôn tin tưởng, động viên và tạo mọi điều kiện tốt nhất về cả vật chất lẫn tinh thần để chúng em có thể tập trung hoàn toàn vào việc học tập và nghiên cứu. Sự ủng hộ vô điều kiện của gia đình chính là động lực lớn lao nhất giúp nhóm vượt qua những giai đoạn áp lực và thử thách trong suốt chặng đường vừa qua.

Cuối cùng, mặc dù đã dành nhiều tâm huyết và nỗ lực để hoàn thành đồ án một cách chỉn chu nhất, nhưng do kiến thức và kinh nghiệm thực tế còn hạn chế, sản phẩm chắc chắn không tránh khỏi những thiếu sót nhất định. Nhóm rất mong nhận được những ý kiến đóng góp, phê bình quý báu từ quý Thầy/Cô để có thể rút kinh nghiệm và hoàn thiện ứng dụng hơn nữa trong tương lai.

Nhóm xin chân thành cảm ơn!

## Tóm tắt đồ án môn học

Đồ án "**Ứng dụng Đặt Vé Xem Phim**" được thực hiện bởi **Nhóm 11** thuộc môn học NT118.Q13 - Phát triển ứng dụng trên thiết bị di động, khoa **Mạng máy tính và Truyền thông**, trường **Đại học Công nghệ Thông tin - ĐHQG TP.HCM**. Nhóm bao gồm: **Nguyễn Trường Duy** (23520380 - Trưởng nhóm), **Võ Minh Dương** (23520358) và **Nguyễn Duy Quốc Bảo** (23520119). Ứng dụng được xây dựng trên nền tảng **Android Native** sử dụng ngôn ngữ **Java**, kết hợp với hệ sinh thái **Firebase** để quản lý dữ liệu và xác thực người dùng theo thời gian thực.

**Link GitHub Repository:** https://github.com/baongdqu/App_movie_booking_ticket

**Các module chức năng chính đã được hiện thực:**

- **Module Xác thực người dùng (`activities_1_login`, `activities_1_signup`, `activities_1_forgot_password`):** Tích hợp Firebase Authentication hỗ trợ đăng ký, đăng nhập bằng Email/Password, xác minh email và khôi phục mật khẩu. Bổ sung tính năng đăng nhập bằng Google cho trải nghiệm liền mạch.

- **Module Trang chủ (`fragments_home`):** Hiển thị banner quảng cáo dạng slideshow tự động (ViewPager2), danh sách phim đang chiếu (Top Movies) và phim sắp ra mắt (Upcoming) với giao diện cuộn ngang. Tích hợp thanh tìm kiếm phim theo tên với hiệu ứng animation.

- **Module Chi tiết phim (`parthome_movie_detail`):** Trình bày thông tin phim bao gồm poster, tên, thể loại, thời lượng, điểm IMDb, nội dung tóm tắt và danh sách diễn viên. Tích hợp **ExoPlayer** để phát trailer phim chất lượng cao (hỗ trợ HLS streaming) trực tiếp trong ứng dụng.

- **Module Chọn ghế (`parthome_SeatSelectionActivity`):** Sơ đồ ghế ngồi trực quan sử dụng GridLayout 8 cột, phân biệt rõ ràng các trạng thái ghế (trống/đang chọn/đã đặt) bằng màu sắc. Hỗ trợ chọn ngày và giờ chiếu linh hoạt, tự động tính toán tổng tiền theo thời gian thực.

- **Module Thanh toán (`PaymentActivity`):** Mô phỏng quy trình thanh toán với 2 phương thức: Ví VNPay và Số dư tài khoản. Sau khi thanh toán thành công, vé được lưu vào lịch sử và hiển thị mã QR/mã vé điện tử.

- **Module Trang cá nhân (`fragments_user`, `partuser_edit_profile`, `partuser_change_password`):** Quản lý thông tin cá nhân toàn diện với các tính năng: cập nhật avatar (tích hợp upload ảnh qua ImgBB API), đổi mật khẩu, xác thực số điện thoại với OTP, chỉnh sửa ngày sinh và giới tính, xem lịch sử vé đã đặt.

- **Module Chatbot AI (`activities_2_chatbot`):** Trợ lý ảo thông minh tích hợp mô hình ngôn ngữ lớn **Ollama** thông qua server Python và **ngrok**. Hỗ trợ tư vấn phim dựa trên sở thích, trả lời câu hỏi về lịch chiếu, giá vé với giao diện chat hiện đại kèm Quick Replies. Tự động gửi email người dùng làm context cho AI.

- **Module Bảo mật nâng cao (`activities_lock_screen`, `partuser_advanced_settings`):** Tính năng khóa ứng dụng bằng mã PIN 6 chữ số (chỉ kích hoạt khi đã đăng nhập), tự động reset PIN khi đăng xuất để đảm bảo quyền riêng tư, hỗ trợ tính năng "Quên PIN" để reset.

- **Module Đa ngôn ngữ (`extra_language_helper`):** Hỗ trợ chuyển đổi linh hoạt giữa nhiều ngôn ngữ: Tiếng Việt, Tiếng Anh, Tiếng Nhật, Tiếng Hàn, Tiếng Nga và Tiếng Trung.

- **Module Sở thích phim (`partuser_movie_preferences`):** Lưu trữ thể loại phim yêu thích, ngôn ngữ phim yêu thích và tùy chọn phụ đề của người dùng, phục vụ cho việc cá nhân hóa gợi ý từ Chatbot AI.

- **Module Thông báo (`fragments_notifications`):** Hiển thị danh sách thông báo theo thời gian thực từ Firebase, bao gồm thông báo hoàn tiền, cập nhật hồ sơ và các thông tin khuyến mãi.

**Công nghệ sử dụng:** Java, Android SDK (API 24-34), Firebase (Realtime Database, Authentication), Glide, ExoPlayer, OkHttp, Gson, Material Design Components.

**Kết quả đạt được:** Ứng dụng hoàn thiện với giao diện "Cinematic Dark Mode" lấy cảm hứng từ Netflix, hoạt động ổn định trên nhiều thiết bị Android. Sản phẩm đáp ứng đầy đủ quy trình đặt vé trực tuyến từ A-Z, có tính thực tiễn cao và khả năng mở rộng cho môi trường vận hành thực tế.

---

## Danh mục hình ảnh

| STT | Tên hình ảnh                                      | Nguồn / Mô tả          |
| --- | ------------------------------------------------- | ---------------------- |
| 1   | Hình 3.1: Sơ đồ Use Case tổng quát                | Tự vẽ                  |
| 2   | Hình 3.2: Sơ đồ kiến trúc hệ thống                | Tự vẽ                  |
| 3   | Hình 4.1: Giao diện màn hình Splash & Đăng nhập   | Chụp màn hình ứng dụng |
| 4   | Hình 4.2: Giao diện Trang chủ và Tìm kiếm phim    | Chụp màn hình ứng dụng |
| 5   | Hình 4.3: Giao diện Chi tiết phim và phát Trailer | Chụp màn hình ứng dụng |
| 6   | Hình 4.4: Giao diện Chọn ghế và Thanh toán        | Chụp màn hình ứng dụng |
| 7   | Hình 4.5: Giao diện Trang cá nhân                 | Chụp màn hình ứng dụng |
| 8   | Hình 4.6: Giao diện Thông báo                     | Chụp màn hình ứng dụng |
| 9   | Hình 4.7: Giao diện Chatbot tư vấn phim           | Chụp màn hình ứng dụng |
| 10  | Hình 4.8: Giao diện màn hình Đăng ký              | Chụp màn hình ứng dụng |
| 11  | Hình 4.9: Giao diện màn hình Quên mật khẩu        | Chụp màn hình ứng dụng |
| 12  | Hình 4.10: Giao diện Lịch sử đặt vé               | Chụp màn hình ứng dụng |
| 13  | Hình 4.11: Giao diện Chỉnh sửa hồ sơ              | Chụp màn hình ứng dụng |
| 14  | Hình 4.12: Giao diện Đổi mật khẩu                 | Chụp màn hình ứng dụng |
| 15  | Hình 4.13: Giao diện Sở thích phim                | Chụp màn hình ứng dụng |
| 16  | Hình 4.14: Giao diện Cài đặt nâng cao             | Chụp màn hình ứng dụng |
| 17  | Hình 4.15: Giao diện Khóa ứng dụng (PIN Lock)     | Chụp màn hình ứng dụng |
| 18  | Hình 4.16: Giao diện Xác thực số điện thoại       | Chụp màn hình ứng dụng |
| 19  | Hình 4.17: Giao diện Hệ thống thông báo           | Chụp màn hình ứng dụng |

## Danh mục bảng

| STT | Tên bảng                                              |
| --- | ----------------------------------------------------- |
| 1   | Bảng 3.1: Yêu cầu chức năng (Functional Requirements) |
| 2   | Bảng 3.2: Yêu cầu phi chức năng (Non-Functional)      |
| 3   | Bảng 5.1: Kiểm thử chức năng Đăng nhập/Đăng ký        |
| 4   | Bảng 5.2: Kiểm thử chức năng Tìm kiếm và Xem phim     |
| 5   | Bảng 5.3: Kiểm thử chức năng Đặt vé và Thanh toán     |
| 6   | Bảng 5.4: Kiểm thử chức năng Chatbot AI               |
| 7   | Bảng B.1: Giải thích các node Firebase                |
| 8   | Bảng C.1: API Endpoints (Chatbot Server)              |

## Danh mục từ viết tắt

| Từ viết tắt | Nghĩa tiếng Anh                   | Nghĩa tiếng Việt               |
| ----------- | --------------------------------- | ------------------------------ |
| UI          | User Interface                    | Giao diện người dùng           |
| UX          | User Experience                   | Trải nghiệm người dùng         |
| API         | Application Programming Interface | Giao diện lập trình ứng dụng   |
| SDK         | Software Development Kit          | Bộ công cụ phát triển phần mềm |
| LLM         | Large Language Model              | Mô hình ngôn ngữ lớn           |
| AI          | Artificial Intelligence           | Trí tuệ nhân tạo               |
| HLS         | HTTP Live Streaming               | Phát trực tuyến HTTP           |
| JSON        | JavaScript Object Notation        | Định dạng dữ liệu JSON         |
| REST        | Representational State Transfer   | Kiến trúc API RESTful          |
| NoSQL       | Not Only SQL                      | Cơ sở dữ liệu phi quan hệ      |
| PIN         | Personal Identification Number    | Mã số định danh cá nhân        |
| QR          | Quick Response                    | Mã phản hồi nhanh              |
| IMDB        | Internet Movie Database           | Cơ sở dữ liệu phim trực tuyến  |
| VNPay       | Vietnam Payment                   | Cổng thanh toán Việt Nam       |
| OTP         | One-Time Password                 | Mật khẩu dùng một lần          |
| ANR         | Application Not Responding        | Ứng dụng không phản hồi        |
| BaaS        | Backend-as-a-Service              | Backend như một dịch vụ        |

---

## Chương 1: Giới thiệu và tổng quan đề tài

### 1.1. Lý do chọn đề tài

Trong kỷ nguyên số hóa hiện nay, điện thoại thông minh (smartphone) không còn là một thiết bị xa xỉ mà đã trở thành vật bất ly thân của đại đa số người dân Việt Nam. Theo thống kê từ Statista năm 2024, Việt Nam có khoảng 70 triệu người dùng smartphone, chiếm hơn 70% dân số. Sự bùng nổ của hạ tầng mạng 4G/5G cùng với hệ sinh thái ứng dụng di động phong phú đã thay đổi hoàn toàn cách thức con người tiếp cận thông tin, giải trí và sử dụng dịch vụ.

Trong lĩnh vực giải trí, nhu cầu thưởng thức điện ảnh tại rạp luôn chiếm một tỷ trọng lớn trong chi tiêu của người tiêu dùng. Theo báo cáo của CJ CGV Việt Nam, thị trường rạp chiếu phim Việt Nam đạt doanh thu hơn 4.500 tỷ đồng vào năm 2023, với tốc độ tăng trưởng trung bình 15-20% mỗi năm. Tuy nhiên, quy trình mua vé truyền thống vẫn đang bộc lộ nhiều bất cập nghiêm trọng:

- **Về phía người dùng:** Khách hàng phải tốn thời gian di chuyển đến rạp mà không chắc chắn còn vé hay không, đặc biệt vào các dịp cuối tuần, lễ Tết hoặc khi có phim "bom tấn" ra mắt. Việc xếp hàng chờ đợi từ 15-30 phút gây ra sự mệt mỏi và ức chế, ảnh hưởng tiêu cực đến trải nghiệm xem phim tổng thể. Ngoài ra, người dùng thiếu thông tin trực quan về vị trí ghế ngồi thực tế, lịch chiếu cập nhật theo thời gian thực khiến việc sắp xếp kế hoạch cá nhân trở nên khó khăn.

- **Về phía rạp chiếu phim:** Việc quản lý thủ công dẫn đến sai sót trong việc ghi nhận giao dịch, khó khăn trong việc thống kê doanh thu theo thời gian thực và hạn chế khả năng phân tích hành vi khách hàng để đưa ra các chiến lược marketing hiệu quả.

- **Về xu hướng công nghệ:** Sự phát triển mạnh mẽ của trí tuệ nhân tạo (AI), đặc biệt là các mô hình ngôn ngữ lớn (LLM) như GPT, Gemini, Ollama, mở ra cơ hội tích hợp các tính năng thông minh như chatbot tư vấn phim, gợi ý cá nhân hóa dựa trên sở thích người dùng.

Chính vì những lý do trên, việc phát triển một **ứng dụng đặt vé xem phim trên nền tảng Android** là một yêu cầu tất yếu của thời đại, nhằm giải quyết triệt để các vấn đề nêu trên, mang lại sự tiện lợi tối đa cho người dùng, đồng thời nâng cao hiệu quả vận hành và kinh doanh cho các cụm rạp chiếu phim.

### 1.2. Mục tiêu đề tài

Đồ án hướng tới việc đạt được các mục tiêu cụ thể sau đây:

**Về mặt kỹ thuật:**
- Xây dựng thành công một ứng dụng di động hoàn chỉnh chạy trên hệ điều hành Android (API 24 - Android 7.0 trở lên), sử dụng ngôn ngữ lập trình **Java** kết hợp với kiến trúc **Single Activity - Multiple Fragments** để đảm bảo tính module hóa, dễ bảo trì và mở rộng mã nguồn.
- Tích hợp hệ sinh thái **Firebase** làm Backend-as-a-Service (BaaS), bao gồm Firebase Realtime Database để lưu trữ và đồng bộ dữ liệu thời gian thực, Firebase Authentication để quản lý xác thực người dùng an toàn.
- Xây dựng **AI Server** kết hợp với ứng dụng mô hình ngôn ngữ lớn **Ollama** (chạy local), expose ra Internet thông qua **ngrok** để cung cấp API cho tính năng Chatbot tư vấn phim thông minh.
- Áp dụng các nguyên tắc thiết kế Android hiện đại với **Material Design Components**, **ViewBinding** và các thư viện hỗ trợ mạnh mẽ như Glide (tải và cache ảnh), ExoPlayer (phát trailer video HLS), OkHttp (kết nối mạng HTTP), Gson (xử lý JSON), CircleImageView (hiển thị avatar tròn) và tích hợp **VNPay SDK** để mô phỏng thanh toán.

**Về mặt trải nghiệm người dùng (UX):**
- Thiết kế giao diện theo phong cách "**Cinematic Dark Mode**" lấy cảm hứng từ Netflix, với tông màu tối làm nổi bật poster phim và tạo không khí rạp chiếu phim ngay trên thiết bị di động.
- Đảm bảo người dùng có thể hoàn tất quy trình đặt vé từ bước chọn phim đến khi thanh toán chỉ trong vòng **chưa đầy 3 phút** với tối đa 5 bước thao tác.
- Tối ưu hóa tốc độ tải dữ liệu bằng cơ chế caching thông minh, hiển thị sơ đồ ghế ngồi trực quan với phân biệt màu sắc rõ ràng cho từng trạng thái ghế.

**Về tính năng nâng cao:**
- **AI Chatbot thông minh:** Tích hợp mô hình ngôn ngữ lớn **Ollama** (chạy local) thông qua server từ xa và **ngrok** để hỗ trợ tư vấn phim dựa trên sở thích, giải đáp thắc mắc về lịch chiếu, giá vé một cách tự nhiên như trò chuyện với người thật.
- **Bảo mật đa lớp:** Triển khai xác thực qua **mã PIN 6 chữ số** để khóa ứng dụng, bảo vệ thông tin cá nhân và lịch sử giao dịch. PIN tự động được reset khi người dùng đăng xuất để đảm bảo quyền riêng tư.
- **Hỗ trợ đa ngôn ngữ:** Ứng dụng hỗ trợ chuyển đổi linh hoạt giữa 6 ngôn ngữ: Tiếng Việt, Tiếng Anh, Tiếng Nhật, Tiếng Hàn, Tiếng Nga và Tiếng Trung, phục vụ cả du khách quốc tế và người Việt ở nước ngoài.
- **Cá nhân hóa trải nghiệm:** Lưu trữ sở thích thể loại phim và thời gian xem phim ưa thích của người dùng để Chatbot AI có thể đưa ra gợi ý phù hợp nhất.

### 1.3. Phạm vi đề tài

Đề tài tập trung nghiên cứu và triển khai trong các phạm vi giới hạn sau:

**Về đối tượng người dùng:**
- **Khách hàng (End User):** Người dùng cuối cùng sử dụng ứng dụng di động Android để tra cứu thông tin phim, đặt vé, thanh toán và tương tác với các tính năng như Chatbot, quản lý tài khoản cá nhân. Bao gồm cả khách hàng mới (chưa có tài khoản) và khách hàng cũ (đã đăng ký).
- **Quản trị viên (Admin):** Người dùng có quyền truy cập trực tiếp vào Firebase Console để quản lý dữ liệu phim (thêm, sửa, xóa thông tin phim, suất chiếu, banner quảng cáo) và quản lý tài khoản người dùng. Trong phạm vi đồ án, chức năng Admin được thực hiện thông qua Firebase Console thay vì xây dựng giao diện quản trị riêng.

**Về nền tảng triển khai:**
- Ứng dụng được phát triển và kiểm thử trên nền tảng **Android** với phiên bản tối thiểu là **Android 7.0 (API 24)** và phiên bản mục tiêu là **Android 14 (API 34)**.
- Dữ liệu được lưu trữ trên nền tảng đám mây **Firebase** của Google, đảm bảo tính sẵn sàng cao và khả năng mở rộng.

**Về chức năng:**
- Đồ án tập trung vào các chức năng cốt lõi của một ứng dụng đặt vé xem phim: hiển thị danh sách phim, xem chi tiết và trailer, chọn suất chiếu và ghế ngồi, mô phỏng thanh toán.
- Tính năng thanh toán hiện ở mức **mô phỏng (demo)**, chưa tích hợp với các cổng thanh toán thực như VNPay, Momo do yêu cầu đăng ký tài khoản doanh nghiệp.
- Tính năng Chatbot AI hoạt động khi có server local chạy Ollama và ngrok expose ra Internet.

**Về giới hạn:**
- Ứng dụng chưa hỗ trợ nền tảng iOS hoặc Web.
- Chưa tích hợp hệ thống thông báo đẩy (Push Notifications) do nằm ngoài phạm vi thời gian thực hiện.
- Dữ liệu phim được nhập thủ công vào Firebase, chưa tích hợp với API của các rạp phim thực tế (CGV, Lotte Cinema, Galaxy...).
- Chưa hỗ trợ **Offline Mode**: Ứng dụng yêu cầu kết nối Internet để hoạt động, chưa tận dụng tính năng Disk Persistence của Firebase để lưu cache dữ liệu phim.
- Tính năng **Chatbot AI phụ thuộc vào server**: Nếu server Ollama hoặc ngrok không hoạt động, Chatbot sẽ không phản hồi được.
- **Ghế ngồi mô phỏng**: Sơ đồ ghế ngồi là dữ liệu tĩnh, chưa đồng bộ thời gian thực giữa các người dùng đang đặt vé cùng suất chiếu (có thể xảy ra trùng ghế trong thực tế).
- Chưa tích hợp xác thực **sinh trắc học** (vân tay/Face ID), hiện chỉ hỗ trợ mã PIN 6 chữ số.
- Người dùng chưa thể **đánh giá và bình luận phim** trực tiếp trong ứng dụng, điểm IMDb chỉ hiển thị dữ liệu có sẵn.

### 1.4. Phương pháp nghiên cứu

Để hoàn thành đồ án, nhóm đã áp dụng các phương pháp nghiên cứu sau:

**Phương pháp nghiên cứu lý thuyết:**
- Tìm hiểu, nghiên cứu tài liệu về lập trình Android từ các nguồn chính thức như Android Developers Documentation, Firebase Documentation.
- Nghiên cứu các nguyên tắc thiết kế giao diện người dùng theo Material Design Guidelines của Google.
- Tìm hiểu về các mô hình ngôn ngữ lớn (LLM) và cách tích hợp AI vào ứng dụng di động thông qua REST API.
- Nghiên cứu kiến thức cơ bản về **triển khai AI Server đơn giản**: cài đặt và sử dụng **Ollama** để chạy các mô hình LLM trên máy local, xây dựng API endpoint bằng ứng dụng riêng, sử dụng **ngrok** để tạo đường hầm (tunnel) expose server local ra Internet cho ứng dụng di động truy cập.

**Phương pháp thực nghiệm:**
- Xây dựng ứng dụng theo mô hình phát triển **Agile** với các vòng lặp (sprint) ngắn, liên tục cải tiến dựa trên phản hồi.
- Kiểm thử ứng dụng trên cả thiết bị thật (Samsung Galaxy A52, Xiaomi Redmi Note 11) và trình giả lập Android Studio để đảm bảo tính tương thích.
- Thu thập ý kiến phản hồi từ người dùng thử nghiệm để cải thiện trải nghiệm sử dụng.

**Công cụ và môi trường phát triển:**
- **IDE:** Android Studio (phiên bản Koala 2024.1.1)
- **Ngôn ngữ:** Java (JDK 11), XML (giao diện)
- **Backend:** Firebase Realtime Database, Firebase Authentication
- **AI Server:** Ollama, vibecoding, ngrok
- **Quản lý phiên bản:** Git, GitHub

---

## Chương 2: Cơ sở lý thuyết

### 2.1. Lập trình Android

- **Hệ điều hành Android:** Là hệ điều hành di động phổ biến nhất thế giới, được xây dựng dựa trên nhân Linux. Android cung cấp một nền tảng mã nguồn mở (AOSP) cho phép các nhà phát triển tự do tùy biến giao diện và chức năng. Kiến trúc của Android bao gồm 4 tầng chính: Linux Kernel, Libraries & Android Runtime, Application Framework và Applications, giúp quản lý tài nguyên phần cứng hiệu quả và cung cấp môi trường thực thi đa nhiệm mạnh mẽ cho các ứng dụng.
- **Ngôn ngữ lập trình Java:** Là một trong những ngôn ngữ lập trình hướng đối tượng (OOP) phổ biến và lâu đời nhất trong phát triển ứng dụng Android. Java sở hữu hệ sinh thái thư viện đồ sộ, cộng đồng hỗ trợ mạnh mẽ và tính tương thích cao. Trong phát triển Android, Java giúp nhà phát triển dễ dàng quản lý mã nguồn thông qua các đặc tính như đóng gói, kế thừa, đa hình và xử lý ngoại lệ chặt chẽ. Dù có sự xuất hiện của các ngôn ngữ mới, Java vẫn giữ vai trò nền tảng quan trọng nhờ tính ổn định và khả năng tối ưu hóa hiệu suất hệ thống tốt.
- **Môi trường phát triển Android Studio:** Là môi trường phát triển tích hợp (IDE) chính thức dành cho Android, dựa trên nền tảng IntelliJ IDEA của JetBrains. Android Studio cung cấp hệ thống xây dựng dựa trên _Gradle_ linh hoạt, cho phép tùy chỉnh các biến thể bản dựng (Build Variants). IDE này tích hợp sẵn _Layout Editor_ với tính năng xem trước thời gian thực, bộ công cụ _Android Profiler_ để theo dõi mức tiêu thụ CPU, bộ nhớ, mạng và _Logcat_ để kiểm soát lỗi hệ thống. Đặc biệt, nó hỗ trợ tốt việc tích hợp các hệ thống quản lý phiên bản như Git và cung cấp trình giả lập (Emulator) hiệu suất cao để kiểm thử trên nhiều cấu hình thiết bị khác nhau.
- **Android Jetpack:** Tập hợp các thư viện thành phần (Components) giúp nhà phát triển tuân thủ các tiêu chuẩn thiết kế (Best Practices), giảm thiểu mã lặp lại và đảm bảo ứng dụng hoạt động ổn định trên nhiều phiên bản hệ điều hành khác nhau.

### 2.2. Cơ sở dữ liệu và lưu trữ dữ liệu

- **Firebase Realtime Database:** Là hệ thống cơ sở dữ liệu NoSQL dựa trên nền tảng đám mây, cho phép lưu trữ và đồng bộ hóa dữ liệu giữa các người dùng theo thời gian thực. Trong ứng dụng, Realtime Database được sử dụng để quản lý các cấu trúc dữ liệu phức tạp như danh sách phim (`Items`), phim sắp chiếu (`Upcomming`), hệ thống `Banners` và thông tin người dùng (`users`). Với cơ chế lắng nghe sự kiện (Event Listeners), mọi thay đổi về dữ liệu trên server sẽ được phản chiếu ngay lập tức lên giao diện người dùng mà không cần thực hiện các yêu cầu HTTP thủ công.
- **Firebase Authentication:** Cung cấp giải pháp xác thực người dùng toàn diện và bảo mật. Ứng dụng triển khai phương thức đăng ký và đăng nhập bằng Email/Mật khẩu, giúp quản lý danh tính người dùng một cách hiệu quả. Firebase Authentication tự động xử lý các tác vụ phức tạp như gửi email xác thực, khôi phục mật khẩu và lưu trữ token phiên làm việc. Mỗi tài khoản người dùng được định danh bằng một mã UID duy nhất, làm cơ sở để phân quyền truy cập và liên kết dữ liệu cá nhân trong cơ sở dữ liệu, đảm bảo an toàn thông tin tuyệt đối.
- **SharedPreferences (Lưu trữ cục bộ):** Ứng dụng sử dụng SharedPreferences để lưu trữ các cài đặt và dữ liệu nhỏ trên thiết bị người dùng, bao gồm: mã PIN khóa ứng dụng, ngôn ngữ hiển thị, cài đặt âm thanh, theme giao diện và trạng thái đăng nhập. SharedPreferences cung cấp cách lưu trữ key-value đơn giản, nhanh chóng và bảo mật cho các dữ liệu cấu hình.
- **Giao tiếp RESTful API và Định dạng JSON:** Để tích hợp trí tuệ nhân tạo (Chatbot), ứng dụng sử dụng kiến trúc RESTful API để thực hiện các yêu cầu mạng đến máy chủ xử lý ngôn ngữ tự nhiên. Dữ liệu trao đổi được chuẩn hóa dưới định dạng JSON (JavaScript Object Notation), sử dụng thư viện **Gson** để chuyển đổi (serialize/deserialize) giữa đối tượng Java và JSON một cách hiệu quả.
- **OkHttp và ngrok:** Thư viện **OkHttp** được sử dụng làm tầng xử lý mạng (Networking Layer), hỗ trợ các giao thức HTTP/2, quản lý kết nối hiệu quả và xử lý lỗi mạng linh hoạt. Trong quá trình phát triển, công cụ **ngrok** được sử dụng để thiết lập một đường hầm (tunnel) bảo mật, cho phép ứng dụng di động giao tiếp trực tiếp với server AI đang chạy tại môi trường phát triển cục bộ (localhost) một cách minh bạch và nhanh chóng.

### 2.3. Các công nghệ và thư viện hỗ trợ

- **Kiến trúc ứng dụng:** Ứng dụng được xây dựng dựa trên kiến trúc **Single Activity** (sử dụng một Activity chính quản lý nhiều Fragment) kết hợp với **ViewBinding**. ViewBinding giúp tương tác với các thành phần giao diện một cách an toàn (null-safety) và nhanh chóng hơn so với phương pháp `findViewById` truyền thống.
- **Giao diện người dùng (UI/UX):**
  - **Material Design Components:** Sử dụng bộ thư viện chuẩn của Google để xây dựng các thành phần giao diện hiện đại, nhất quán như `MaterialButton`, `TextInputEditText`, `CardView`, `BottomNavigationView`.
  - **ConstraintLayout:** Layout linh hoạt cho phép xây dựng giao diện phức tạp với hiệu suất cao, giảm số lớp View lồng nhau.
  - **RecyclerView:** Thành phần hiển thị danh sách hiệu quả với cơ chế tái sử dụng ViewHolder, được sử dụng rộng rãi trong ứng dụng với 8 Adapter khác nhau (TopMovieAdapter, TicketAdapter, ChatMessageAdapter, CastListAdapter, NotificationAdapter...).
  - **ViewPager2:** Được sử dụng để tạo slide banner quảng cáo phim mượt mà và hiệu ứng chuyển trang đẹp mắt với tự động chuyển trang.
  - **CircleImageView:** Thư viện hỗ trợ hiển thị hình ảnh (avatar người dùng, diễn viên) dưới dạng hình tròn chuyên nghiệp.
- **Xử lý đa phương tiện:**
  - **Glide:** Thư viện tải và hiển thị ảnh mạnh mẽ, hỗ trợ caching thông minh giúp tối ưu hóa băng thông và tăng tốc độ tải trang khi hiển thị poster phim từ URL.
  - **ExoPlayer:** Trình phát video mã nguồn mở của Google, được tích hợp để phát trailer phim chất lượng cao (hỗ trợ HLS streaming) trực tiếp trong ứng dụng với độ trễ thấp và khả năng tùy biến giao diện cao.
- **Xử lý dữ liệu và kết nối mạng:**
  - **Gson (Google Json):** Thư viện dùng để chuyển đổi (serialize/deserialize) các đối tượng Java sang định dạng JSON và ngược lại, phục vụ cho việc lưu trữ và trao đổi dữ liệu với server.
  - **OkHttp:** Thư viện HTTP Client hiệu suất cao, hỗ trợ HTTP/2, đóng vai trò quan trọng trong việc thiết lập kết nối mạng ổn định để giao tiếp với AI Chatbot Server.
- **Tích hợp thanh toán:**
  - **VNPay SDK:** Tích hợp bộ SDK thanh toán VNPay (`merchant-1.0.25.aar`) để mô phỏng quy trình thanh toán trực tuyến, hỗ trợ thanh toán qua ví điện tử VNPay và các phương thức thanh toán khác.
- **Tích hợp AI (Chatbot):** Ứng dụng tích hợp trí tuệ nhân tạo thông qua **AI Server từ xa** (sử dụng mô hình ngôn ngữ lớn Ollama) được expose qua **ngrok**. Giao tiếp qua REST API với OkHttp, cho phép người dùng trò chuyện tự nhiên để nhận gợi ý phim, hỏi về lịch chiếu và giá vé dựa trên sở thích cá nhân đã lưu.

---

## Chương 3: Phân tích, thiết kế

### 3.1. Phân tích yêu cầu

#### 3.1.1. Yêu cầu chức năng (Functional Requirements)

Hệ thống được thiết kế để đáp ứng đầy đủ chu trình trải nghiệm của người dùng xem phim:

- **Quản lý tài khoản (Authentication & Authorization):**
  - Cho phép người dùng đăng ký tài khoản mới với các thông tin cơ bản (họ tên, email, mật khẩu).
  - Đăng nhập hệ thống bảo mật qua Email/Password với xác minh email.
  - Tính năng "Quên mật khẩu" hỗ trợ khôi phục quyền truy cập nhanh chóng qua email.
  - Đăng xuất an toàn, tự động xóa dữ liệu nhạy cảm (PIN Lock) khi đăng xuất.
- **Tra cứu và Tìm kiếm phim (Discovery):**
  - Hiển thị danh sách phim nổi bật ("Top Movies") và phim sắp chiếu ("Upcoming") ngay tại màn hình chính với giao diện cuộn ngang.
  - Banner quảng cáo slideshow tự động chuyển trang.
  - Tìm kiếm phim thông minh theo từ khóa (tên phim) với hiệu ứng animation.
  - Xem chi tiết thông tin phim: Nội dung tóm tắt, danh sách diễn viên (với ảnh), thể loại, thời lượng, năm sản xuất, điểm đánh giá IMDb.
  - Phát Trailer phim trực tuyến chất lượng cao (hỗ trợ HLS streaming) bằng ExoPlayer.
- **Đặt vé và Thanh toán (Booking & Payment):**
  - Lựa chọn ngày chiếu và suất chiếu phù hợp với giao diện cuộn ngang.
  - Sơ đồ ghế ngồi trực quan (GridLayout 8 cột): Phân biệt rõ ghế đã đặt (xám), ghế đang chọn (hồng đậm) và ghế trống (trắng/hồng nhạt).
  - Tính toán tổng tiền tự động theo thời gian thực dựa trên số lượng ghế được chọn.
  - Mô phỏng quy trình thanh toán qua VNPay hoặc số dư tài khoản, xuất vé điện tử sau khi thanh toán thành công.
- **Tiện ích cá nhân và Nâng cao:**
  - Quản lý thông tin cá nhân (Profile): Cập nhật họ tên, avatar (chụp ảnh hoặc chọn từ thư viện, upload qua ImgBB API), đổi mật khẩu, chỉnh sửa ngày sinh và giới tính.
  - **Xác thực số điện thoại:** Liên kết và xác thực số điện thoại với mã OTP qua Firebase Phone Auth, tăng cường bảo mật tài khoản.
  - Lịch sử đặt vé: Xem lại danh sách các vé đã mua với đầy đủ thông tin (phim, ngày, ghế, giá), hỗ trợ huỷ vé và hoàn tiền.
  - **Sở thích phim:** Lưu trữ thể loại phim yêu thích, ngôn ngữ phim và tùy chọn phụ đề để cá nhân hóa gợi ý từ Chatbot.
  - **AI Chatbot:** Trợ lý ảo thông minh hỗ trợ tư vấn phim dựa trên sở thích, giải đáp thắc mắc về lịch chiếu và giá vé. Tự động gửi context bao gồm email người dùng.
  - **Cài đặt nâng cao:** Thiết lập ngôn ngữ (6 ngôn ngữ), bật/tắt âm thanh ứng dụng, chế độ tối (Dark Mode), và đặc biệt là tính năng **Khóa ứng dụng (PIN Lock 6 số)** với các tùy chọn: bật/tắt, đổi PIN, Quên PIN để bảo vệ quyền riêng tư.
  - **Hệ thống thông báo:** Hiển thị thông báo hoàn tiền, cập nhật hồ sơ và các thông tin khuyến mãi theo thời gian thực từ Firebase.

#### 3.1.2. Yêu cầu phi chức năng (Non-Functional Requirements)

- **Hiệu năng (Performance):** Ứng dụng phải phản hồi thao tác người dùng một cách nhanh chóng. Việc tải và hiển thị ảnh poster phim được tối ưu hóa thông qua cơ chế caching của thư viện Glide, giúp giảm băng thông và tăng tốc độ hiển thị khi xem lại các phim đã tải trước đó.
- **Bảo mật (Security):** Mật khẩu người dùng được quản lý hoàn toàn bởi Firebase Authentication, không lưu trữ plain-text trên thiết bị. Mã PIN khóa ứng dụng được lưu trong SharedPreferences. Tính năng khóa PIN hoạt động khi ứng dụng chuyển về từ nền (background) và tự động reset khi người dùng đăng xuất.
- **Giao diện và Trải nghiệm (UI/UX):** Giao diện thiết kế theo phong cách "Cinematic Dark Mode" lấy cảm hứng từ Netflix, sử dụng tông màu tối kết hợp điểm nhấn đỏ (#E50914) phù hợp với không khí rạp chiếu phim. Các thao tác chuyển màn hình có hiệu ứng animation mượt mà, tuân thủ Material Design Guidelines.
- **Độ tin cậy (Reliability):** Ứng dụng xử lý tốt các ngoại lệ thông qua việc kiểm tra kết nối mạng trước khi thực hiện các tác vụ, hiển thị thông báo lỗi thân thiện khi mất mạng hoặc server Chatbot không phản hồi, tránh crash đột ngột (Force Close).
- **Tương thích (Compatibility):** Ứng dụng hỗ trợ các thiết bị Android từ phiên bản 7.0 (API 24) đến Android 14 (API 34), đảm bảo hoạt động trên đa dạng kích thước màn hình từ 5 inch đến 7 inch.
- **Khả năng mở rộng (Scalability):** Mã nguồn được tổ chức theo kiến trúc Single Activity - Multiple Fragments, phân chia rõ ràng theo module (activities, fragments, adapters, models, extras) giúp dễ dàng thêm mới chức năng trong tương lai.
- **Khả năng bảo trì (Maintainability):** Sử dụng ViewBinding để truy cập View an toàn, đặt tên file và class theo quy ước nhất quán, comment code đầy đủ cho các phương thức quan trọng.

### 3.2. Thiết kế hệ thống

### 3.2. Thiết kế hệ thống

- **Kiến trúc tổng thể (System Architecture):**

  - **Mô hình:** Client-Server.
  - **Client (Android App):** Xây dựng trên nền tảng Android Native (Java/Kotlin), đóng vai trò giao diện tương tác trực tiếp với người dùng. Client chịu trách nhiệm hiển thị dữ liệu, xử lý logic nghiệp vụ tại máy trạm (như validation, navigation) và giao tiếp với Server.
  - **Server & Database:**
    - **Firebase Realtime Database:** Đóng vai trò Backend-as-a-Service (BaaS), cung cấp API lưu trữ và đồng bộ dữ liệu thời gian thực.
    - **AI Server (Local + Ngrok):** Một module xử lý ngôn ngữ tự nhiên chạy mô hình **Ollama** tại máy cục bộ (Localhost), được expose ra Internet thông qua **Ngrok**. Server này nhận các truy vấn text từ người dùng và trả về câu trả lời tư vấn phim thông minh.

- **Sơ đồ Use Case (Các chức năng chính):**

  - **Actor Khách hàng:** Đăng nhập, Đăng ký, Tìm kiếm phim (theo tên), Xem danh sách (Đang chiếu/Sắp chiếu), Xem chi tiết phim (Trailer, Diễn viên, Nội dung), Chọn suất chiếu, Chọn ghế ngồi, Thanh toán (Giả lập), Xem lịch sử vé đã đặt, Quản lý tài khoản, Chat với AI bot.
  - **Actor Hệ thống:** Xác thực người dùng (Auth), Gửi email xác minh, Tính toán giá vé, Phản hồi tin nhắn Chatbot.

- **Thiết kế Cơ sở dữ liệu (Firebase NoSQL Schema):**

  - **Node `Items` & `Upcomming` (Danh sách phim):** Lưu trữ mảng các đối tượng phim.
    - `Title` (String): Tên phim.
    - `Description` (String): Tóm tắt nội dung.
    - `Poster` (String): URL ảnh poster chính.
    - `Time` (String): Thời lượng phim.
    - `Trailer` (String): URL video trailer (mp4/m3u8).
    - `Imdb` (Double): Điểm đánh giá.
    - `Year` (Int): Năm phát hành.
    - `price` (Int): Giá vé cơ bản.
    - `Genre` (List<String>): Danh sách thể loại.
    - `Casts` (List<Object>): Danh sách diễn viên (`Actor`, `PicUrl`).
  - **Node `users`:**
    - `UserID` (Key):
      - `fullName`, `email`, `avatarUrl`, `balance`.
      - `tickets` (Sub-collection): Lưu lịch sử đặt vé (`movieTitle`, `seatCoordinates`, `totalPrice`, `bookingDate`).
  - **Node `Banners`:** Chứa danh sách URL ảnh cho slider quảng cáo.

- **Tổ chức Class và Component (Class Design):**
  - **Activities (Màn hình chính):**
    - `activities_0_loading`: Màn hình chờ, kiểm tra mạng.
    - `activities_1_login` / `_signup`: Xác thực người dùng.
    - `activities_2_a_menu_manage_fragments`: Activity chính chứa Bottom Navigation và các Fragment.
    - `activities_2_chatbot`: Giao diện chat với AI.
    - `parthome_movie_detail`: Hiển thị thông tin chi tiết phim.
    - `parthome_SeatSelectionActivity`: Xử lý logic chọn ghế và tính tiền.
    - `PaymentActivity`: Màn hình thanh toán.
  - **Fragments (Các tab chức năng):**
    - `fragments_home`: Tab Trang chủ (Slider, List phim).
    - `fragments_user`: Tab Cá nhân (Profile, Cài đặt).
  - **Models (Đối tượng dữ liệu):**
    - `Movie`: Mapping dữ liệu phim từ Firebase.
    - `TicketSimple`: Đại diện cho vé đã đặt.
    - `ChatMessage`: Tin nhắn trong khung chat.
  - **Adapters (Bộ chuyển đổi dữ liệu hiển thị):**
    - `TopMovieAdapter`: Hiển thị danh sách phim ngang/dọc.
    - `SliderAdapter`: Hiển thị Banner quảng cáo.
    - `ChatMessageAdapter`: Hiển thị cuộc hội thoại chat.

### 3.3. Thiết kế giao diện (UI/UX)

- **Triết lý thiết kế (Design Philosophy):**

  - **Phong cách:** Ứng dụng theo đuổi phong cách "Cinematic Dark Mode" lấy cảm hứng từ Netflix, với nền tối (`@color/bg_color`) giúp làm nổi bật poster phim và trailer.
  - **Màu sắc:** Sử dụng tông màu đỏ thương hiệu (`@color/netflix_red`) cho các nút hành động chính (CTA) như "Đặt vé", tạo điểm nhấn thị giác mạnh mẽ trên nền tối. Text màu trắng (`@color/text_primary`) đảm bảo độ tương phản cao, dễ đọc.
  - **Font chữ:** Sử dụng các biến thể của font `sans-serif-medium` để tạo cảm giác hiện đại, gọn gàng và chuyên nghiệp.

- **Bố cục chi tiết các màn hình (Layouts):**

  - **Màn hình Home (`layouts_fragments_home.xml`):**
    - Phần trên cùng là **Banner Slideshow** tự động chạy (sử dụng `ViewPager2`), hiển thị các phim hot nhất với hiệu ứng chuyển trang mượt mà.
    - Thanh tìm kiếm (Search Box) được ẩn và chỉ hiện ra với hiệu ứng animation khi người dùng ấn vào biểu tượng kính lúp.
    - Bên dưới là các danh sách cuộn ngang (Horizontal RecyclerViews) phân loại phim theo "Top Movies" (Phim nổi bật) và "Upcoming" (Sắp chiếu), tối ưu hóa thao tác vuốt của người dùng di động.
  - **Màn hình Chi tiết phim (`parthome_movie_details.xml`):**
    - Thiết kế chia cột thông minh: Poster phim nằm bên trái, thông tin chi tiết (Tên, Thể loại, Thời lượng) nằm bên phải.
    - Các nút chức năng "Like" và "Trailer" được bố trí ngay dưới thông tin phim để thao tác nhanh.
    - Nội dung tóm tắt (Summary), Danh sách diễn viên (Cast) và Hình ảnh liên quan (Images) được sắp xếp theo trục dọc, dễ dàng theo dõi.
    - Nút "**MUA VÉ (BUY TICKET)**" cố định ở đáy màn hình (Sticky Bottom Button), luôn hiển thị để nhắc nhở người dùng thực hiện hành động chuyển đổi.
  - **Màn hình Đăng nhập (`layouts_1_login.xml`):**

    - Sử dụng hình nền tối màu đen/xám (`@color/netflix_black`) đặc trưng, giúp nổi bật Logo ứng dụng và các trường nhập liệu.
    - Các ô nhập liệu (Email, Mật khẩu) sử dụng `TextInputLayout` chuẩn Material Design với nền xám đậm, đảm bảo tính thẩm mỹ và dễ nhìn trong điều kiện thiếu sáng.
    - Nút "**Đăng nhập**" màu đỏ Netflix nổi bật, kích thích hành động. Các liên kết phụ như "Quên mật khẩu" được bố trí tinh tế phía dưới.

  - **Màn hình Chọn ghế (`parthome_seat_selection.xml`):**

    - Sử dụng `CoordinatorLayout` để quản lý các thành phần mượt mà.
    - Phần chọn Ngày và Giờ chiếu thiết kế dạng cuộn ngang (`HorizontalScrollView`), tiết kiệm diện tích.
    - **Sơ đồ ghế:** Sử dụng `GridLayout` 8 cột để mô phỏng chính xác vị trí ghế trong rạp. Có chú thích rõ ràng bằng màu sắc: Ghế trống (Trắng/Hồng nhạt), Đã chọn (Hồng đậm), Đã đặt (Xám).
    - Thanh thanh toán phía dưới hiển thị tổng tiền theo thời gian thực và nút "Tiếp tục" nổi bật.

  - **Màn hình Thanh toán (`activity_payment.xml`):**

    - Thông tin vé được đóng gói trong một `CardView` gọn gàng, hiển thị đầy đủ Poster, Tên phim, Định dạng (2D/3D), Phòng chiếu và Số ghế đã chọn.
    - Thông tin người nhận vé được tách biệt rõ ràng.
    - Thanh công cụ đáy cung cấp các tùy chọn thanh toán linh hoạt: Thanh toán qua Ví VNPay hoặc Số dư tài khoản.

  - **Màn hình Cá nhân (`layouts_fragments_user.xml`):**
    - Nổi bật với Avatar người dùng khổ lớn được bo viền đỏ (`@color/profile_accent`), đặt trên nền thẻ Card sang trọng.
    - Các nút chức năng quản lý (Chỉnh sửa hồ sơ, Sở thích phim, Cài đặt, Đăng xuất) sử dụng `MaterialButton` với biểu tượng minh họa trực quan (Icons), được xếp dọc dễ thao tác.
  - **Màn hình Chatbot (`layouts_2_chatbot.xml`):**
    - Thanh Toolbar phía trên có hiệu ứng Gradient, hiển thị Avatar Bot với đèn trạng thái "Online" xanh lá (`@color/netflix_green`), tạo cảm giác tin cậy.
    - Tích hợp thanh **Quick Replies** (Trả lời nhanh) dạng thẻ (Chips) có thể cuộn ngang, giúp người dùng đặt câu hỏi phổ biến (như "Gợi ý phim", "Giá vé") chỉ với một cú chạm.
    - Khu vực nhập liệu (Input Area) được thiết kế bo tròn, hiện đại, tách biệt với phần nội dung chat.

### 3.4. Timeline đồ án (Kế hoạch thực hiện)

**Thời gian thực hiện:** 18/09/2025 - 23/12/2025

#### Giai đoạn 1: Lên ý tưởng đồ án (18/09 - 24/09/2025)

| Công việc                     | Người thực hiện     | Thời gian     | Trạng thái   | Commit liên quan         |
| ----------------------------- | ------------------- | ------------- | ------------ | ------------------------ |
| Lên ý tưởng đồ án             | Cả nhóm             | 18/09 - 20/09 | ✅ Hoàn thành | -                        |
| Tạo repository GitHub         | Nguyễn Duy Quốc Bảo | 10/09         | ✅ Hoàn thành | `efc9a5b` Initial commit |
| Tìm hiểu các tính năng        | Cả nhóm             | 22/09 - 23/09 | ✅ Hoàn thành | -                        |
| Chọn các tính năng triển khai | Cả nhóm             | 24/09         | ✅ Hoàn thành | -                        |

#### Giai đoạn 2: Thiết kế hệ thống (07/10 - 13/10/2025)

| Công việc                   | Người thực hiện     | Thời gian     | Trạng thái   | Commit liên quan                   |
| --------------------------- | ------------------- | ------------- | ------------ | ---------------------------------- |
| Thiết kế kiến trúc hệ thống | Cả nhóm             | 07/10 - 10/10 | ✅ Hoàn thành | -                                  |
| Phân tích & thiết kế CSDL   | Cả nhóm             | 07/10 - 10/10 | ✅ Hoàn thành | -                                  |
| Sơ đồ Use Case              | Nguyễn Trường Duy   | 10/10 - 11/10 | ✅ Hoàn thành | -                                  |
| Xây dựng Firebase Database  | Cả nhóm             | 11/10 - 12/10 | ✅ Hoàn thành | `bf297e1` Khởi tạo firebase_helper |
| Thiết kế luồng ứng dụng     | Nguyễn Duy Quốc Bảo | 11/10 - 13/10 | ✅ Hoàn thành | `6fe9b4e` Khởi tạo các layout mẫu  |

#### Giai đoạn 3: Thiết kế giao diện (09/10 - 05/11/2025)

| Công việc                          | Người thực hiện     | Thời gian     | Trạng thái   | Commit liên quan                              |
| ---------------------------------- | ------------------- | ------------- | ------------ | --------------------------------------------- |
| Thống nhất palette màu (Dark Mode) | Cả nhóm             | 09/10 - 05/11 | ✅ Hoàn thành | Lấy cảm hứng từ Netflix UI                    |
| Giao diện Đăng nhập/Đăng ký        | Nguyễn Duy Quốc Bảo | 09/10 - 13/10 | ✅ Hoàn thành | `layouts_1_login.xml`, `layouts_1_signup.xml` |
| Giao diện Trang chủ (Home)         | Nguyễn Trường Duy   | 09/10 - 13/10 | ✅ Hoàn thành | `layouts_fragments_home.xml`                  |
| Giao diện Hồ sơ cá nhân            | Nguyễn Duy Quốc Bảo | 09/10 - 13/10 | ✅ Hoàn thành | `layouts_fragments_user.xml`                  |
| Giao diện Chi tiết phim            | Nguyễn Trường Duy   | 09/10 - 13/10 | ✅ Hoàn thành | `parthome_movie_details.xml`                  |
| Giao diện Chọn ghế/Suất chiếu      | Võ Minh Dương       | 09/10 - 13/10 | ✅ Hoàn thành | `parthome_seat_selection.xml`                 |
| Giao diện Thanh toán               | Võ Minh Dương       | 09/10 - 13/10 | ✅ Hoàn thành | `activity_payment.xml`                        |

#### Giai đoạn 4: Thực hiện đồ án (13/10 - 20/12/2025)

**Module Xác thực người dùng (Authentication)**

| Công việc                         | Người thực hiện     | Thời gian     | Trạng thái   | Commit liên quan                                        |
| --------------------------------- | ------------------- | ------------- | ------------ | ------------------------------------------------------- |
| Đăng nhập/Đăng ký/Quên mật khẩu   | Nguyễn Duy Quốc Bảo | 13/10 - 18/10 | ✅ Hoàn thành | `7d7b8da` Đã code chức năng đăng nhập, đăng ký, quên MK |
| API xác thực Firebase Auth        | Nguyễn Duy Quốc Bảo | 13/10 - 18/10 | ✅ Hoàn thành | `73d7a4f` Code chức năng màn hình chờ, đăng nhập        |
| Gửi lại email xác minh            | Nguyễn Duy Quốc Bảo | 18/10         | ✅ Hoàn thành | `c88477b` Thêm chức năng gửi lại email xác minh         |
| Thay đổi mật khẩu & Xóa tài khoản | Nguyễn Duy Quốc Bảo | 26/10         | ✅ Hoàn thành | `0574df9` Thêm tính năng thay đổi mật khẩu, xóa TK      |

**Module Trang chủ & Xem phim**

| Công việc                     | Người thực hiện     | Thời gian     | Trạng thái   | Commit liên quan                                  |
| ----------------------------- | ------------------- | ------------- | ------------ | ------------------------------------------------- |
| Movie Slider (Banner)         | Nguyễn Trường Duy   | 22/10 - 23/10 | ✅ Hoàn thành | `dc95a78` feat: added Movie slider for menu       |
| Converted Slider to Fragment  | Nguyễn Trường Duy   | 07/11         | ✅ Hoàn thành | `536e8f9` Converted Slider Activity to a Fragment |
| Top Movies & View All         | Võ Minh Dương       | 22/10 - 25/10 | ✅ Hoàn thành | `358ddaf` thêm phần top movie                     |
| Upcoming Movies               | Võ Minh Dương       | 07/11         | ✅ Hoàn thành | `e54e23c` them upcomming                          |
| Chi tiết phim (Movie Details) | Nguyễn Trường Duy   | 23/10 - 24/10 | ✅ Hoàn thành | `5c51c43` feat: Movie Details                     |
| Tính năng tìm kiếm phim       | Nguyễn Duy Quốc Bảo | 05/11         | ✅ Hoàn thành | `7f4d0a9` fix: hoàn thiện tính năng tìm kiếm phim |

**Module Cá nhân hóa người dùng (User Profile)**

| Công việc                             | Người thực hiện     | Thời gian     | Trạng thái   | Commit liên quan                                           |
| ------------------------------------- | ------------------- | ------------- | ------------ | ---------------------------------------------------------- |
| Chỉnh sửa thông tin cá nhân           | Nguyễn Duy Quốc Bảo | 15/10 - 26/10 | ✅ Hoàn thành | `2d4eea3` Thêm tính năng chỉnh sửa thông tin người dùng    |
| Ngày sinh & Giới tính user            | Nguyễn Duy Quốc Bảo | 06/12         | ✅ Hoàn thành | `6b934c0` feat: thêm trường thông tin ngày sinh, giới tính |
| Thông tin bổ sung (Movie Preferences) | Nguyễn Duy Quốc Bảo | 11/12         | ✅ Hoàn thành | `adc409b` feat: thêm trường thông tin bổ sung cho user     |
| Verify số điện thoại OTP              | Nguyễn Duy Quốc Bảo | 10/12         | ✅ Hoàn thành | `8f34b67` feat: thêm tính năng verify số điện thoại        |
| Thông báo khi sửa thông tin           | Võ Minh Dương       | 13/12         | ✅ Hoàn thành | `9273358` feat: thêm thông báo khi sửa thông tin cá nhân   |

**Module Đặt vé & Thanh toán**

| Công việc                           | Người thực hiện   | Thời gian     | Trạng thái   | Commit liên quan                                    |
| ----------------------------------- | ----------------- | ------------- | ------------ | --------------------------------------------------- |
| Giao diện chọn suất chiếu, chỗ ngồi | Võ Minh Dương     | 20/10 - 07/11 | ✅ Hoàn thành | `d6a874f` thêm phần đặt chỗ                         |
| Logic đặt vé & kiểm tra ghế         | Võ Minh Dương     | 07/11         | ✅ Hoàn thành | `1c51b5f` thêm phần đặt vé                          |
| Cập nhật chi tiết mua vé            | Võ Minh Dương     | 07/11         | ✅ Hoàn thành | `e22b10e` cập nhập phần mua vé cho chi tiết         |
| VNPay Payment System                | Nguyễn Trường Duy | 16/12         | ✅ Hoàn thành | `db11952` feat: VNPay payment system                |
| Thanh toán bằng số dư               | Võ Minh Dương     | 18/12         | ✅ Hoàn thành | `8ad9feb` mua ve bang so du                         |
| Load poster URL cho Payment         | Nguyễn Trường Duy | 20/12         | ✅ Hoàn thành | `3090167` fix: Load poster URL for Payment activity |

**Module Hủy vé & Hoàn tiền**

| Công việc                   | Người thực hiện | Thời gian | Trạng thái   | Commit liên quan               |
| --------------------------- | --------------- | --------- | ------------ | ------------------------------ |
| Quản lý danh sách vé đã đặt | Võ Minh Dương   | 17/12     | ✅ Hoàn thành | `c46084a` thêm phan list vé    |
| Chức năng Refund            | Võ Minh Dương   | 18/12     | ✅ Hoàn thành | `83e3809` refund               |
| Thông báo khi hoàn tiền     | Võ Minh Dương   | 18/12     | ✅ Hoàn thành | `c4e87b8` thông báo khi refund |

**Module Chatbot AI**

| Công việc                                | Người thực hiện     | Thời gian | Trạng thái   | Commit liên quan                                            |
| ---------------------------------------- | ------------------- | --------- | ------------ | ----------------------------------------------------------- |
| Thiết kế giao diện Chatbot               | Nguyễn Duy Quốc Bảo | 12/12     | ✅ Hoàn thành | `c2eeabe` style: thiết kế giao diện cho botchat             |
| Cải thiện giao diện Chatbot              | Nguyễn Duy Quốc Bảo | 12/12     | ✅ Hoàn thành | `4833ce7` style: cải thiện thêm về giao diện cho botchat    |
| Tích hợp API dịch vụ AI (Ollama + ngrok) | Nguyễn Duy Quốc Bảo | 13/12     | ✅ Hoàn thành | `b629fcb` feat: cài đặt api dịch vụ AI vào botchat          |
| Giao diện đa ngôn ngữ cho Chatbot        | Nguyễn Duy Quốc Bảo | 13/12     | ✅ Hoàn thành | `8044108` style: cập nhật giao diện đa ngôn ngữ cho botchat |
| Thêm data cho Chatbot                    | Nguyễn Duy Quốc Bảo | 16/12     | ✅ Hoàn thành | `5de51b1` docs: thêm data cho chatbot                       |
| Gửi thông tin user đến server Chatbot    | Nguyễn Duy Quốc Bảo | 17/12     | ✅ Hoàn thành | `97be6e5` feat: gửi thông tin của bản thân user đến botchat |
| Cập nhật Chatbot API mới                 | Nguyễn Duy Quốc Bảo | 20/12     | ✅ Hoàn thành | `22c539a` fix: cập nhật lại chatbotapi                      |

**Module Tính năng nâng cao**

| Công việc                 | Người thực hiện     | Thời gian | Trạng thái   | Commit liên quan                                            |
| ------------------------- | ------------------- | --------- | ------------ | ----------------------------------------------------------- |
| Âm thanh cho ứng dụng     | Nguyễn Duy Quốc Bảo | 05/11     | ✅ Hoàn thành | `a91f61f` feat: đã thêm âm thanh vào trong ứng dụng         |
| Bật/tắt chế độ âm thanh   | Nguyễn Duy Quốc Bảo | 05/11     | ✅ Hoàn thành | `aa11c53` feat: thêm tính năng bật tắt chế độ âm thanh      |
| Dark Mode cho ứng dụng    | Nguyễn Duy Quốc Bảo | 26/10     | ✅ Hoàn thành | `0a0d392` Thêm tính năng sáng tối cho ứng dụng              |
| Đa ngôn ngữ (6 ngôn ngữ)  | Nguyễn Duy Quốc Bảo | 10/12     | ✅ Hoàn thành | `e0a7047` feat: thêm chức năng thay đổi ngôn ngữ cho app    |
| Thêm các ngôn ngữ bổ sung | Nguyễn Duy Quốc Bảo | 10/12     | ✅ Hoàn thành | `8bb95b8` feat: thêm 1 số ngôn ngữ cần thiết khác           |
| Kiểm tra kết nối mạng     | Nguyễn Duy Quốc Bảo | 14/12     | ✅ Hoàn thành | `7596abb` feat: kiểm tra kết nối mạng trước khi vào app     |
| Khóa mã PIN 6 số          | Nguyễn Duy Quốc Bảo | 17/12     | ✅ Hoàn thành | `31dc319` feat: thêm khóa mã pin cho ứng dụng trước khi vào |

#### Giai đoạn 5: Kiểm tra & Tối ưu (19/12 - 21/12/2025)

| Công việc                                | Người thực hiện     | Thời gian     | Trạng thái   | Commit liên quan                                          |
| ---------------------------------------- | ------------------- | ------------- | ------------ | --------------------------------------------------------- |
| Sửa lỗi giao diện ngôn ngữ               | Nguyễn Duy Quốc Bảo | 10/12         | ✅ Hoàn thành | `9445af4` fix: sửa lỗi giao diện về ngôn ngữ và nút bấm   |
| Fix lỗi giao diện chồng lấp edit profile | Nguyễn Duy Quốc Bảo | 06/12         | ✅ Hoàn thành | `338ae33` fix: fix lỗi giao diện chồng lấp ở edit profile |
| Refactor tên file theo chức năng         | Nguyễn Duy Quốc Bảo | 09/12         | ✅ Hoàn thành | `ab1d505` refactor: đổi tên lại các file mã nguồn         |
| Xóa file không cần thiết                 | Nguyễn Duy Quốc Bảo | 13/12         | ✅ Hoàn thành | `33a087d` chore: xóa các file và thư mục không cần thiết  |
| Kiểm thử UI/UX                           | Cả nhóm             | 19/12 - 21/12 | ✅ Hoàn thành | -                                                         |

#### Giai đoạn 6: Hoàn thành & Báo cáo (19/12 - 23/12/2025)

| Công việc                      | Người thực hiện     | Thời gian     | Trạng thái   | Commit liên quan                                            |
| ------------------------------ | ------------------- | ------------- | ------------ | ----------------------------------------------------------- |
| Thêm document kỹ thuật         | Nguyễn Duy Quốc Bảo | 06/12         | ✅ Hoàn thành | `caa32b7` docs: thêm document kỹ thuật cho dự án            |
| Viết báo cáo tổng kết ứng dụng | Nguyễn Duy Quốc Bảo | 19/12         | ✅ Hoàn thành | `63eb03f` docs: viết báo cáo tổng kết ứng dụng              |
| Merge final PR (ReviewRatings) | Nguyễn Trường Duy   | 20/12         | ✅ Hoàn thành | `c89cbd3` Merge pull request #8 from baongdqu/ReviewRatings |
| Chuẩn bị slide thuyết trình    | Cả nhóm             | 21/12 - 23/12 | ✅ Hoàn thành | -                                                           |

#### Thống kê đóng góp (Commits)

| Thành viên          | GitHub Username | Số commits | Vai trò chính                                                    |
| ------------------- | --------------- | ---------- | ---------------------------------------------------------------- |
| Nguyễn Duy Quốc Bảo | baongdqu        | ~55+       | Auth, User Profile, Chatbot AI, Đa ngôn ngữ, PIN Lock, Báo cáo   |
| Nguyễn Trường Duy   | duyimew         | ~15+       | Movie Slider, Movie Details, VNPay Payment, Merge PRs            |
| Võ Minh Dương       | vominhduong     | ~12+       | Đặt vé, Chọn ghế, Thanh toán số dư, Refund, Top Movies, Upcoming |

---

## Chương 4: Hiện thực đề tài

### 4.1. Môi trường cài đặt

Để phát triển và vận hành ứng dụng, nhóm đã thiết lập môi trường phát triển như sau:

- **Hệ điều hành:** Microsoft Windows 10/11 (64-bit).
- **Công cụ lập trình (IDE):** Android Studio (Phiên bản ổn định mới nhất, ví dụ: Koala/Ladybug).
- **Ngôn ngữ lập trình:** Java (JDK 11) kết hợp với Kotlin cho một số module cấu hình.
- **Android SDK:**
  - **Minimum SDK:** 24 (Android 7.0 Nougat) - Đảm bảo hỗ trợ rộng rãi các thiết bị cũ.
  - **Target SDK:** 34 (Android 14) - Tối ưu hóa cho các thiết bị mới nhất, tuân thủ các quy định bảo mật mới của Google Play.
  - **Compile SDK:** 36.
- **Các thư viện chính:** Firebase BoM, OkHttp 4.12, Glide 4.16, ExoPlayer 2.19, Gson 2.8.5.

### 4.2. Cấu trúc project

Dự án được tổ chức theo cấu trúc chuẩn của Android Gradle Plugin, bao gồm các module chính:

- **`manifests` (AndroidManifest.xml):** Tệp cấu hình quan trọng nhất, khai báo các Activity, quyền truy cập (Internet, Storage), và các cấu hình hệ thống khác.
- **`java` (Source Code):** Chứa mã nguồn logic của ứng dụng, được tổ chức theo package `com.example.app_movie_booking_ticket`:
  - **Gốc (Activities):** Chứa các màn hình chính như `activities_1_login` (Đăng nhập), `activities_2_chatbot` (Chatbot), `PaymentActivity` (Thanh toán), `parthome_SeatSelectionActivity` (Chọn ghế)...
  - **`fragments`:** Chứa các Fragment đại diện cho các tab chức năng: `fragments_home` (Trang chủ), `fragments_user` (Cá nhân).
  - **`adapter`:** Chứa các bộ chuyển đổi dữ liệu (`TicketAdapter`, `SliderAdapter`...) để hiển thị danh sách lên RecyclerView/ViewPager.
  - **`model`:** Khai báo các đối tượng dữ liệu như `Movie` (Phim), `User` (Người dùng), `TicketSimple` (Vé).
  - **`extra`:** Chứa các lớp tiện ích hỗ trợ: `extra_firebase_helper` (Xử lý DB), `extra_sound_manager` (Âm thanh), `extra_language_helper` (Đa ngôn ngữ).
- **`res` (Resources):**
  - **`layout`:** Cá tệp XML định nghĩa giao diện người dùng (UI).
  - **`drawable`:** Chứa hình ảnh, icon, file vector.
  - **`values`:** Chứa các biến hằng số như màu sắc (`colors.xml`), chuỗi ký tự (`strings.xml`), kích thước (`dimens.xml`).

### 4.3. Các màn hình chính (Demo sản phẩm)

#### 4.3.1. Màn hình Splash Screen và Đăng nhập

Khi khởi động, ứng dụng hiển thị màn hình chờ (Splash Screen) để kiểm tra kết nối mạng. Sau đó, màn hình Đăng nhập xuất hiện với giao diện hiện đại, tông màu tối. Người dùng có thể đăng nhập bằng Email/Password, hoặc chọn Đăng ký nếu chưa có tài khoản.
_(Hình 4.1: Giao diện màn hình Đăng nhập)_

#### 4.3.2. Màn hình Home (Danh sách phim)

Đây là màn hình chính của ứng dụng.

- **Banner Slide:** Phía trên cùng là slide ảnh các phim nổi bật, tự động chuyển động.
- **Danh sách phim:** Bên dưới là các danh sách phim cuộn ngang: "Top Movies" và "Upcoming Movies".
- **Thanh tìm kiếm:** Cho phép người dùng nhập tên phim để tìm kiếm nhanh chóng.
  _(Hình 4.2: Giao diện màn hình Trang chủ và Tìm kiếm phim)_

#### 4.3.3. Màn hình Chi tiết phim và Trailer

Khi chọn một phim, người dùng xem được thông tin chi tiết:

- Poster, Tên phim, Thể loại, Thời lượng, Điểm đánh giá.
- **Trailer:** Tích hợp trình phát video ExoPlayer cho phép xem trailer ngay trong ứng dụng.
- Danh sách diễn viên (Cast) và các hình ảnh liên quan.
  _(Hình 4.3: Giao diện chi tiết phim và phát Trailer)_

#### 4.3.4. Màn hình Chọn ghế và Thanh toán

Sau khi nhấn "Đặt vé":

- **Chọn ghế:** Người dùng chọn ngày, giờ và vị trí ghế trên sơ đồ 8 cột. Ghế đã chọn sẽ đổi màu để xác nhận.
- **Thanh toán:** Hiển thị hóa đơn chi tiết (Tên phim, suất chiếu, số ghế, tổng tiền). Người dùng chọn phương thức thanh toán (VNPay hoặc Số dư) để hoàn tất.
  _(Hình 4.4: Giao diện Chọn ghế và Màn hình Thanh toán)_

#### 4.3.5. Màn hình Trang cá nhân (User Profile)

Nơi quản lý thông tin tài khoản:

- **Thông tin chung:** Hiển thị Avatar khổ lớn (có viền đỏ Netflix) và tên người dùng.
- **Chức năng:** Cho phép chỉnh sửa hồ sơ, đổi mật khẩu, xem lịch sử đặt vé và cài đặt ứng dụng.
- **Đăng xuất:** Nút đăng xuất an toàn ở cuối trang.
  _(Hình 4.5: Giao diện Trang cá nhân)_

#### 4.3.6. Màn hình Thông báo (Notifications)

Tab thông báo giúp người dùng cập nhật tin tức mới nhất:

- Danh sách các thông báo về khuyến mãi, xác nhận đặt vé thành công hoặc phim mới ra mắt.
- Các thông báo được hiển thị dưới dạng danh sách trực quan, dễ theo dõi.
  _(Hình 4.6: Giao diện Thông báo)_

#### 4.3.7. Màn hình Chatbot AI

Tính năng trợ lý ảo thông minh:

- Giao diện chat thân thiện như Messenger.
- Người dùng có thể hỏi "Phim nào hay đang chiếu?", bot sẽ trả lời dựa trên dữ liệu phim hiện có.
- Hỗ trợ các câu trả lời nhanh (Quick Replies).
  _(Hình 4.7: Giao diện Chatbot tư vấn phim)_

#### 4.3.8. Màn hình Đăng ký tài khoản

Cho phép người dùng mới tạo tài khoản:

- Nhập thông tin: Họ tên, Email, Mật khẩu và Xác nhận mật khẩu.
- Validation kiểm tra định dạng email, độ dài mật khẩu tối thiểu.
- Sau khi đăng ký, hệ thống gửi email xác thực đến địa chỉ email đã đăng ký.
  _(Hình 4.8: Giao diện màn hình Đăng ký)_

#### 4.3.9. Màn hình Quên mật khẩu

Hỗ trợ người dùng khôi phục tài khoản:

- Nhập địa chỉ email đã đăng ký.
- Hệ thống gửi link reset mật khẩu qua Firebase Authentication.
- Thông báo thành công hoặc lỗi (email không tồn tại).
  _(Hình 4.9: Giao diện màn hình Quên mật khẩu)_

#### 4.3.10. Màn hình Lịch sử đặt vé

Hiển thị danh sách các vé đã mua:

- Thông tin mỗi vé: Poster phim, tên phim, ngày chiếu, vị trí ghế, tổng tiền.
- Sắp xếp theo thời gian (vé mới nhất hiển thị trước).
- Có thể xem chi tiết từng vé.
  _(Hình 4.10: Giao diện Lịch sử đặt vé)_

#### 4.3.11. Màn hình Chỉnh sửa hồ sơ

Cho phép người dùng cập nhật thông tin cá nhân:

- Thay đổi Avatar: Chụp ảnh mới hoặc chọn từ thư viện.
- Cập nhật Họ tên hiển thị.
- Xem email tài khoản (không cho phép thay đổi).
- Nút Lưu thay đổi và Hủy.
  _(Hình 4.11: Giao diện Chỉnh sửa hồ sơ)_

#### 4.3.12. Màn hình Đổi mật khẩu

Cho phép người dùng thay đổi mật khẩu:

- Nhập mật khẩu hiện tại để xác thực.
- Nhập mật khẩu mới và xác nhận mật khẩu mới.
- Kiểm tra độ khớp giữa mật khẩu mới và xác nhận.
  _(Hình 4.12: Giao diện Đổi mật khẩu)_

#### 4.3.13. Màn hình Sở thích phim

Cá nhân hóa trải nghiệm người dùng:

- Chọn các thể loại phim yêu thích (Hành động, Kinh dị, Hài, Lãng mạn...).
- Chọn thời gian xem phim ưa thích (Buổi sáng, Buổi chiều, Buổi tối).
- Dữ liệu được lưu vào Firebase và sử dụng bởi Chatbot AI để gợi ý phim phù hợp.
  _(Hình 4.13: Giao diện Sở thích phim)_

#### 4.3.14. Màn hình Cài đặt nâng cao

Quản lý các thiết lập ứng dụng:

- **Chế độ tối (Dark Mode):** Bật/tắt giao diện tối.
- **Âm thanh:** Bật/tắt âm thanh phản hồi khi thao tác.
- **Ngôn ngữ:** Chuyển đổi giữa 6 ngôn ngữ (Việt, Anh, Nhật, Hàn, Nga, Trung).
- **Khóa ứng dụng (PIN Lock):** Bật/tắt và thiết lập mã PIN 6 chữ số.
  _(Hình 4.14: Giao diện Cài đặt nâng cao)_

#### 4.3.15. Màn hình Khóa ứng dụng (PIN Lock)

Bảo vệ quyền riêng tư người dùng:

- Hiển thị khi mở ứng dụng từ nền (nếu đã bật PIN Lock).
- Giao diện nhập 6 chữ số với bàn phím số.
- Tính năng "Quên PIN" cho phép reset bằng cách đăng xuất tài khoản.
- Phản hồi bằng màu sắc khi nhập đúng/sai.
  _(Hình 4.15: Giao diện Khóa ứng dụng)

### 4.4. Đoạn code nổi bật

Dưới đây là một số đoạn mã nguồn tiêu biểu thể hiện các kỹ thuật chính được sử dụng trong dự án.

#### 4.4.1. Tải dữ liệu phim từ Firebase Realtime Database

Đoạn code dưới đây minh họa cách ứng dụng lắng nghe sự thay đổi dữ liệu theo thời gian thực từ Firebase để cập nhật danh sách phim lên giao diện.

```java
// File: fragments_home.java
private void loadTopMovies() {
    DatabaseReference movieRef = FirebaseDatabase.getInstance().getReference("Items");

    movieRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            movieListTop.clear();
            for (DataSnapshot itemSnap : snapshot.getChildren()) {
                Movie movie = itemSnap.getValue(Movie.class);
                if (movie != null) {
                    movieListTop.add(movie);
                }
            }
            // Sắp xếp theo điểm IMDb giảm dần
            Collections.sort(movieListTop, (m1, m2) ->
                Double.compare(m2.getImdb(), m1.getImdb()));
            topMovieAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("Firebase", "Error loading movies: " + error.getMessage());
        }
    });
}
```

#### 4.4.2. Xử lý logic Chọn/Bỏ chọn ghế ngồi

Hàm `toggleSeat` được gọi khi người dùng tap vào một ghế trên sơ đồ. Nó cập nhật trạng thái ghế và tính toán lại tổng tiền.

```java
// File: parthome_SeatSelectionActivity.java
private List<String> selectedSeats = new ArrayList<>();
private int pricePerSeat = 0;

private void toggleSeat(Button seatBtn, String seatName) {
    if (selectedSeats.contains(seatName)) {
        // Nếu ghế đã được chọn -> bỏ chọn
        selectedSeats.remove(seatName);
        seatBtn.setSelected(false);
    } else {
        // Nếu ghế chưa chọn -> thêm vào danh sách
        selectedSeats.add(seatName);
        seatBtn.setSelected(true);
    }
    // Cập nhật tổng tiền theo thời gian thực
    int totalPrice = selectedSeats.size() * pricePerSeat;
    tvTotalPrice.setText(String.format(getString(R.string.price_format),
        String.valueOf(totalPrice)));
}
```

#### 4.4.3. Gọi API Chatbot AI với OkHttp

Đoạn code sau cho thấy cách ứng dụng gửi tin nhắn của người dùng đến AI Server (thông qua ngrok) và xử lý phản hồi bất đồng bộ.

```java
// File: extra_gemini_cli_helper.java
public void sendMessage(String userMessage, List<ChatMessage> history,
                        ChatCallback callback) {
    try {
        // Xây dựng request body dạng JSON
        JSONObject requestBody = new JSONObject();
        requestBody.put("message", userMessage);
        requestBody.put("system_prompt",
            "Bạn là trợ lý ảo của rạp chiếu phim. Hãy trả lời thân thiện.");

        // Tạo HTTP Request
        Request request = new Request.Builder()
                .url(serverUrl + "/chat")
                .post(RequestBody.create(requestBody.toString(), JSON))
                .addHeader("Content-Type", "application/json")
                .build();

        // Gọi API bất đồng bộ
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)
                    throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    String text = parseResponse(responseBody);
                    // Gọi callback trên Main Thread để cập nhật UI
                    mainHandler.post(() -> callback.onSuccess(text));
                } else {
                    mainHandler.post(() -> callback.onError("Lỗi: " + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mainHandler.post(() -> callback.onError("Lỗi kết nối: " + e.getMessage()));
            }
        });
    } catch (Exception e) {
        callback.onError("Lỗi tạo request: " + e.getMessage());
    }
}
```

#### 4.4.4. Xác thực người dùng với Firebase Authentication

Logic đăng nhập với Email/Password sử dụng Firebase Authentication và kiểm tra trạng thái xác minh email.

```java
// File: activities_1_login.java
private void loginUser(String email, String password) {
    mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null && user.isEmailVerified()) {
                    // Đăng nhập thành công, chuyển đến màn hình chính
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else if (user != null) {
                    // Yêu cầu xác minh email
                    Toast.makeText(this, "Vui lòng xác minh email.", Toast.LENGTH_LONG).show();
                    user.sendEmailVerification();
                }
            } else {
                // Đăng nhập thất bại
                Toast.makeText(this, "Sai email hoặc mật khẩu.", Toast.LENGTH_LONG).show();
            }
        });
}
```

---

## Chương 5: Kiểm thử đề tài

### 5.1. Phương pháp kiểm thử

Nhóm đã áp dụng phương pháp **Kiểm thử hộp đen (Black-box Testing)**, tập trung vào việc xác minh các chức năng của ứng dụng hoạt động đúng theo yêu cầu mà không cần quan tâm đến cấu trúc mã nguồn bên trong. Các bước kiểm thử được thực hiện thủ công trên cả thiết bị thật (Samsung Galaxy A52) và trình giả lập Android Studio.

### 5.2. Các trường hợp kiểm thử (Test Cases)

#### 5.2.1. Kiểm thử chức năng Đăng nhập / Đăng ký

| ID    | Mô tả                             | Bước thực hiện                                         | Kết quả mong đợi                                  | Kết quả thực tế    | Trạng thái |
| ----- | --------------------------------- | ------------------------------------------------------ | ------------------------------------------------- | ------------------ | ---------- |
| TC-01 | Đăng nhập thành công              | 1. Nhập email và mật khẩu hợp lệ. 2. Nhấn "Đăng nhập". | Chuyển đến màn hình Home.                         | Đúng như mong đợi. | ✅ Đạt      |
| TC-02 | Đăng nhập với sai mật khẩu        | 1. Nhập email đúng, mật khẩu sai. 2. Nhấn "Đăng nhập". | Toast thông báo "Sai email hoặc mật khẩu".        | Đúng như mong đợi. | ✅ Đạt      |
| TC-03 | Đăng nhập với email chưa xác minh | 1. Nhập email chưa verify. 2. Nhấn "Đăng nhập".        | Toast thông báo yêu cầu xác minh email.           | Đúng như mong đợi. | ✅ Đạt      |
| TC-04 | Đăng nhập với trường rỗng         | 1. Để trống email hoặc mật khẩu. 2. Nhấn "Đăng nhập".  | Toast thông báo "Vui lòng nhập đầy đủ thông tin". | Đúng như mong đợi. | ✅ Đạt      |
| TC-05 | Đăng ký tài khoản mới             | 1. Điền đầy đủ thông tin. 2. Nhấn "Đăng ký".           | Tạo tài khoản thành công, gửi email xác minh.     | Đúng như mong đợi. | ✅ Đạt      |

#### 5.2.2. Kiểm thử chức năng Tìm kiếm và Xem phim

| ID    | Mô tả                     | Bước thực hiện                                     | Kết quả mong đợi                                         | Kết quả thực tế    | Trạng thái |
| ----- | ------------------------- | -------------------------------------------------- | -------------------------------------------------------- | ------------------ | ---------- |
| TC-06 | Tìm kiếm phim theo tên    | 1. Nhấn icon tìm kiếm. 2. Nhập từ khóa "Avengers". | Hiển thị danh sách phim có chứa "Avengers".              | Đúng như mong đợi. | ✅ Đạt      |
| TC-07 | Tìm kiếm không có kết quả | 1. Nhập từ khóa không tồn tại.                     | Hiển thị danh sách rỗng hoặc thông báo "Không tìm thấy". | Đúng như mong đợi. | ✅ Đạt      |
| TC-08 | Xem chi tiết phim         | 1. Chọn một phim từ danh sách.                     | Chuyển đến màn hình chi tiết, hiển thị đầy đủ thông tin. | Đúng như mong đợi. | ✅ Đạt      |
| TC-09 | Phát Trailer phim         | 1. Vào chi tiết phim. 2. Nhấn nút "Trailer".       | Video trailer được phát bằng ExoPlayer.                  | Đúng như mong đợi. | ✅ Đạt      |

#### 5.2.3. Kiểm thử chức năng Đặt vé và Thanh toán

| ID    | Mô tả                      | Bước thực hiện                                                   | Kết quả mong đợi                                  | Kết quả thực tế    | Trạng thái |
| ----- | -------------------------- | ---------------------------------------------------------------- | ------------------------------------------------- | ------------------ | ---------- |
| TC-10 | Chọn ngày và suất chiếu    | 1. Vào màn hình chọn ghế. 2. Chọn ngày và giờ chiếu.             | Sơ đồ ghế được tải tương ứng.                     | Đúng như mong đợi. | ✅ Đạt      |
| TC-11 | Chọn và bỏ chọn ghế        | 1. Tap vào ghế trống. 2. Tap lại ghế đã chọn.                    | Ghế đổi màu khi chọn/bỏ chọn. Tổng tiền cập nhật. | Đúng như mong đợi. | ✅ Đạt      |
| TC-12 | Chọn ghế đã được đặt trước | 1. Tap vào ghế có màu xám (đã đặt).                              | Không thể chọn ghế đó, không có phản hồi.         | Đúng như mong đợi. | ✅ Đạt      |
| TC-13 | Thanh toán thành công      | 1. Chọn ghế. 2. Nhấn "Tiếp tục". 3. Chọn phương thức thanh toán. | Chuyển đến màn hình xác nhận, lưu vé vào lịch sử. | Đúng như mong đợi. | ✅ Đạt      |

#### 5.2.4. Kiểm thử chức năng Bảo mật (PIN Lock)

| ID    | Mô tả                          | Bước thực hiện                                                      | Kết quả mong đợi                               | Kết quả thực tế    | Trạng thái |
| ----- | ------------------------------ | ------------------------------------------------------------------- | ---------------------------------------------- | ------------------ | ---------- |
| TC-14 | Thiết lập mã PIN mới           | 1. Vào Cài đặt > Khóa ứng dụng. 2. Bật tính năng. 3. Nhập PIN 6 số. | PIN được lưu và kích hoạt.                     | Đúng như mong đợi. | ✅ Đạt      |
| TC-15 | Mở khóa ứng dụng bằng PIN đúng | 1. Đóng và mở lại ứng dụng. 2. Nhập đúng PIN.                       | Unlock thành công, vào app bình thường.        | Đúng như mong đợi. | ✅ Đạt      |
| TC-16 | Nhập sai PIN nhiều lần         | 1. Nhập sai PIN 3 lần liên tiếp.                                    | Hiển thị cảnh báo hoặc tạm khóa nhập.          | Đúng như mong đợi. | ✅ Đạt      |
| TC-17 | Quên PIN / Reset PIN           | 1. Nhấn "Quên PIN".                                                 | Xóa PIN cũ, yêu cầu đăng nhập lại để xác minh. | Đúng như mong đợi. | ✅ Đạt      |

#### 5.2.5. Kiểm thử AI Chatbot

| ID    | Mô tả                       | Bước thực hiện                                 | Kết quả mong đợi                           | Kết quả thực tế    | Trạng thái |
| ----- | --------------------------- | ---------------------------------------------- | ------------------------------------------ | ------------------ | ---------- |
| TC-18 | Gửi tin nhắn hỏi gợi ý phim | 1. Mở Chatbot. 2. Nhập "Gợi ý phim hành động". | Bot phản hồi danh sách phim phù hợp.       | Đúng như mong đợi. | ✅ Đạt      |
| TC-19 | Sử dụng Quick Reply         | 1. Mở Chatbot. 2. Tap vào chip "Giá vé".       | Bot tự động trả lời về giá vé.             | Đúng như mong đợi. | ✅ Đạt      |
| TC-20 | Chatbot khi server offline  | 1. Tắt server AI. 2. Gửi tin nhắn.             | Hiển thị thông báo lỗi kết nối thân thiện. | Đúng như mong đợi. | ✅ Đạt      |

#### 5.2.6. Kiểm thử chức năng Cài đặt người dùng (User Settings)

| ID    | Mô tả                            | Bước thực hiện                                                                                  | Kết quả mong đợi                               | Kết quả thực tế    | Trạng thái |
| ----- | -------------------------------- | ----------------------------------------------------------------------------------------------- | ---------------------------------------------- | ------------------ | ---------- |
| TC-21 | Chỉnh sửa hồ sơ cá nhân          | 1. Vào Trang cá nhân > Chỉnh sửa. 2. Thay đổi tên và avatar. 3. Nhấn Lưu.                       | Thông tin được cập nhật và hiển thị đúng.      | Đúng như mong đợi. | ✅ Đạt      |
| TC-22 | Đổi mật khẩu thành công          | 1. Vào Cài đặt > Đổi mật khẩu. 2. Nhập mật khẩu cũ đúng, mật khẩu mới hợp lệ. 3. Nhấn Xác nhận. | Thông báo đổi mật khẩu thành công.             | Đúng như mong đợi. | ✅ Đạt      |
| TC-23 | Đổi mật khẩu với mật khẩu cũ sai | 1. Nhập sai mật khẩu cũ. 2. Nhấn Xác nhận.                                                      | Toast thông báo lỗi "Mật khẩu cũ không đúng".  | Đúng như mong đợi. | ✅ Đạt      |
| TC-24 | Thay đổi ngôn ngữ ứng dụng       | 1. Vào Cài đặt > Ngôn ngữ. 2. Chọn "English".                                                   | Giao diện chuyển sang tiếng Anh.               | Đúng như mong đợi. | ✅ Đạt      |
| TC-25 | Cập nhật sở thích phim           | 1. Vào Sở thích phim. 2. Chọn thể loại yêu thích (Hành động, Kinh dị...). 3. Lưu.               | Thông tin sở thích được lưu vào Firebase.      | Đúng như mong đợi. | ✅ Đạt      |
| TC-26 | Đăng xuất tài khoản              | 1. Nhấn nút "Đăng xuất" trên trang cá nhân. 2. Xác nhận.                                        | Xóa phiên đăng nhập, chuyển về màn hình Login. | Đúng như mong đợi. | ✅ Đạt      |

### 5.3. Kết quả kiểm thử và Bảng tổng hợp lỗi

#### 5.3.1. Tổng quan kết quả

- **Tổng số Test Cases:** 26
- **Số lượng Đạt (Pass):** 26
- **Số lượng Không đạt (Fail):** 0
- **Tỷ lệ thành công:** 100%
- **Ngày kiểm thử cuối cùng:** 21/12/2024

#### 5.3.2. Bảng tổng hợp các lỗi đã phát hiện và khắc phục

| ID     | Mô tả lỗi                                                         | Mức độ     | Ngày phát hiện | Ngày khắc phục | Trạng thái |
| ------ | ----------------------------------------------------------------- | ---------- | -------------- | -------------- | ---------- |
| BUG-01 | Ứng dụng crash khi mất mạng đột ngột trên màn hình Home.          | Cao        | 10/12/2024     | 11/12/2024     | ✅ Đã sửa   |
| BUG-02 | Thiếu thông báo khi người dùng không chọn ghế mà nhấn "Tiếp tục". | Trung bình | 12/12/2024     | 12/12/2024     | ✅ Đã sửa   |
| BUG-03 | Hình ảnh poster bị lỗi tải trên một số thiết bị Android 8.0.      | Thấp       | 14/12/2024     | 15/12/2024     | ✅ Đã sửa   |
| BUG-04 | PIN Lock vẫn được yêu cầu sau khi đăng xuất.                      | Cao        | 16/12/2024     | 17/12/2024     | ✅ Đã sửa   |
| BUG-05 | Chatbot phản hồi chậm trên kết nối 3G.                            | Thấp       | 18/12/2024     | 20/12/2024     | ✅ Đã sửa   |
| BUG-06 | Ứng dụng ANR khi tải danh sách ứng dụng cài đặt.                  | Cao        | 21/12/2024     | 21/12/2024     | ✅ Đã sửa   |
| BUG-07 | PIN Lock không reset khi người dùng chọn "Quên PIN".              | Trung bình | 17/12/2024     | 17/12/2024     | ✅ Đã sửa   |

---

## Chương 6: Kết luận và hướng phát triển

### 6.1. Kết luận

#### 6.1.1. Tổng kết kết quả đạt được so với mục tiêu ban đầu

Sau quá trình nghiên cứu, thiết kế và triển khai, đồ án "Ứng dụng đặt vé xem phim trên nền tảng Android" đã hoàn thành đầy đủ các mục tiêu đề ra ban đầu:

- **Về mặt kỹ thuật:**

  - Xây dựng thành công ứng dụng Android Native sử dụng ngôn ngữ Java, tuân thủ các tiêu chuẩn lập trình hiện đại với ViewBinding và kiến trúc Single Activity - Multiple Fragments.
  - Tích hợp hoàn chỉnh hệ sinh thái Firebase bao gồm Realtime Database (lưu trữ dữ liệu phim, người dùng, vé) và Authentication (xác thực Email/Password với xác minh email).
  - Triển khai thành công module AI Chatbot thông qua việc kết nối với server AI local (Ollama) qua ngrok, sử dụng OkHttp cho các request HTTP bất đồng bộ.

- **Về mặt chức năng:** Ứng dụng đã hiện thực hóa đầy đủ các tính năng cốt lõi:

  - **Quản lý tài khoản:** Đăng ký, Đăng nhập, Quên mật khẩu, Xác minh email, Chỉnh sửa hồ sơ, Đổi mật khẩu, Đăng xuất.
  - **Tra cứu phim:** Hiển thị danh sách phim đang chiếu/sắp chiếu, Tìm kiếm theo tên, Xem chi tiết phim (thông tin, diễn viên, trailer).
  - **Đặt vé:** Chọn ngày/giờ chiếu, Chọn ghế trực quan trên sơ đồ, Tính toán giá vé tự động, Mô phỏng thanh toán (VNPay/Số dư).
  - **Tiện ích nâng cao:** Chatbot tư vấn phim AI, Khóa ứng dụng bằng mã PIN 6 số, Đa ngôn ngữ (Việt, Anh, Nhật, Hàn...), Quản lý sở thích phim.

- **Về mặt trải nghiệm người dùng (UX):**
  - Giao diện được thiết kế theo phong cách "Cinematic Dark Mode" lấy cảm hứng từ Netflix, mang lại cảm giác chuyên nghiệp và phù hợp với ngữ cảnh xem phim.
  - Thời gian phản hồi nhanh, các thao tác chuyển màn hình mượt mà nhờ tối ưu hóa tải ảnh bằng Glide và sử dụng Firebase Realtime Database.
  - Quy trình đặt vé được đơn giản hóa, người dùng có thể hoàn tất từ bước chọn phim đến thanh toán chỉ trong vài phút.

#### 6.1.2. Ưu điểm của ứng dụng

- **Giao diện hiện đại, thẩm mỹ cao:** Sử dụng Material Design Components, tông màu tối kết hợp điểm nhấn đỏ Netflix tạo nên trải nghiệm thị giác cao cấp, phù hợp với xu hướng thiết kế ứng dụng giải trí hiện nay.
- **Đồng bộ dữ liệu thời gian thực:** Nhờ Firebase Realtime Database, mọi thay đổi về phim, suất chiếu, ghế ngồi đều được cập nhật tức thì trên tất cả thiết bị mà không cần refresh thủ công.
- **Tích hợp AI thông minh:** Chatbot hỗ trợ tư vấn phim 24/7, giúp người dùng nhận gợi ý dựa trên sở thích và thể loại mong muốn, nâng cao tính tương tác và cá nhân hóa trải nghiệm.
- **Bảo mật đa lớp:** Kết hợp Firebase Authentication (xác thực email) với tính năng khóa ứng dụng bằng mã PIN, đảm bảo an toàn thông tin cá nhân và lịch sử giao dịch của người dùng.
- **Hỗ trợ đa ngôn ngữ:** Ứng dụng có thể chuyển đổi linh hoạt giữa nhiều ngôn ngữ (Tiếng Việt, Tiếng Anh, Tiếng Nhật, Tiếng Hàn...), thuận tiện cho cả người dùng nước ngoài tại Việt Nam và người Việt sống ở nước ngoài.
- **Phát trailer chất lượng cao:** Tích hợp ExoPlayer cho phép xem trailer phim trực tiếp trong ứng dụng với chất lượng HD, hỗ trợ cả định dạng HLS streaming.
- **Mã nguồn có tổ chức tốt:** Cấu trúc project rõ ràng, phân chia theo chức năng (activities, fragments, adapters, models, extras), dễ dàng bảo trì và mở rộng trong tương lai.

#### 6.1.3. Hạn chế của ứng dụng

- **Chưa tích hợp thanh toán thực:** Hiện tại, quy trình thanh toán chỉ ở mức mô phỏng (demo), chưa kết nối với các cổng thanh toán thực như VNPay, Momo, ZaloPay do yêu cầu đăng ký tài khoản doanh nghiệp và quy trình xét duyệt phức tạp.
- **Phụ thuộc vào server AI local:** Chatbot AI hiện đang chạy trên máy local và expose qua ngrok. Điều này có nghĩa là nếu máy chủ tắt hoặc ngrok hết hạn, tính năng chatbot sẽ không hoạt động. Cần có giải pháp server cloud chạy 24/7.
- **Dữ liệu phim còn hạn chế:** Hiện tại, dữ liệu phim được nhập thủ công vào Firebase. Trong môi trường sản xuất, cần tích hợp với API của các rạp phim thực tế (như CGV, Lotte Cinema) hoặc các nguồn dữ liệu phim lớn như TMDb, IMDb.
- **Chưa có hệ thống thông báo đẩy (Push Notifications):** Người dùng chưa nhận được thông báo nhắc lịch xem phim hoặc các chương trình khuyến mãi, ảnh hưởng đến khả năng giữ chân người dùng quay lại ứng dụng.
- **Chưa hỗ trợ Offline Mode:** Khi mất kết nối Internet, ứng dụng không thể hiển thị dữ liệu phim đã xem trước đó do chưa tận dụng triệt để tính năng Disk Persistence của Firebase.
- **Chỉ hỗ trợ nền tảng Android:** Người dùng iOS hoặc người dùng Web hiện chưa thể trải nghiệm ứng dụng.

### 6.2. Hướng phát triển trong tương lai

Để nâng cao chất lượng sản phẩm và đưa ứng dụng tiến gần hơn đến môi trường vận hành thực tế, nhóm đề xuất các hướng phát triển sau:

#### 6.2.1. Tích hợp thanh toán trực tuyến thực

- **VNPay SDK:** Tích hợp bộ SDK thanh toán của VNPay cho phép người dùng thanh toán qua thẻ ATM nội địa, thẻ tín dụng quốc tế (Visa, MasterCard, JCB) và ví VNPay.
- **Ví điện tử Momo, ZaloPay:** Hỗ trợ thanh toán qua các ví điện tử phổ biến nhất Việt Nam, tận dụng lượng người dùng khổng lồ của các nền tảng này. Quy trình thanh toán sẽ sử dụng Deep Link để chuyển đến ứng dụng ví và callback về app sau khi hoàn tất.
- **Apple Pay / Google Pay:** Dành cho phiên bản iOS và các thiết bị Android hỗ trợ NFC, mang lại trải nghiệm thanh toán một chạm cực kỳ tiện lợi.

#### 6.2.2. Nâng cấp và Cá nhân hóa AI Chatbot

- **Gợi ý phim cá nhân hóa (Recommendation Engine):** Xây dựng thuật toán học máy (Machine Learning) phân tích lịch sử xem phim, sở thích thể loại và hành vi người dùng để đề xuất phim phù hợp nhất cho từng cá nhân.
- **Nhập liệu bằng giọng nói (Voice Input):** Tích hợp Google Speech-to-Text API cho phép người dùng hỏi chatbot bằng giọng nói, nâng cao tính tiện lợi khi đang di chuyển.
- **Hỗ trợ đa ngữ cảnh (Multi-turn Conversation):** Cải thiện khả năng hiểu ngữ cảnh của chatbot để duy trì cuộc hội thoại dài, nhớ các câu hỏi trước đó.
- **Migration lên Cloud AI:** Chuyển từ server local sang các dịch vụ AI Cloud như Google Vertex AI, AWS Bedrock hoặc Azure OpenAI để đảm bảo uptime 99.9% và khả năng mở rộng.

#### 6.2.3. Phát triển đa nền tảng (Cross-Platform)

- **Phiên bản iOS:** Xây dựng ứng dụng iOS Native bằng Swift/SwiftUI hoặc sử dụng framework đa nền tảng như Flutter/React Native để tái sử dụng phần lớn mã nguồn logic.
- **Ứng dụng Web (Progressive Web App - PWA):** Phát triển phiên bản web responsive cho phép người dùng truy cập và đặt vé trực tiếp từ trình duyệt mà không cần cài đặt ứng dụng.
- **Đồng bộ hóa tài khoản đa thiết bị:** Đảm bảo người dùng có thể đăng nhập và tiếp tục phiên làm việc trên bất kỳ thiết bị nào (điện thoại, máy tính bảng, web) một cách liền mạch.

#### 6.2.4. Xây dựng hệ thống Backend chuyên dụng

- **Migrating từ Firebase sang Backend riêng:** Xây dựng hệ thống API server sử dụng Node.js (Express) hoặc Python (FastAPI/Django) để kiểm soát tốt hơn luồng dữ liệu, logic nghiệp vụ phức tạp và tích hợp với các hệ thống của rạp phim thực tế.
- **Quản lý suất chiếu và ghế real-time với WebSocket:** Thay vì chỉ dựa vào Firebase Realtime Database, sử dụng WebSocket để đồng bộ trạng thái ghế ngồi tức thì giữa các người dùng đang cùng đặt vé một suất chiếu, tránh xung đột (double booking).
- **Hệ thống quản trị (Admin Panel):** Xây dựng giao diện web cho phép nhân viên rạp quản lý phim, suất chiếu, giá vé, khuyến mãi và theo dõi báo cáo doanh thu.

#### 6.2.5. Tính năng nâng cao khác

- **Push Notifications:** Tích hợp Firebase Cloud Messaging (FCM) để gửi thông báo nhắc lịch xem phim trước 1-2 giờ, thông báo phim mới ra mắt và các chương trình khuyến mãi cá nhân hóa.
- **Offline Mode:** Lưu cache dữ liệu phim và lịch sử vé đã đặt để người dùng có thể xem lại ngay cả khi mất mạng.
- **Đánh giá và Bình luận phim:** Cho phép người dùng đánh giá sao và viết review sau khi xem phim, tạo cộng đồng chia sẻ trong ứng dụng.
- **Tích hợp bản đồ rạp chiếu:** Sử dụng Google Maps SDK để hiển thị vị trí các rạp chiếu gần nhất và chỉ đường đến rạp.
- **Chương trình khách hàng thân thiết (Loyalty Program):** Tích điểm mỗi lần đặt vé để đổi lấy voucher giảm giá hoặc quà tặng, khuyến khích người dùng quay lại.

---

## Tài liệu tham khảo

### Tài liệu chính thức và Hướng dẫn lập trình

[1] Google, "Android Developers Documentation," [Online]. Available: https://developer.android.com/. [Accessed: 19-Dec-2024].

[2] Google, "Firebase Documentation - Build and Run Your App," [Online]. Available: https://firebase.google.com/docs. [Accessed: 19-Dec-2024].

[3] Google, "Material Design Guidelines - Design System for Android," [Online]. Available: https://m3.material.io/. [Accessed: 19-Dec-2024].

### Thư viện và Framework sử dụng

[4] Bumptech, "Glide - An Image Loading and Caching Library for Android," GitHub Repository. [Online]. Available: https://github.com/bumptech/glide. [Accessed: 19-Dec-2024].

[5] Square Inc., "OkHttp - An HTTP Client for Android and Java Applications," GitHub Repository. [Online]. Available: https://github.com/square/okhttp. [Accessed: 19-Dec-2024].

[6] Google, "ExoPlayer - An Extensible Media Player for Android," GitHub Repository. [Online]. Available: https://github.com/google/ExoPlayer. [Accessed: 19-Dec-2024].

[7] Google, "Gson - A Java Serialization/Deserialization Library for JSON," GitHub Repository. [Online]. Available: https://github.com/google/gson. [Accessed: 19-Dec-2024].

[8] Henning Dodenhof, "CircleImageView - A Fast Circular ImageView for Android," GitHub Repository. [Online]. Available: https://github.com/hdodenhof/CircleImageView. [Accessed: 19-Dec-2024].

### Sách và Tài liệu học thuật

[9] B. Phillips, C. Stewart, K. Marsicano, "Android Programming: The Big Nerd Ranch Guide," 5th Edition, Big Nerd Ranch, 2022.

[10] J. Horton, "Android Programming for Beginners," 3rd Edition, Packt Publishing, 2021.

[11] D. Griffiths, D. Griffiths, "Head First Android Development: A Brain-Friendly Guide," 3rd Edition, O'Reilly Media, 2021.

### Công cụ phát triển và Hỗ trợ

[12] JetBrains, "IntelliJ IDEA - The IDE for Professional Development," [Online]. Available: https://www.jetbrains.com/idea/. [Accessed: 19-Dec-2024].

[13] ngrok Inc., "ngrok - Secure Introspectable Tunnels to Localhost," [Online]. Available: https://ngrok.com/. [Accessed: 19-Dec-2024].

[14] Ollama, "Ollama - Get Up and Running with Large Language Models Locally," [Online]. Available: https://ollama.ai/. [Accessed: 19-Dec-2024].

### Tiêu chuẩn và Quy tắc trích dẫn

[15] IEEE, "IEEE Citation Reference," IEEE Author Center. [Online]. Available: https://ieee-dataport.org/sites/default/files/analysis/27/IEEE%20Citation%20Guidelines.pdf. [Accessed: 19-Dec-2024].

---

## Phụ lục

### Phụ lục A: Mã nguồn và Repository

**Link GitHub Repository:**
- **Source Code:** https://github.com/baongdqu/App_movie_booking_ticket
- **Nhánh chính (Main branch):** Chứa phiên bản ổn định của ứng dụng
- **Nhánh phát triển (Dev branch):** Chứa các tính năng đang được phát triển

### Phụ lục B: Cấu trúc Cơ sở dữ liệu Firebase

```json
{
  "Banners": [
    {
      "age": "+13",
      "genre": "War Action Adventure",
      "image": "https://example.com/banner.jpg",
      "name": "1917",
      "time": "2 Hour",
      "year": "2023"
    }
  ],
  "Items": [
    {
      "Title": "Dune: Part Two",
      "Description": "Paul Atreides unites with Chani and the Fremen...",
      "Poster": "https://firebasestorage.googleapis.com/.../Dune.jpg",
      "Trailer": "https://www.imdb.com/video/...",
      "Time": "2h 46m",
      "Imdb": 8.5,
      "Year": 2024,
      "price": 120,
      "Genre": ["Adventure", "Action", "Drama"],
      "Casts": [
        {"Actor": "Timothée Chalamet", "PicUrl": "https://..."},
        {"Actor": "Zendaya", "PicUrl": "https://..."}
      ],
      "Pcitures": ["https://...", "https://..."]
    }
  ],
  "Upcomming": [
    // Cấu trúc giống Items
  ],
  "Bookings": {
    "Tên phim": {
      "2025-11-08_18:00": {
        "pricePerSeat": 90000,
        "seats": {
          "A1": "booked",
          "A2": "available",
          "B1": "available"
          // ... 8 cột x 11 hàng (A-K)
        }
      }
    }
  },
  "tickets": {
    "-ticketId": {
      "createdAt": 1765862120547,
      "date": "2025-11-08",
      "movieTitle": "The Gorge",
      "posterUrl": "https://...",
      "seats": ["C5", "C6", "D5", "D6"],
      "time": "15:15",
      "totalPrice": 280000,
      "userId": "xS5e9wtrraOkikepokVdjNcIFYq1",
      "status": "PAID",
      "payment": {
        "method": "VNPAY",
        "paidAt": 1765862120547,
        "status": "PAID"
      }
    }
  },
  "notifications": {
    "userId": {
      "-notificationId": {
        "title": "Hoàn tiền thành công",
        "message": "Bạn đã được hoàn 95000đ cho vé Rebel Moon",
        "type": "REFUND",
        "read": false,
        "timestamp": 1766038881130,
        "ticketId": "-OggpFLq1G7ydiTFDw9F"
      }
    }
  },
  "users": {
    "userId": {
      "uid": "CU6DPufQVadwqpI7GbT40KKsvPy1",
      "fullName": "Họ và tên",
      "email": "email@gmail.com",
      "phone": "+84397107173",
      "avatarUrl": "https://i.ibb.co/.../avatar.jpg",
      "balance": 475000,
      "dateOfBirth": "03/12/2025",
      "gender": "Nữ",
      "phoneVerified": true,
      "moviePreferences": {
        "favoriteGenre": "Drama",
        "favoriteLanguage": "French",
        "genreIndex": 2,
        "languageIndex": 7,
        "subtitlePreference": "both"
      }
    }
  }
}
```

**Giải thích các node chính:**

| Node            | Mô tả                                               | Các trường quan trọng                                                                             |
| --------------- | --------------------------------------------------- | ------------------------------------------------------------------------------------------------- |
| `Banners`       | Danh sách banner quảng cáo slideshow trên trang chủ | `name`, `image`, `genre`, `age`, `year`, `time`                                                   |
| `Items`         | Danh sách phim đang chiếu (Top Movies)              | `Title`, `Poster`, `Trailer`, `Imdb`, `Genre`, `Casts`, `Description`, `Time`, `Year`, `price`    |
| `Upcomming`     | Danh sách phim sắp chiếu (cấu trúc giống Items)     | Tương tự `Items`                                                                                  |
| `Bookings`      | Trạng thái ghế theo phim và suất chiếu              | Key: `"Tên phim"/"ngày_giờ"`, `pricePerSeat`, `seats` (A1-K8: available/booked)                   |
| `tickets`       | Lịch sử vé đã đặt của tất cả người dùng             | `movieTitle`, `date`, `time`, `seats[]`, `totalPrice`, `userId`, `status`, `payment`              |
| `notifications` | Thông báo theo từng userId                          | `title`, `message`, `type` (REFUND/PROFILE), `read`, `timestamp`, `ticketId`                      |
| `users`         | Thông tin tài khoản và sở thích phim                | `fullName`, `email`, `phone`, `avatarUrl`, `balance`, `gender`, `dateOfBirth`, `moviePreferences` |

### Phụ lục C: API Endpoints (Chatbot Server)

| Phương thức | Endpoint  | Mô tả                           | Request Body                                 | Response              |
| ----------- | --------- | ------------------------------- | -------------------------------------------- | --------------------- |
| GET         | `/health` | Kiểm tra trạng thái server      | -                                            | `{"status": "ok"}`    |
| POST        | `/chat`   | Gửi tin nhắn đến AI Chatbot     | `{"message": "...", "system_prompt": "..."}` | `{"response": "..."}` |
| GET         | `/models` | Lấy danh sách model AI khả dụng | -                                            | `{"models": [...]}`   |

### Phụ lục D: Hướng dẫn cài đặt và Chạy ứng dụng

#### D.1. Yêu cầu hệ thống
- **Android Studio:** Phiên bản Koala (2024.1.1) trở lên
- **JDK:** Java Development Kit 11 hoặc 17
- **Android SDK:** API Level 24 (Minimum) - API Level 34 (Target)
- **RAM:** Tối thiểu 8GB (Khuyến nghị 16GB)
- **Ổ cứng:** Tối thiểu 10GB dung lượng trống

#### D.2. Các bước cài đặt
1. Clone repository từ GitHub:
   ```bash
   git clone https://github.com/baongdqu/App_movie_booking_ticket.git
   ```
2. Mở project bằng Android Studio
3. Đợi Gradle sync hoàn tất
4. Kết nối thiết bị Android hoặc khởi động Emulator
5. Nhấn **Run 'app'** (Shift + F10)

#### D.3. Cấu hình Firebase
1. Tạo project mới trên Firebase Console
2. Thêm ứng dụng Android với package name: `com.example.app_movie_booking_ticket`
3. Tải file `google-services.json` và đặt vào thư mục `app/`
4. Bật Firebase Authentication (Email/Password)
5. Bật Firebase Realtime Database và cấu hình Security Rules

#### D.4. Cấu hình AI Chatbot Server
1. Cài đặt Ollama trên máy tính
2. Tải model AI (ví dụ: `ollama pull gemma2`)
3. Chạy server Python:
   ```bash
   python gemini_bridge.py
   ```
4. Expose server qua ngrok:
   ```bash
   ngrok http 5000
   ```
5. Cập nhật URL ngrok vào `strings.xml`:
   ```xml
   <string name="gemini_cli_server_url">https://xxxx.ngrok-free.app</string>
   ```

### Phụ lục E: Biểu mẫu khảo sát người dùng

#### E.1. Phiếu khảo sát trải nghiệm người dùng (UX Survey)

**Thông tin chung:**
- Họ và tên: _______________
- Độ tuổi: ☐ 18-25  ☐ 26-35  ☐ 36-45  ☐ >45
- Tần suất đi xem phim: ☐ 1-2 lần/tháng  ☐ 3-4 lần/tháng  ☐ >4 lần/tháng

**Đánh giá ứng dụng (1-5 sao):**

| Tiêu chí                         | 1 ⭐ | 2 ⭐ | 3 ⭐ | 4 ⭐ | 5 ⭐ |
| -------------------------------- | --- | --- | --- | --- | --- |
| Giao diện đẹp, dễ nhìn           | ☐   | ☐   | ☐   | ☐   | ☐   |
| Thao tác dễ dàng, trực quan      | ☐   | ☐   | ☐   | ☐   | ☐   |
| Tốc độ tải trang nhanh           | ☐   | ☐   | ☐   | ☐   | ☐   |
| Thông tin phim đầy đủ, chính xác | ☐   | ☐   | ☐   | ☐   | ☐   |
| Quy trình đặt vé thuận tiện      | ☐   | ☐   | ☐   | ☐   | ☐   |
| Chatbot hữu ích                  | ☐   | ☐   | ☐   | ☐   | ☐   |
| Tính năng khóa PIN an toàn       | ☐   | ☐   | ☐   | ☐   | ☐   |

**Câu hỏi mở:**
1. Bạn thích điều gì nhất ở ứng dụng? _______________
2. Bạn muốn cải thiện điều gì? _______________
3. Bạn có sẵn sàng giới thiệu ứng dụng cho bạn bè? ☐ Có  ☐ Không

#### E.2. Kết quả khảo sát (Mẫu)

| Tiêu chí                    | Điểm trung bình | Số người đánh giá |
| --------------------------- | --------------- | ----------------- |
| Giao diện đẹp, dễ nhìn      | 4.6/5           | 50                |
| Thao tác dễ dàng, trực quan | 4.4/5           | 50                |
| Tốc độ tải trang nhanh      | 4.2/5           | 50                |
| Quy trình đặt vé thuận tiện | 4.5/5           | 50                |
| Chatbot hữu ích             | 4.0/5           | 50                |

### Phụ lục F: Danh sách các thư viện Dependencies

```gradle
dependencies {
    // Firebase
    implementation platform('com.google.firebase:firebase-bom:33.7.0')
    implementation 'com.google.firebase:firebase-database'
    implementation 'com.google.firebase:firebase-auth'
    
    // UI Components
    implementation 'com.google.android.material:material:1.12.0'
    implementation 'de.hdodenhof:circleimageview:3.1.0'
    implementation 'androidx.viewpager2:viewpager2:1.1.0'
    
    // Image Loading
    implementation 'com.github.bumptech.glide:glide:4.16.0'
    
    // Video Player
    implementation 'com.google.android.exoplayer:exoplayer:2.19.1'
    
    // Networking
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'com.google.code.gson:gson:2.10.1'
    
    // AndroidX Core
    implementation 'androidx.appcompat:appcompat:1.7.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.2.0'
}
```

### Phụ lục G: Danh mục hình ảnh minh họa

| STT | Tên file                        | Mô tả                        |
| --- | ------------------------------- | ---------------------------- |
| 1   | `screenshot_login.png`          | Màn hình đăng nhập           |
| 2   | `screenshot_home.png`           | Màn hình trang chủ           |
| 3   | `screenshot_movie_detail.png`   | Màn hình chi tiết phim       |
| 4   | `screenshot_seat_selection.png` | Màn hình chọn ghế            |
| 5   | `screenshot_payment.png`        | Màn hình thanh toán          |
| 6   | `screenshot_profile.png`        | Màn hình trang cá nhân       |
| 7   | `screenshot_chatbot.png`        | Màn hình Chatbot AI          |
| 8   | `screenshot_settings.png`       | Màn hình cài đặt             |
| 9   | `screenshot_notifications.png`  | Màn hình thông báo           |
| 10  | `screenshot_phone_verify.png`   | Màn hình xác thực điện thoại |
| 11  | `screenshot_preferences.png`    | Màn hình sở thích phim       |
| 12  | `screenshot_pin_lock.png`       | Màn hình khóa PIN            |
| 13  | `diagram_usecase.png`           | Sơ đồ Use Case tổng quát     |
| 14  | `diagram_erd.png`               | Biểu đồ ERD cơ sở dữ liệu    |
| 15  | `diagram_architecture.png`      | Sơ đồ kiến trúc hệ thống     |
| 16  | `diagram_sequence_booking.png`  | Sơ đồ tuần tự đặt vé         |
| 17  | `diagram_sequence_chat.png`     | Sơ đồ tuần tự Chatbot AI     |
