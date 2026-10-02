# Phân tích source và chức năng bổ sung

## 1. Cấu trúc và flow cũ

Dự án Java 17, đóng gói WAR, dùng Jakarta Servlet, JSP/JSTL, Jdbi và MySQL. Có cả Maven và Gradle. Giữ nguyên các package `Controller`, `Service`, `Dao`, `Model`, `Util`.

Flow hiện tại: request → Servlet Controller → Service kiểm tra nghiệp vụ → DAO truy vấn database → Controller đặt request attribute → JSP hiển thị. Người dùng đăng nhập được lưu trong session `currentUser`; phân quyền dựa trên `User.role` (`CUSTOMER`, `ADMIN`).

Luồng khách hàng đang có:

- `/home`, `/movies`, `/search`, `/movie-detail`: trang chủ, danh sách, tìm kiếm và chi tiết phim.
- `/showtimes`: lọc lịch chiếu theo phim, ngày, thể loại.
- `/booking`: chọn ghế, tạo booking `PENDING/UNPAID`, giữ ghế 10 phút.
- `/payment`: chọn thanh toán tại quầy hoặc VNPay.
- `/payment-result`, `/vnpay-return`: xem kết quả và xử lý callback.

Các phần thiếu trước khi bổ sung:

- `register.jsp` là form demo, không có route `/register`, không lưu database.
- Đăng nhập admin redirect `/admin/dashboard` nhưng chưa có màn hình/controller tương ứng.
- Chưa có CRUD phim/lịch chiếu, quản lý đơn, lịch sử đặt vé.
- Truy vấn chi tiết thanh toán dùng INNER JOIN ghế nên đơn đã giải phóng ghế không còn xuất hiện.
- Đăng nhập cũ so sánh mật khẩu trực tiếp với `password_hash`.

`js/main.js` còn dữ liệu demo phía trình duyệt. Các chức năng mới dùng Servlet/JSP và database thật, không gọi các hàm demo đó. Không viết lại luồng tìm phim, chọn ghế hoặc phương thức thanh toán hiện tại.

## 2. Chức năng mới và URL

Các URL bên dưới cần thêm context path của ứng dụng khi chạy WAR.

| URL | Chức năng | Quyền |
| --- | --- | --- |
| `/register` | Đăng ký, kiểm tra dữ liệu và email trùng | Khách |
| `/booking-history` | Lịch sử đơn của tài khoản đăng nhập | Đã đăng nhập |
| `/admin/dashboard` | Tổng quan số phim, lịch chiếu, đơn | ADMIN |
| `/admin/movies` | Danh sách, thêm, sửa, xóa phim; gán thể loại; ẩn phim | ADMIN |
| `/admin/showtimes` | Danh sách, thêm, sửa, xóa lịch; mở/đóng/hủy lịch | ADMIN |
| `/admin/bookings` | Lọc đơn theo trạng thái/từ khóa; xác nhận thu tiền; hủy đơn | ADMIN |

Đăng nhập bằng tài khoản ADMIN hiện có để vào quản trị. Đăng ký luôn tạo `CUSTOMER/ACTIVE`; gửi thêm tham số `role=ADMIN` không nâng quyền. Header có liên kết lịch sử đặt vé và liên kết quản trị theo quyền.

Đăng ký thành công chuyển về `/login?registered=1`. Mật khẩu mới được lưu bằng PBKDF2-HMAC-SHA256 với salt riêng. Tài khoản cũ có mật khẩu dạng plain text vẫn đăng nhập được; hash được nâng cấp khi đăng nhập thành công. Không lưu hash mật khẩu vào session.

## 3. Ràng buộc nghiệp vụ

### Phim

