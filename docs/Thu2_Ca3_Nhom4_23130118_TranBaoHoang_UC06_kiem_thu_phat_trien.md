# UC06 - Development Testing

## Mục đích

Tài liệu này ghi lại quá trình kiểm thử phát triển do cá nhân thực hiện cho các issue liên quan đến source code của chức năng UC06 - Đặt vé.

Các test case trong tài liệu này tập trung vào việc kiểm tra các thay đổi do cá nhân thực hiện trong quá trình phát triển, bao gồm kiểm tra dữ liệu đầu vào, xử lý nghiệp vụ, giao diện, transaction và cơ chế khóa ghế tạm thời.

---

## Issue 12: [UC06] Xử lý showtimeId không hợp lệ khi truy cập chức năng đặt vé

### Nội dung issue

Issue này xử lý trường hợp người dùng truy cập chức năng đặt vé với `showtimeId` bị thiếu, rỗng, sai định dạng hoặc nhỏ hơn/bằng 0. Mục tiêu là tránh lỗi 500 và không cho hệ thống xử lý đặt vé với dữ liệu không hợp lệ.

### Dữ liệu test

| STT | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| 1 | `/booking?showtimeId=1` | Hiển thị trang chọn ghế nếu suất chiếu hợp lệ | Pass |
| 2 | `/booking` | Không lỗi 500, chuyển về trang phù hợp, không tạo booking | Pass |
| 3 | `/booking?showtimeId=` | Không lỗi 500, không tạo booking | Pass |
| 4 | `/booking?showtimeId=abc` | Không phát sinh NumberFormatException, không tạo booking | Pass |
| 5 | `/booking?showtimeId=0` | Không xử lý đặt vé, không tạo booking | Pass |
| 6 | `/booking?showtimeId=-1` | Không xử lý đặt vé, không tạo booking | Pass |

### Trường hợp thành công

- `showtimeId` hợp lệ.
- Khách hàng đã đăng nhập.
- Suất chiếu tồn tại và đang cho phép đặt vé.
- Hệ thống hiển thị trang chọn ghế.

### Trường hợp thất bại

- `showtimeId` thiếu, rỗng, sai định dạng hoặc không hợp lệ.
- Hệ thống không tạo booking.
- Hệ thống không lưu dữ liệu vào `booking_seats`.
- Hệ thống không phát sinh lỗi 500.

---

## Issue 13: [UC06] Kiểm tra suất chiếu hợp lệ khi đặt vé

### Nội dung issue

Issue này kiểm tra suất chiếu có tồn tại và còn cho phép đặt vé hay không trước khi hiển thị trang chọn ghế.

### Dữ liệu test

| STT | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| 1 | `showtimeId = 1`, trạng thái `OPEN` | Hiển thị trang chọn ghế | Pass |
| 2 | `showtimeId = 9999`, không tồn tại | Không hiển thị trang chọn ghế, không tạo booking | Pass |
| 3 | `showtimeId = 1`, trạng thái `CLOSED` | Không cho đặt vé | Pass |
| 4 | `showtimeId = 1`, trạng thái `CANCELLED` | Không cho đặt vé | Pass |

### Trường hợp thành công

- Suất chiếu tồn tại.
- Suất chiếu có trạng thái `OPEN`.
- Hệ thống tiếp tục lấy danh sách ghế.

### Trường hợp thất bại

- Suất chiếu không tồn tại.
- Suất chiếu không còn mở đặt vé.
- Hệ thống không tạo đơn đặt vé.

---

## Issue 14: [UC06] Cập nhật truy vấn danh sách ghế và trạng thái ghế khi đặt vé

### Nội dung issue

Issue này kiểm tra việc lấy danh sách ghế theo suất chiếu và xác định trạng thái ghế đã đặt hoặc đang được giữ.

### Dữ liệu test

| STT | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| 1 | Suất chiếu có danh sách ghế | Hiển thị đầy đủ danh sách ghế | Pass |
| 2 | Ghế chưa thuộc booking nào | Ghế hiển thị là ghế trống | Pass |
| 3 | Ghế thuộc booking `CONFIRMED` | Ghế bị disabled, không thể chọn | Pass |
| 4 | Ghế thuộc booking `PENDING/UNPAID` còn hạn `hold_expires_at` | Ghế bị disabled, không thể chọn | Pass |
| 5 | Ghế thuộc booking `CANCELLED` | Ghế được mở lại và có thể chọn | Pass |

### Trường hợp thành công

- Ghế được hiển thị đúng trạng thái.
- Ghế trống có thể chọn.
- Ghế đã đặt hoặc đang giữ không thể chọn.

### Trường hợp thất bại

- Không có danh sách ghế.
- Ghế bị sai trạng thái.
- Ghế đã đặt nhưng vẫn chọn được.

---

## Issue 15: [UC06] Cập nhật xử lý tạo booking và lưu ghế trong BookingDAO

### Nội dung issue

Issue này kiểm tra việc tạo booking, kiểm tra ghế hợp lệ và lưu danh sách ghế đã chọn vào bảng `booking_seats`.

### Dữ liệu test

| STT | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| 1 | `userId=1`, `showtimeId=1`, `seatIds=[1,2]` hợp lệ | Tạo booking và lưu 2 ghế thành công | Pass |
| 2 | Ghế không thuộc phòng chiếu của suất chiếu | Không tạo booking | Pass |
| 3 | Ghế đã thuộc booking `CONFIRMED` | Không tạo booking, báo ghế đã được đặt | Pass |
| 4 | Ghế đang được giữ bởi booking khác | Không tạo booking, báo ghế đang được giữ | Pass |

