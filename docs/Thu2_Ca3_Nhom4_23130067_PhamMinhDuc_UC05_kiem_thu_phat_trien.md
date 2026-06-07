# UC05 - Development Testing

## Mục đích

Tài liệu này ghi lại quá trình kiểm thử phát triển do cá nhân thực hiện cho các issue liên quan đến source code của chức năng UC05 - Xem lịch chiếu.

Các test case trong tài liệu này tập trung vào việc kiểm tra các thay đổi do cá nhân thực hiện trong quá trình phát triển, bao gồm kiểm tra lọc suất chiếu đã quá giờ, lọc lịch chiếu theo ngày, lọc theo tên phim, lọc theo thể loại phim, cải thiện giao diện và hiển thị số lượng lịch chiếu tìm được.

---

## Các file test đã bổ sung

| STT | File test | Mục đích kiểm thử |
|---|---|---|
| 1 | `src/test/java/Controller/ShowtimeControllerTest.java` | Kiểm tra cấu hình Controller, đường dẫn `/showtimes`, service được sử dụng trong UC05 |
| 2 | `src/test/java/Service/ShowtimeServiceTest.java` | Kiểm tra xử lý nghiệp vụ lọc lịch chiếu trong Service |
| 3 | `src/test/java/Dao/ShowtimeDAOTest.java` | Kiểm tra truy vấn dữ liệu lịch chiếu trong DAO |

---

## Issue 23: [UC05] Ẩn các suất chiếu đã quá giờ

### Nội dung issue

Issue này xử lý trường hợp trang xem lịch chiếu vẫn hiển thị các suất chiếu đã quá thời gian chiếu thực tế. Mục tiêu là chỉ hiển thị các suất chiếu còn hiệu lực, có trạng thái `OPEN` và chưa bắt đầu.

### Dữ liệu test

| STT | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| 1 | Danh sách suất chiếu có `status = OPEN` và `start_time >= NOW()` | Suất chiếu được hiển thị | Pass |
| 2 | Suất chiếu có `start_time < NOW()` | Suất chiếu không được hiển thị | Pass |
| 3 | Suất chiếu có `status != OPEN` | Suất chiếu không được hiển thị | Pass |

### Trường hợp thành công

- Suất chiếu có trạng thái `OPEN`.
- Thời gian chiếu lớn hơn hoặc bằng thời gian hiện tại.
- Hệ thống hiển thị suất chiếu trên trang xem lịch chiếu.

### Trường hợp thất bại

- Suất chiếu đã quá giờ.
- Suất chiếu không còn trạng thái `OPEN`.
- Hệ thống không hiển thị các suất chiếu này trong danh sách lịch chiếu.

---

## Issue 39: [UC05] Thêm lọc lịch chiếu theo ngày

### Nội dung issue

Issue này bổ sung bộ lọc ngày chiếu trên trang xem lịch chiếu. Người dùng có thể chọn một ngày cụ thể để chỉ xem các suất chiếu diễn ra trong ngày đó.

### Dữ liệu test

| STT | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| 1 | Không chọn ngày chiếu | Hiển thị toàn bộ suất chiếu hợp lệ | Pass |
| 2 | Chọn ngày có lịch chiếu | Chỉ hiển thị suất chiếu thuộc ngày được chọn | Pass |
| 3 | Chọn ngày không có lịch chiếu | Hiển thị thông báo không có lịch chiếu phù hợp | Pass |
| 4 | Kết hợp ngày chiếu với phim cụ thể | Chỉ hiển thị lịch chiếu của phim trong ngày được chọn | Pass |

### Trường hợp thành công

- Người dùng chọn ngày có dữ liệu lịch chiếu.
- Hệ thống chỉ hiển thị các suất chiếu thuộc ngày đó.
- Các suất chiếu vẫn phải thỏa điều kiện `OPEN` và chưa quá giờ.

### Trường hợp thất bại

- Ngày được chọn không có suất chiếu phù hợp.
- Hệ thống không lỗi và hiển thị thông báo phù hợp.

---

## Issue 41: [UC05] Thêm lọc lịch chiếu theo tên phim và thể loại phim

### Nội dung issue

Issue này bổ sung bộ lọc theo tên phim và thể loại phim trên trang xem lịch chiếu. Người dùng có thể chọn một phim cụ thể hoặc một thể loại phim để tìm các suất chiếu phù hợp nhanh hơn.

