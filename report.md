# BÁO CÁO ĐỒ ÁN: ỨNG DỤNG ĐẶT VÉ XEM PHIM (MOVIE BOOKING APP)

---

## Mục lục

1. [Lời mở đầu](#lời-mở-đầu)
2. [Lời cảm ơn](#lời-cảm-ơn)
3. [Tóm tắt khóa luận, đồ án](#tóm-tắt-khóa-luận-đồ-án)
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

Lời đầu tiên, em xin bày tỏ lòng biết ơn sâu sắc và chân thành nhất tới Thầy/Cô hướng dẫn **[Tên Giảng Viên]**. Trong suốt quá trình thực hiện đồ án, Thầy/Cô đã dành rất nhiều thời gian và tâm huyết để chỉ dẫn, định hướng cho em từ những bước đầu tiên của đề tài cho đến khi hoàn thiện sản phẩm. Những lời khuyên chuyên môn quý báu cùng thái độ làm việc nghiêm túc của Thầy/Cô không chỉ giúp em vượt qua những khó khăn về mặt kỹ thuật mà còn là bài học lớn về tư duy logic và phong cách làm việc chuyên nghiệp.

Em cũng xin gửi lời cảm ơn trân trọng đến quý Thầy/Cô thuộc khoa **[Tên Khoa]**, trường **[Tên Trường]**. Trong suốt những năm tháng học tập tại giảng đường, em đã được truyền đạt những kiến thức nền tảng vững chắc và tiếp cận với những công nghệ hiện đại. Sự tận tâm trong giảng dạy và môi trường học tập năng động mà quý Thầy/Cô tạo ra chính là bệ phóng quan trọng giúp em có đủ tự tin và kỹ năng để hiện thực hóa ý tưởng trong đồ án này.

Bên cạnh đó, em xin dành lời cảm ơn đặc biệt đến gia đình – điểm tựa tinh thần vững chắc nhất. Cảm ơn bố mẹ và người thân đã luôn tin tưởng, động viên và tạo mọi điều kiện tốt nhất về cả vật chất lẫn tinh thần để em có thể tập trung hoàn toàn vào việc học tập và nghiên cứu. Sự ủng hộ vô điều kiện của gia đình chính là động lực lớn lao nhất giúp em vượt qua những giai đoạn áp lực và thử thách trong suốt chặng đường vừa qua.

Em cũng không quên gửi lời cảm ơn đến các bạn bè và đồng nghiệp đã đồng hành cùng em. Cảm ơn mọi người đã luôn sẵn sàng chia sẻ kinh nghiệm, đóng góp ý kiến và cùng nhau thảo luận để tìm ra giải pháp cho những bài toán hóc búa trong quá trình phát triển ứng dụng. Những kỷ niệm và sự hỗ trợ nhiệt tình từ các bạn là một phần không thể thiếu tạo nên thành công của đồ án này.

Cuối cùng, mặc dù đã dành nhiều tâm huyết và nỗ lực để hoàn thành đồ án một cách chỉn chu nhất, nhưng do kiến thức và kinh nghiệm thực tế còn hạn chế, sản phẩm chắc chắn không tránh khỏi những thiếu sót nhất định. Em rất mong nhận được những ý kiến đóng góp, phê bình quý báu từ quý Thầy/Cô để em có thể rút kinh nghiệm và hoàn thiện ứng dụng hơn nữa trong tương lai.

Em xin chân thành cảm ơn!

## Tóm tắt khóa luận, đồ án

Đồ án tập trung vào việc nghiên cứu và xây dựng một ứng dụng di động toàn diện trên nền tảng Android nhằm hỗ trợ người dùng trong việc tìm kiếm thông tin và đặt vé xem phim một cách nhanh chóng, tiện lợi. Trong bối cảnh công nghệ di động phát triển vượt bậc, ứng dụng được thiết kế nhằm tối ưu hóa trải nghiệm người dùng, giúp giảm thiểu thời gian chờ đợi và đơn giản hóa quy trình giao dịch truyền thống tại các rạp chiếu phim hiện nay.

**Các hệ thống chức năng cốt lõi của ứng dụng bao gồm:**

- **Hệ thống hiển thị và quản lý nội dung:** Cung cấp danh sách phim đang chiếu và sắp chiếu được cập nhật liên tục theo thời gian thực. Người dùng có thể dễ dàng tra cứu thông tin chi tiết về nội dung, dàn diễn viên, xem đánh giá và thưởng thức trailer chất lượng cao ngay trên giao diện ứng dụng.
- **Quy trình đặt vé và chọn chỗ thông minh:** Tích hợp tính năng lựa chọn suất chiếu linh hoạt theo cụm rạp, cho phép người dùng thực hiện chọn vị trí ghế ngồi trực quan thông qua sơ đồ phòng chiếu được mô phỏng sinh động và chính xác.
- **Thanh toán và bảo mật giao dịch:** Hỗ trợ các phương thức thanh toán trực tuyến an toàn, tích hợp các lớp bảo mật bổ sung như mã PIN và xác thực tài khoản để đảm bảo an toàn tuyệt đối cho thông tin cá nhân cũng như lịch sử giao dịch của khách hàng.
- **Tiện ích mở rộng:** Tích hợp Chatbot tự động hỗ trợ giải đáp thắc mắc nhanh chóng và hệ thống thông báo nhắc lịch xem phim, giúp nâng cao tính tương tác và giữ chân người dùng.

**Kết quả đạt được:** Sau quá trình thực hiện, ứng dụng đã được hoàn thiện với giao diện UI/UX hiện đại, trẻ trung và hoạt động ổn định trên nhiều dòng thiết bị Android khác nhau. Sản phẩm không chỉ đáp ứng đầy đủ các nhu cầu cấp thiết của người dùng về việc đặt vé trực tuyến mà còn thể hiện tính thực tiễn cao, có khả năng áp dụng vào hệ thống vận hành của các cụm rạp thực tế trong tương lai.

---

## Danh mục hình ảnh

| STT | Tên hình ảnh                               | Nguồn / Mô tả          |
| --- | ------------------------------------------ | ---------------------- |
| 1   | Hình 1.1: Tổng quan thị trường vé xem phim | Internet               |
| 2   | Hình 3.1: Sơ đồ Use Case tổng quát         | Tự vẽ                  |
| 3   | Hình 3.2: Biểu đồ ERD cơ sở dữ liệu        | Tự vẽ                  |
| 4   | Hình 4.1: Giao diện màn hình đăng nhập     | Chụp màn hình ứng dụng |
| 5   | Hình 4.2: Giao diện trang chủ              | Chụp màn hình ứng dụng |
| ... | ...                                        | ...                    |

## Danh mục bảng

| STT | Tên bảng                                 | Số trang |
| --- | ---------------------------------------- | -------- |
| 1   | Bảng 2.1: So sánh các hệ quản trị CSDL   | ...      |
| 2   | Bảng 3.1: Chi tiết các tác nhân (Actors) | ...      |
| 3   | Bảng 3.2: Kịch bản kiểm thử (Test cases) | ...      |

## Danh mục từ viết tắt

| Từ viết tắt | Nghĩa tiếng Anh                   | Nghĩa tiếng Việt             |
| ----------- | --------------------------------- | ---------------------------- |
| UI          | User Interface                    | Giao diện người dùng         |
| UX          | User Experience                   | Trải nghiệm người dùng       |
| DB          | Database                          | Cơ sở dữ liệu                |
| API         | Application Programming Interface | Giao diện lập trình ứng dụng |
| ...         | ...                               | ...                          |

---

## Chương 1: Giới thiệu và tổng quan đề tài

### 1.1. Lý do chọn đề tài

Trong kỷ nguyên số hóa hiện nay, điện thoại thông minh (smartphone) không còn là một thiết bị xa xỉ mà đã trở thành vật bất ly thân của đại đa số người dân. Sự bùng nổ của hạ tầng mạng 4G/5G cùng với các ứng dụng di động đã thay đổi hoàn toàn cách thức con người tiếp cận thông tin và dịch vụ. Trong lĩnh vực giải trí, nhu cầu thưởng thức điện ảnh tại rạp luôn chiếm một tỷ trọng lớn. Tuy nhiên, quy trình mua vé truyền thống đang bộc lộ nhiều bất cập: khách hàng phải tốn thời gian di chuyển đến rạp mà không chắc chắn còn vé hay không, việc xếp hàng chờ đợi trong các dịp lễ hay phim "bom tấn" gây ra sự mệt mỏi và ức chế.

Hơn nữa, việc thiếu thông tin trực quan về vị trí ghế ngồi, lịch chiếu cập nhật theo thời gian thực khiến người dùng khó lòng sắp xếp kế hoạch cá nhân một cách tối ưu. Đối với các đơn vị vận hành, việc quản lý thủ công cũng dẫn đến sai sót và khó khăn trong việc phân tích hành vi khách hàng. Chính vì vậy, việc phát triển một ứng dụng đặt vé xem phim trên nền tảng Android là một yêu cầu tất yếu, nhằm giải quyết triệt để các vấn đề trên, mang lại sự tiện lợi tối đa cho người dùng và nâng cao hiệu quả kinh doanh cho doanh nghiệp.

### 1.2. Mục tiêu đề tài

Đồ án hướng tới việc đạt được các mục tiêu cụ thể sau đây:

- **Về mặt kỹ thuật:** Xây dựng thành công một ứng dụng di động hoàn chỉnh chạy trên hệ điều hành Android, sử dụng các ngôn ngữ lập trình hiện đại như Kotlin/Java kết hợp với kiến trúc MVVM để đảm bảo tính dễ bảo trì và mở rộng của mã nguồn.
- **Về mặt trải nghiệm người dùng (UX):** Thiết kế giao diện trực quan, thân thiện, giúp người dùng có thể hoàn tất quy trình đặt vé từ bước chọn phim đến khi thanh toán chỉ trong vòng chưa đầy 2 phút. Tối ưu hóa tốc độ tải dữ liệu và hiển thị sơ đồ ghế ngồi một cách sinh động.
- **Về tính năng nâng cao:** Tích hợp các công nghệ tiên tiến nhằm tạo sự khác biệt, bao gồm:
  - **AI Chatbot:** Hỗ trợ tư vấn phim, giải đáp thắc mắc về giá vé và rạp chiếu tự động 24/7.
  - **Bảo mật đa lớp:** Triển khai xác thực qua mã PIN hoặc sinh trắc học (vân tay/khuôn mặt) để bảo vệ thông tin thanh toán và ví điện tử tích hợp.
  - **Hệ thống thông báo (Push Notifications):** Nhắc lịch xem phim và cập nhật các chương trình khuyến mãi cá nhân hóa.

### 1.3. Phạm vi đề tài

Đề tài tập trung nghiên cứu và triển khai trong các phạm vi giới hạn sau:

- **Đối tượng người dùng:**
  - **Khách hàng (End User):** Người dùng cuối cùng, bao gồm cả khách hàng mới và khách hàng cũ.
  - **Admin (Backend):** Người dùng có quyền truy cập vào hệ thống quản lý, bao gồm cả việc thêm, sửa, xóa thông tin phim, suất chiếu, và các thông tin khác.

---

## Chương 2: Cơ sở lý thuyết

### 2.1. Lập trình Android

- **Hệ điều hành Android:** Là hệ điều hành di động phổ biến nhất thế giới, được xây dựng dựa trên nhân Linux. Android cung cấp một nền tảng mã nguồn mở (AOSP) cho phép các nhà phát triển tự do tùy biến giao diện và chức năng. Kiến trúc của Android bao gồm 4 tầng chính: Linux Kernel, Libraries & Android Runtime, Application Framework và Applications, giúp quản lý tài nguyên phần cứng hiệu quả và cung cấp môi trường thực thi đa nhiệm mạnh mẽ cho các ứng dụng.
- **Ngôn ngữ lập trình Java:** Là một trong những ngôn ngữ lập trình hướng đối tượng (OOP) phổ biến và lâu đời nhất trong phát triển ứng dụng Android. Java sở hữu hệ sinh thái thư viện đồ sộ, cộng đồng hỗ trợ mạnh mẽ và tính tương thích cao. Trong phát triển Android, Java giúp nhà phát triển dễ dàng quản lý mã nguồn thông qua các đặc tính như đóng gói, kế thừa, đa hình và xử lý ngoại lệ chặt chẽ. Dù có sự xuất hiện của các ngôn ngữ mới, Java vẫn giữ vai trò nền tảng quan trọng nhờ tính ổn định và khả năng tối ưu hóa hiệu suất hệ thống tốt.
- **Môi trường phát triển Android Studio:** Là môi trường phát triển tích hợp (IDE) chính thức dành cho Android, dựa trên nền tảng IntelliJ IDEA của JetBrains. Android Studio cung cấp hệ thống xây dựng dựa trên _Gradle_ linh hoạt, cho phép tùy chỉnh các biến thể bản dựng (Build Variants). IDE này tích hợp sẵn _Layout Editor_ với tính năng xem trước thời gian thực, bộ công cụ _Android Profiler_ để theo dõi mức tiêu thụ CPU, bộ nhớ, mạng và _Logcat_ để kiểm soát lỗi hệ thống. Đặc biệt, nó hỗ trợ tốt việc tích hợp các hệ thống quản lý phiên bản như Git và cung cấp trình giả lập (Emulator) hiệu suất cao để kiểm thử trên nhiều cấu hình thiết bị khác nhau.
- **Android Jetpack:** Tập hợp các thư viện thành phần (Components) giúp nhà phát triển tuân thủ các tiêu chuẩn thiết kế (Best Practices), giảm thiểu mã lặp lại và đảm bảo ứng dụng hoạt động ổn định trên nhiều phiên bản hệ điều hành khác nhau.

### 2.2. Cơ sở dữ liệu và lưu trữ dữ liệu

- **Firebase Realtime Database:** Là hệ thống cơ sở dữ liệu NoSQL dựa trên nền tảng đám mây, cho phép lưu trữ và đồng bộ hóa dữ liệu giữa các người dùng theo thời gian thực. Trong ứng dụng, Realtime Database được sử dụng để quản lý các cấu trúc dữ liệu phức tạp như danh sách phim (`Items`), phim sắp chiếu (`Upcomming`), hệ thống `Banners` và thông tin người dùng (`users`). Với cơ chế lắng nghe sự kiện (Event Listeners), mọi thay đổi về dữ liệu trên server sẽ được phản chiếu ngay lập tức lên giao diện người dùng mà không cần thực hiện các yêu cầu HTTP thủ công. Ngoài ra, tính năng lưu trữ đệm (Disk Persistence) giúp ứng dụng duy trì khả năng hoạt động ngay cả trong điều kiện kết nối mạng không ổn định.
- **Firebase Authentication:** Cung cấp giải pháp xác thực người dùng toàn diện và bảo mật. Ứng dụng triển khai phương thức đăng ký và đăng nhập bằng Email/Mật khẩu, giúp quản lý danh tính người dùng một cách hiệu quả. Firebase Authentication tự động xử lý các tác vụ phức tạp như gửi email xác thực, khôi phục mật khẩu và lưu trữ token phiên làm việc. Mỗi tài khoản người dùng được định danh bằng một mã UID duy nhất, làm cơ sở để phân quyền truy cập và liên kết dữ liệu cá nhân trong cơ sở dữ liệu, đảm bảo an toàn thông tin tuyệt đối.
- **Giao tiếp RESTful API và Định dạng JSON:** Để tích hợp trí tuệ nhân tạo (Chatbot), ứng dụng sử dụng kiến trúc RESTful API để thực hiện các yêu cầu mạng đến máy chủ xử lý ngôn ngữ tự nhiên. Dữ liệu trao đổi được chuẩn hóa dưới định dạng JSON (JavaScript Object Notation), giúp tối ưu hóa băng thông và dễ dàng phân tích (parse) dữ liệu trên Android thông qua các thư viện hỗ trợ.
- **OkHttp và ngrok:** Thư viện **OkHttp** được sử dụng làm tầng xử lý mạng (Networking Layer), hỗ trợ các giao thức HTTP/2, quản lý kết nối hiệu quả và xử lý lỗi mạng linh hoạt. Trong quá trình phát triển, công cụ **ngrok** được sử dụng để thiết lập một đường hầm (tunnel) bảo mật, cho phép ứng dụng di động giao tiếp trực tiếp với server AI đang chạy tại môi trường phát triển cục bộ (localhost) một cách minh bạch và nhanh chóng.

### 2.3. Các công nghệ và thư viện hỗ trợ

- **Kiến trúc ứng dụng:** Ứng dụng được xây dựng dựa trên kiến trúc **Single Activity** (sử dụng một Activity chính quản lý nhiều Fragment) kết hợp với **ViewBinding**. ViewBinding giúp tương tác với các thành phần giao diện một cách an toàn (null-safety) và nhanh chóng hơn so với phương pháp `findViewById` truyền thống.
- **Giao diện người dùng (UI/UX):**
  - **Material Design Components:** Sử dụng bộ thư viện chuẩn của Google để xây dựng các thành phần giao diện hiện đại, nhất quán như `MaterialButton`, `TextInputEditText`, `CardView`.
  - **ViewPager2:** Được sử dụng để tạo slide banner quảng cáo phim mượt mà và hiệu ứng chuyển trang đẹp mắt.
  - **CircleImageView:** Thư viện hỗ trợ hiển thị hình ảnh (avatar người dùng, diễn viên) dưới dạng hình tròn chuyên nghiệp.
- **Xử lý đa phương tiện:**
  - **Glide:** Thư viện tải và hiển thị ảnh mạnh mẽ, hỗ trợ caching thông minh giúp tối ưu hóa băng thông và tăng tốc độ tải trang khi hiển thị poster phim từ URL.
  - **ExoPlayer:** Trình phát video mã nguồn mở của Google, được tích hợp để phát trailer phim chất lượng cao (HLS/Dash) trực tiếp trong ứng dụng với độ trễ thấp và khả năng tùy biến giao diện cao.
- **Xử lý dữ liệu và kết nối mạng:**
  - **Gson (Google Json):** Thư viện dùng để chuyển đổi (serialize/deserialize) các đối tượng Java sang định dạng JSON và ngược lại, phục vụ cho việc lưu trữ và trao đổi dữ liệu.
  - **OkHttp:** Thư viện HTTP Client hiệu suất cao, đóng vai trò quan trọng trong việc thiết lập kết nối mạng ổn định để giao tiếp với Chatbot Server.
- **Tích hợp AI (Chatbot):** Ứng dụng tích hợp mô hình ngôn ngữ lớn (LLM) thông qua **Gemini CLI** (được bọc bởi server Python local) và **ngrok**. Hệ thống này cho phép người dùng trò chuyện tự nhiên để nhận gợi ý phim, tra cứu lịch chiếu.

---

## Chương 3: Phân tích, thiết kế

### 3.1. Phân tích yêu cầu

#### 3.1.1. Yêu cầu chức năng (Functional Requirements)

Hệ thống được thiết kế để đáp ứng đầy đủ chu trình trải nghiệm của người dùng xem phim:

- **Quản lý tài khoản (Authentication & Authorization):**
  - Cho phép người dùng đăng ký tài khoản mới với các thông tin cơ bản.
  - Đăng nhập hệ thống bảo mật qua Email/Password.
  - Tính năng "Quên mật khẩu" hỗ trợ khôi phục quyền truy cập nhanh chóng.
  - Đăng xuất an toàn.
- **Tra cứu và Tìm kiếm phim (Discovery):**
  - Hiển thị danh sách phim nổi bật ("Hot/Trending") và phim sắp chiếu ("Upcoming") ngay tại màn hình chính.
  - Tìm kiếm phim thông minh theo từ khóa (tên phim).
  - Xem chi tiết thông tin phim: Nội dung tóm tắt, diễn viên, đạo diễn, thời lượng, điểm đánh giá IMDb.
  - Phát Trailer phim trực tuyến chất lượng cao.
- **Đặt vé và Thanh toán (Booking & Payment):**
  - Lựa chọn ngày chiếu và suất chiếu phù hợp.
  - Sơ đồ ghế ngồi trực quan: Phân biệt rõ ghế đã đặt, ghế đang chọn và ghế trống.
  - Tính toán tổng tiền tự động dựa trên số lượng ghế.
  - Mô phỏng quy trình thanh toán và xuất vé điện tử (QR Code/Mã vé).
- **Tiện ích cá nhân và Nâng cao:**
  - Quản lý thông tin cá nhân (Profile): Cập nhật avatar, đổi mật khẩu.
  - Lịch sử đặt vé: Xem lại danh sách các vé đã mua.
  - **AI Chatbot:** Trợ lý ảo hỗ trợ tư vấn phim và giải đáp thắc mắc.
  - **Cài đặt nâng cao:** Thiết lập ngôn ngữ, bật/tắt thông báo, và đặc biệt là tính năng **Khóa ứng dụng (PIN Lock/App Lock)** để bảo vệ quyền riêng tư.

#### 3.1.2. Yêu cầu phi chức năng (Non-Functional Requirements)

- **Hiệu năng (Performance):** Ứng dụng phải phản hồi thao tác người dùng dưới 1 giây. Việc tải danh sách phim và ảnh phải được tối ưu hóa (caching) để hoạt động mượt mà ngay cả khi mạng yếu.
- **Bảo mật (Security):** Mật khẩu người dùng không được lưu trữ plain-text trên local. Dữ liệu thanh toán và thông tin cá nhân phải được bảo vệ. Tính năng khóa PIN phải hoạt động chính xác khi ứng dụng chạy nền.
- **Giao diện và Trải nghiệm (UI/UX):** Giao diện thiết kế theo phong cách hiện đại (Modern UI), sử dụng tông màu tối (Dark Theme) phù hợp với rạp chiếu phim. Các thao tác chuyển màn hình phải có hiệu ứng (animation) mượt mà.
- **Độ tin cậy (Reliability):** Ứng dụng phải xử lý tốt các ngoại lệ (mất mạng, lỗi server) và không bị crash đột ngột (Force Close).

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

| Giai đoạn | Thời gian | Công việc chính                                     | Người thực hiện |
| --------- | --------- | --------------------------------------------------- | --------------- |
| 1         | Tuần 1-2  | Nghiên cứu đề tài, thu thập yêu cầu                 | ...             |
| 2         | Tuần 3-4  | Phân tích và thiết kế hệ thống, CSDL                | ...             |
| 3         | Tuần 5-8  | Code các chức năng chính (Frontend + Backend)       | ...             |
| 4         | Tuần 9    | Tích hợp các tính năng nâng cao (Chatbot, PIN lock) | ...             |
| 5         | Tuần 10   | Kiểm thử và sửa lỗi                                 | ...             |
| 6         | Tuần 11   | Viết báo cáo và chuẩn bị slide                      | ...             |

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

#### 5.3.2. Bảng tổng hợp các lỗi đã phát hiện và khắc phục

| ID     | Mô tả lỗi                                                         | Mức độ     | Ngày phát hiện | Ngày khắc phục | Trạng thái   |
| ------ | ----------------------------------------------------------------- | ---------- | -------------- | -------------- | ------------ |
| BUG-01 | Ứng dụng crash khi mất mạng đột ngột trên màn hình Home.          | Cao        | 10/12/2024     | 11/12/2024     | ✅ Đã sửa     |
| BUG-02 | Thiếu thông báo khi người dùng không chọn ghế mà nhấn "Tiếp tục". | Trung bình | 12/12/2024     | 12/12/2024     | ✅ Đã sửa     |
| BUG-03 | Hình ảnh poster bị lỗi tải trên một số thiết bị Android 8.0.      | Thấp       | 14/12/2024     | 15/12/2024     | ✅ Đã sửa     |
| BUG-04 | PIN Lock vẫn được yêu cầu sau khi đăng xuất.                      | Cao        | 16/12/2024     | 17/12/2024     | ✅ Đã sửa     |
| BUG-05 | Chatbot phản hồi chậm trên kết nối 3G.                            | Thấp       | 18/12/2024     | Đang theo dõi  | ⏳ Đang xử lý |

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
- **Source Code:** https://github.com/[username]/App_movie_booking_ticket
- **Nhánh chính (Main branch):** Chứa phiên bản ổn định của ứng dụng
- **Nhánh phát triển (Dev branch):** Chứa các tính năng đang được phát triển

### Phụ lục B: Cấu trúc Cơ sở dữ liệu Firebase

```json
{
  "Banners": {
    "0": "https://example.com/banner1.jpg",
    "1": "https://example.com/banner2.jpg"
  },
  "Items": [
    {
      "Title": "Tên phim",
      "Description": "Mô tả nội dung phim",
      "Poster": "URL ảnh poster",
      "Trailer": "URL video trailer",
      "Time": "120 phút",
      "Imdb": 8.5,
      "Year": 2024,
      "price": 90000,
      "Genre": ["Hành động", "Phiêu lưu"],
      "Casts": [
        {"Actor": "Tên diễn viên", "PicUrl": "URL ảnh"}
      ]
    }
  ],
  "Upcomming": [...],
  "users": {
    "userId": {
      "fullName": "Họ và tên",
      "email": "email@example.com",
      "avatarUrl": "URL ảnh đại diện",
      "balance": 500000,
      "moviePreferences": {
        "favoriteGenres": ["Hành động", "Hài"],
        "preferredWatchTime": "Cuối tuần - Buổi tối"
      },
      "tickets": {
        "ticketId": {
          "movieTitle": "Tên phim",
          "seatCoordinates": ["A1", "A2"],
          "totalPrice": 180000,
          "bookingDate": "2024-12-19"
        }
      }
    }
  }
}
```

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
   git clone https://github.com/[username]/App_movie_booking_ticket.git
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

| STT | Tên file                        | Mô tả                     |
| --- | ------------------------------- | ------------------------- |
| 1   | `screenshot_login.png`          | Màn hình đăng nhập        |
| 2   | `screenshot_home.png`           | Màn hình trang chủ        |
| 3   | `screenshot_movie_detail.png`   | Màn hình chi tiết phim    |
| 4   | `screenshot_seat_selection.png` | Màn hình chọn ghế         |
| 5   | `screenshot_payment.png`        | Màn hình thanh toán       |
| 6   | `screenshot_profile.png`        | Màn hình trang cá nhân    |
| 7   | `screenshot_chatbot.png`        | Màn hình Chatbot AI       |
| 8   | `screenshot_settings.png`       | Màn hình cài đặt          |
| 9   | `diagram_usecase.png`           | Sơ đồ Use Case tổng quát  |
| 10  | `diagram_erd.png`               | Biểu đồ ERD cơ sở dữ liệu |
| 11  | `diagram_architecture.png`      | Sơ đồ kiến trúc hệ thống  |