### Trường hợp thành công

- Booking được tạo với trạng thái `PENDING`.
- Thanh toán ban đầu là `UNPAID`.
- Ghế được lưu vào `booking_seats`.
- Hệ thống trả về `bookingId`.

### Trường hợp thất bại

- Ghế không hợp lệ.
- Ghế đã được đặt.
- Ghế đang được giữ.
- Hệ thống không tạo booking và không lưu `booking_seats`.

---

## Issue 18: [UC06] Đảm bảo transaction khi tạo booking và lưu danh sách ghế

### Nội dung issue

Issue này đảm bảo thao tác tạo booking và lưu danh sách ghế được thực hiện trong cùng transaction.

### Dữ liệu test

| STT | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| 1 | Tạo booking và lưu ghế thành công | Transaction commit thành công | Pass |
| 2 | Lỗi khi lưu `booking_seats` | Rollback toàn bộ transaction | Pass |
| 3 | Lỗi khi tạo booking | Không lưu dữ liệu vào `booking_seats` | Pass |

### Trường hợp thành công

- Bảng `bookings` có dữ liệu mới.
- Bảng `booking_seats` có đủ ghế tương ứng.
- Không có dữ liệu thiếu.

### Trường hợp thất bại

- Nếu lưu ghế lỗi, booking không được giữ lại.
- Không xảy ra trường hợp booking tồn tại nhưng không có ghế đi kèm.

---

## Issue 28: [UC06] Cải thiện giao diện chọn ghế trong trang đặt vé

### Nội dung issue

Issue này kiểm tra giao diện chọn ghế, cập nhật số lượng vé, tổng tiền và chặn xác nhận khi chưa chọn ghế.

### Dữ liệu test

| STT | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| 1 | Truy cập `/booking?showtimeId=1` | Hiển thị trang chọn ghế | Pass |
| 2 | Chọn ghế A1, A2 | Cập nhật số lượng vé = 2 và tổng tiền = giá vé * 2 | Pass |
| 3 | Bỏ chọn toàn bộ ghế | Tổng tiền về 0, nút xác nhận bị disabled | Pass |
| 4 | Không chọn ghế nhưng xác nhận | Hệ thống chặn submit và yêu cầu chọn ghế | Pass |
| 5 | Ghế đã đặt hoặc đang giữ | Ghế bị disabled, không thể chọn | Pass |

### Trường hợp thành công

- Ghế trống chọn được.
- Tổng tiền cập nhật đúng.
- Nút xác nhận chỉ bật khi đã chọn ghế.

### Trường hợp thất bại

- Khách hàng chưa chọn ghế.
- Giao diện không cho gửi form tạo booking.

---

## Issue 46: [UC06] Khóa ghế tạm thời khi tạo booking chờ thanh toán

### Nội dung issue

Issue này kiểm tra cơ chế giữ ghế tạm thời bằng trường `hold_expires_at`.

### Dữ liệu test

| STT | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| 1 | Khách hàng A chọn ghế A1 và xác nhận đặt vé | Booking được tạo, A1 được giữ tạm thời | Pass |
| 2 | Khách hàng B truy cập cùng suất chiếu khi A1 còn hạn giữ | A1 bị disabled, không thể chọn | Pass |
| 3 | Khách hàng A thanh toán trong thời gian giữ ghế | Booking chuyển sang `CONFIRMED`, thanh toán `PAID` | Pass |
| 4 | Booking hết hạn giữ ghế nhưng chưa thanh toán | Booking chuyển sang `CANCELLED/FAILED`, ghế được mở lại | Pass |
| 5 | Thanh toán booking đã hết hạn giữ ghế | Hệ thống không xác nhận thanh toán | Pass |

### Trường hợp thành công

- Booking chờ thanh toán giữ ghế trong thời gian timeout.
- Khách hàng khác không thể đặt trùng ghế.
- Thanh toán trong thời gian giữ ghế thì booking được xác nhận.

### Trường hợp thất bại

- Booking hết hạn giữ ghế.
- Hệ thống không cho xác nhận thanh toán.
- Ghế được mở lại cho khách hàng khác đặt.

---

## Issue 50: [UC06] Đồng bộ comment source code với Sequence Diagram sau khi khóa ghế tạm thời

### Nội dung issue

Issue này kiểm tra comment trong source code có khớp với Sequence Diagram mới hay không.

### Dữ liệu test

| STT | File kiểm tra | Các bước cần đối chiếu | Kết quả mong đợi | Kết quả |
|---|---|---|---|---|
| 1 | `BookingController.java` | `6.1.1`, `6.1.2`, `6.1.3`, `6.1.4`, `6.1.7`, `6.1.10`, `6.1.14`, `6.1.15`, `6.1.24`, `6.1.25` | Comment đúng số bước | Pass |
| 2 | `BookingService.java` | `6.1.5`, `6.1.6`, `6.1.8`, `6.1.9`, `6.1.16`, `6.1.17` | Comment đúng số bước | Pass |
| 3 | `SeatDAO.java` | `6.1.8`, `6.1.9` | Comment đúng số bước | Pass |
| 4 | `BookingDAO.java` | `6.1.17` đến `6.1.23` | Comment đúng số bước | Pass |
| 5 | `booking.jsp` | `6.1.10` đến `6.1.14` | Comment đúng số bước | Pass |

### Trường hợp thành công

- Comment source code khớp Sequence Diagram.
- Người đọc có thể đối chiếu từ Use Case sang Sequence và source code.

### Trường hợp thất bại

- Comment lệch số bước.
- Cần cập nhật lại comment, không thay đổi logic xử lý.