- Giữ ba trạng thái có sẵn: `NOW_SHOWING`, `COMING_SOON`, `HIDDEN`.
- Kiểm tra tên, thời lượng 1–600 phút, độ tuổi, ngày, độ dài mô tả và URL HTTP/HTTPS.
- Thể loại lấy từ bảng `genres`; lưu `movie_genres` cùng transaction với phim.
- Không xóa phim đã có lịch chiếu, tránh cascade làm mất dữ liệu.
- Không đổi thời lượng phim đã có lịch chiếu vì ảnh hưởng kiểm tra trùng phòng.
- Muốn ẩn/chuyển sang sắp chiếu, đóng các lịch mở trong tương lai trước.

### Lịch chiếu

- Giữ ba trạng thái có sẵn: `OPEN`, `CLOSED`, `CANCELLED`.
- Chỉ mở lịch cho phim đang chiếu, phòng `ACTIVE` và giờ bắt đầu trong tương lai.
- Kiểm tra khoảng `[start_time, start_time + duration_minutes)` không trùng lịch khác trong cùng phòng. Lịch `CLOSED` vẫn chiếm khoảng thời gian phòng; lịch `CANCELLED` được loại khỏi kiểm tra.
- Khi kiểm tra/lưu, khóa dòng phim, phòng và lịch bằng transaction để tránh hai yêu cầu cùng tạo lịch trùng.
- Lịch đã có bất kỳ đơn đặt vé nào chỉ được đổi trạng thái; không đổi phim/phòng/giờ/giá và không xóa.
- `CLOSED` ngừng nhận đơn mới, giữ các đơn hiện có. `CANCELLED` yêu cầu các đơn đã được xử lý hủy trước.
- Tạo booking khóa suất chiếu và kiểm tra giờ chưa bắt đầu, tránh tạo vé khi admin vừa đóng lịch.

### Đơn đặt vé và lịch sử

- Admin chỉ xác nhận đơn `PENDING/UNPAID`, còn hạn giữ ghế và đã chọn `PAY_AT_COUNTER`. Sau khi thực sự thu tiền, chọn **Xác nhận thu tiền** để cập nhật payment `SUCCESS`, booking `CONFIRMED/PAID` trong cùng transaction.
- Admin hủy đơn chưa thanh toán: cập nhật payment pending sang failed, booking `CANCELLED/FAILED`, xóa `booking_seats` để giải phóng ghế.
- Không hủy/xóa đơn đã thanh toán bằng chức năng này. Schema/flow cũ chưa có hoàn tiền nên không tự coi việc đổi trạng thái là hoàn tiền.
- Lịch sử lọc theo ID lấy từ `currentUser`, không lấy `userId` khách gửi. Hiển thị mã đơn, ngày đặt, phim, phòng, giờ chiếu, ghế, số lượng, tổng tiền, trạng thái; có liên kết tiếp tục thanh toán hoặc xem kết quả.
- Đơn hết hạn hiển thị đã hủy/hết hạn; flow giải phóng ghế cũ vẫn thực hiện khi tạo booking mới. Đơn đã mất dòng ghế vẫn xuất hiện trong lịch sử và trang kết quả; hiển thị thông báo ghế đã giải phóng.
- Callback VNPay kiểm tra giao dịch pending, mã tham chiếu, số tiền, trạng thái đơn và hạn giữ ghế trong transaction. Callback cũ không khôi phục đơn admin đã hủy. Đây là kiểm tra phía ứng dụng; chưa thực hiện giao dịch trực tiếp với VNPay trong bộ test.

## 4. Các thành phần bổ sung

- `Controller/AdminController.java`, `Controller/BookingHistoryController.java`; mở rộng `AuthController` cho đăng ký.
- `Service/AdminService.java`, `Service/BookingManagementService.java`; mở rộng `AuthService`.
- `Dao/AdminDAO.java`, `Dao/BookingManagementDAO.java`; mở rộng `UserDAO`, điều chỉnh truy vấn trong `BookingDAO` và `PaymentDAO`.
- `Model/BookingRecord.java` tái sử dụng định dạng tiền/trạng thái từ `PaymentInfo`.
- `Filter/ManagementFilter.java`: chặn khách chưa đăng nhập, chặn CUSTOMER vào admin, kiểm tra CSRF cho POST quản trị/đăng ký.
- `Util/PasswordUtil.java`: tạo/kiểm tra hash và tương thích mật khẩu cũ.
- Các JSP quản trị và lịch sử nằm trong `WEB-INF/views`, không truy cập trực tiếp từ bên ngoài.
- `css/management.css` cho màn hình mới; tái sử dụng header/footer, màu và nút hiện có.
- Nội dung phim được escape khi hiển thị, bao gồm các JSP khách hàng dùng dữ liệu do admin nhập. Trailer truyền URL qua data attribute để tránh chèn vào chuỗi JavaScript.