### Dữ liệu test

| STT | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| 1 | Chọn một phim cụ thể | Chỉ hiển thị lịch chiếu của phim được chọn | Pass |
| 2 | Chọn một thể loại phim | Chỉ hiển thị lịch chiếu của các phim thuộc thể loại đó | Pass |
| 3 | Chọn phim kết hợp ngày chiếu | Chỉ hiển thị lịch chiếu của phim trong ngày được chọn | Pass |
| 4 | Chọn thể loại kết hợp ngày chiếu | Chỉ hiển thị lịch chiếu thuộc thể loại trong ngày được chọn | Pass |
| 5 | Nhập hoặc truyền `movieId` sai định dạng | Không phát sinh lỗi hệ thống | Pass |

### Trường hợp thành công

- Bộ lọc theo phim hoạt động đúng với `movieId`.
- Bộ lọc theo thể loại hoạt động đúng với dữ liệu trong `genres` và `movie_genres`.
- Các bộ lọc có thể kết hợp với bộ lọc ngày chiếu.

### Trường hợp thất bại

- Không có phim hoặc thể loại phù hợp.
- `movieId` bị thiếu, sai định dạng hoặc không hợp lệ.
- Hệ thống không lỗi và hiển thị danh sách hoặc thông báo phù hợp.

---

## Issue 48: [UC05] Cải thiện giao diện và thông báo trang lịch chiếu

### Nội dung issue

Issue này cải thiện giao diện trang xem lịch chiếu, bao gồm khu vực bộ lọc, danh sách lịch chiếu và thông báo khi không có kết quả phù hợp.

### Dữ liệu test

| STT | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| 1 | Truy cập trang `/showtimes` | Giao diện hiển thị đúng bố cục | Pass |
| 2 | Có danh sách lịch chiếu | Lịch chiếu hiển thị rõ phim, ngày, giờ, phòng, giá vé và nút đặt vé | Pass |
| 3 | Không có lịch chiếu phù hợp | Hiển thị thông báo thân thiện | Pass |
| 4 | Bấm nút xóa bộ lọc | Trở về danh sách lịch chiếu mặc định | Pass |

### Trường hợp thành công

- Bộ lọc hiển thị rõ ràng.
- Danh sách lịch chiếu dễ quan sát.
- Thông báo khi không có dữ liệu dễ hiểu.

### Trường hợp thất bại

- Giao diện bị vỡ bố cục.
- Thông báo không rõ ràng.
- Người dùng không biết cách tiếp tục thao tác khi không có kết quả.

---

## Issue 56: [UC05] Hiển thị số lượng lịch chiếu tìm được

### Nội dung issue

Issue này bổ sung thông tin hiển thị số lượng lịch chiếu tìm được sau khi người dùng áp dụng các bộ lọc trên trang xem lịch chiếu.

### Dữ liệu test

| STT | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| 1 | Không áp dụng bộ lọc | Hiển thị tổng số lịch chiếu hợp lệ | Pass |
| 2 | Lọc theo ngày | Hiển thị số lượng lịch chiếu tương ứng với ngày đã chọn | Pass |
| 3 | Lọc theo phim | Hiển thị số lượng lịch chiếu của phim đã chọn | Pass |
| 4 | Lọc theo thể loại | Hiển thị số lượng lịch chiếu thuộc thể loại đã chọn | Pass |
| 5 | Không có lịch chiếu phù hợp | Hiển thị số lượng kết quả là 0 hoặc thông báo không có lịch chiếu | Pass |

### Trường hợp thành công

- Số lượng lịch chiếu hiển thị đúng với danh sách đang được hiển thị.
- Người dùng dễ biết có bao nhiêu kết quả phù hợp với điều kiện lọc.

### Trường hợp thất bại

- Số lượng kết quả không khớp với danh sách thực tế.
- Không hiển thị thông tin số lượng khiến người dùng khó theo dõi kết quả.

---

## Lệnh chạy test

Có thể chạy toàn bộ test bằng lệnh:

```bash
./gradlew test
```

Hoặc trên Windows PowerShell:

```bash
gradlew test
```

## Kết luận

Các test case Development Testing đã kiểm tra những phần chính được phát triển trong UC05 - Xem lịch chiếu. Kết quả kiểm thử cho thấy chức năng hoạt động đúng với yêu cầu đã mô tả trong issue, use case, sequence diagram và test case của báo cáo.