Không thêm bảng, không đổi cột/enum, không cần migration. Đã đối chiếu schema của database đang cấu hình để viết truy vấn phù hợp.

## 5. Build và kiểm thử

Điều kiện chạy: Java 17, MySQL được cấu hình trong `src/main/resources/db.properties`, Tomcat 10.1 hoặc môi trường Jakarta tương thích. Tiếp tục dùng cấu hình triển khai WAR của dự án.

```powershell
# Bộ test thông thường (có một số test DAO cũ kết nối database đang cấu hình)
mvn test

# Build WAR
mvn package

# Bộ test mới qua HTTP/Tomcat + database test riêng
mvn -Dtest=ManagementWorkflowTest -Dcinebook.integration=true test

# Toàn bộ test và build, bật cả workflow integration
mvn -Dcinebook.integration=true package
```

WAR Maven: `target/CineBook-1.0-SNAPSHOT.war`. Gradle vẫn giữ cấu hình WAR `ROOT.war` và các task triển khai cũ. Chưa chạy task deploy.

`ManagementWorkflowTest` chỉ chạy khi bật `cinebook.integration=true`. Test này cần quyền CREATE/DROP DATABASE: tạo database có tên ngẫu nhiên `cinebook_test_<uuid>`, sao chép cấu trúc bảng, tạo dữ liệu thử, chạy Tomcat cổng ngẫu nhiên và dọn database sau khi hoàn tất. Không dùng dữ liệu thật cho các thao tác đăng ký/CRUD/thanh toán trong workflow test mới.

Đã kiểm tra:

- Hash mật khẩu, đăng nhập tài khoản cũ, tài khoản bị khóa, email trùng và xác nhận mật khẩu.
- Khách chưa đăng nhập, CUSTOMER cố vào admin, POST thiếu/sai CSRF, giả mạo role khi đăng ký.
- Lưu phim/thể loại, xóa phim chưa có lịch; chặn xóa dữ liệu đã liên kết.
- Chặn lịch trùng và sửa giờ/giá của lịch đã có đơn; cho phép đóng/mở lịch.
- Xác nhận thu tiền tại quầy, hủy đơn và giải phóng ghế, chặn sửa đơn đã thanh toán.
- Lịch sử theo đúng chủ đơn; hiển thị đơn hủy/hết hạn; không cho thanh toán đơn hết hạn.
- Callback sai tham chiếu/số tiền bị từ chối; callback sau khi hủy bị từ chối; callback hợp lệ cập nhật thanh toán.
- JSP đăng ký, quản trị, lịch sử, danh sách/chi tiết phim, lịch chiếu, đặt vé, thanh toán và kết quả được render qua Tomcat; nội dung HTML trong tên phim được escape.

Bộ test DAO đặt vé cũ đã được sửa điều kiện chọn dữ liệu sang suất chiếu trong tương lai. Nếu database chỉ có lịch cũ hoặc không đủ ghế, các test phụ thuộc dữ liệu đó được skip theo cơ chế assumption sẵn có. Workflow test mới tạo dữ liệu tương lai riêng để vẫn kiểm tra nghiệp vụ.

Kết quả lần kiểm tra này: `mvn -Dcinebook.integration=true package` thành công; 101 test được ghi nhận, 89 pass, 12 skip do dữ liệu của bộ test cũ, 0 failure, 0 error. WAR đã được tạo thành công.
