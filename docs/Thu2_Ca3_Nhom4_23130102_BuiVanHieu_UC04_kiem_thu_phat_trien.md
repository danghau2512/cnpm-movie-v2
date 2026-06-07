# UC04 - Development Testing

## Mục đích

Tài liệu này ghi lại quá trình kiểm thử phát triển do cá nhân thực hiện cho các issue liên quan đến source code của chức năng UC04 - Xem chi tiết phim.

Các test case trong tài liệu này tập trung vào việc kiểm tra các thay đổi do cá nhân thực hiện trong quá trình phát triển, bao gồm kiểm tra URL truy cập chi tiết phim, giới hạn trạng thái hiển thị, dữ liệu thay thế khi phim thiếu thông tin, trạng thái tìm kiếm khi quay lại danh sách phim, thao tác trailer và unit test cho Controller/Service.

---

## Issue 20: [UC04] Giới hạn xem chi tiết phim theo trạng thái hiển thị

### Nội dung issue

Issue này xử lý trường hợp người dùng truy cập trực tiếp trang chi tiết của phim không thuộc trạng thái được phép hiển thị. Chức năng xem chi tiết phim chỉ cho phép hiển thị phim có trạng thái `NOW_SHOWING`. Nếu phim không tồn tại hoặc có trạng thái khác `NOW_SHOWING`, hệ thống sẽ không hiển thị trang chi tiết và điều hướng người dùng về trang danh sách phim.

### Dữ liệu test

| STT | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| 1 | `/movie-detail?id=1`, phim có status `NOW_SHOWING` | Hiển thị trang chi tiết phim | Pass |
| 2 | `/movie-detail?id={id phim COMING_SOON}` | Redirect về `/movies?detailMessage=movie_not_available` | Pass |
| 3 | `/movie-detail?id={id phim HIDDEN}` | Redirect về `/movies?detailMessage=movie_not_available` | Pass |
| 4 | `/movie-detail?id=9999`, phim không tồn tại | Redirect về `/movies?detailMessage=movie_not_available` | Pass |
| 5 | Phim `COMING_SOON` trên trang danh sách phim | Không hiển thị nút “Chi tiết” | Pass |

### Trường hợp thành công

- Phim tồn tại trong database.
- Phim có trạng thái `NOW_SHOWING`.
- Hệ thống lấy được dữ liệu phim và hiển thị trang `movie-detail.jsp`.

### Trường hợp thất bại

- Phim không tồn tại.
- Phim có trạng thái khác `NOW_SHOWING`.
- Hệ thống không hiển thị dữ liệu chi tiết phim.
- Hệ thống redirect về `/movies?detailMessage=movie_not_available`.

---

## Issue 21: [UC04] Bổ sung nội dung thay thế khi phim thiếu thông tin

### Nội dung issue

Issue này kiểm tra việc hiển thị dữ liệu thay thế trên trang chi tiết phim khi phim thiếu poster, trailer hoặc chưa được gán thể loại. Mục tiêu là tránh lỗi giao diện và vẫn hiển thị trang chi tiết phim ổn định với các dữ liệu còn lại.

### Dữ liệu test

| STT | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| 1 | Phim có đầy đủ poster, trailer và thể loại | Hiển thị đầy đủ thông tin phim | Pass |
| 2 | Phim có `posterUrl` rỗng hoặc `NULL` | Hiển thị poster thay thế | Pass |
| 3 | Phim có `trailerUrl` rỗng hoặc `NULL` | Không hiển thị nút “Trailer” | Pass |
| 4 | Phim chưa có bản ghi trong `movie_genres` | Hiển thị thể loại là “Chưa phân loại” | Pass |
| 5 | Phim thiếu mô tả ngắn | Trang vẫn hiển thị bình thường, không lỗi giao diện | Pass |
| 6 | Phim thiếu mô tả chi tiết | Trang vẫn hiển thị bình thường, không lỗi giao diện | Pass |

### Trường hợp thành công

- Trang chi tiết phim vẫn hiển thị được khi thiếu một số trường dữ liệu.
- Poster thay thế được hiển thị khi không có `posterUrl`.
- Nút trailer chỉ hiển thị khi có `trailerUrl`.
- Thể loại mặc định “Chưa phân loại” được hiển thị khi phim chưa có thể loại.

### Trường hợp thất bại

- Thiếu poster nhưng giao diện bị vỡ.
- Thiếu trailer nhưng vẫn hiển thị nút trailer rỗng.
- Phim chưa có thể loại nhưng giao diện hiển thị trống hoặc lỗi.

---

## Issue 43: [UC04] Giữ trạng thái tìm kiếm khi quay lại danh sách phim từ trang chi tiết

### Nội dung issue

Issue này xử lý việc giữ lại từ khóa tìm kiếm khi người dùng đi từ trang danh sách phim sang trang chi tiết phim, sau đó nhấn nút “Quay lại danh sách”. Mục tiêu là giúp người dùng quay lại đúng kết quả tìm kiếm trước đó thay vì mất trạng thái tìm kiếm.

### Dữ liệu test

| STT | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| 1 | Truy cập `/movies?keyword=action` | Danh sách phim hiển thị theo từ khóa `action` | Pass |
| 2 | Nhấn “Chi tiết” từ phim trong kết quả tìm kiếm | URL chi tiết có kèm `keyword=action` | Pass |
| 3 | Trong trang chi tiết, nhấn “Quay lại danh sách” | Quay về `/movies?keyword=action` | Pass |
| 4 | Truy cập trực tiếp `/movie-detail?id=1` không có keyword | Nút quay lại trỏ về `/movies` | Pass |
| 5 | Keyword có khoảng trắng hoặc tiếng Việt | Từ khóa vẫn được truyền lại khi quay về danh sách | Pass |

### Trường hợp thành công

- `MovieController` lấy được `keyword` từ request.
- `MovieController` set `keyword` vào request attribute.
- `movie-detail.jsp` dùng `keyword` để tạo link quay lại danh sách.
- Người dùng quay lại đúng danh sách phim theo từ khóa trước đó.

### Trường hợp thất bại

- Người dùng quay lại danh sách nhưng bị mất từ khóa tìm kiếm.
- Link quay lại bị sai URL.
- Keyword không được truyền từ trang danh sách sang trang chi tiết.

---

## Issue 44: [UC04] Chuẩn hóa URL và thao tác đóng trailer trong chức năng xem chi tiết phim

### Nội dung issue

Issue này kiểm tra việc chuẩn hóa đường dẫn trong chức năng xem chi tiết phim và thao tác mở/đóng trailer. Mục tiêu là đảm bảo các URL trong trang chi tiết hoạt động đúng theo context path của ứng dụng và modal trailer không tiếp tục phát video sau khi đóng.

### Dữ liệu test

| STT | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| 1 | Nhấn “Xem lịch chiếu” từ trang chi tiết phim | Điều hướng đến `/showtimes?movieId={id}` | Pass |
| 2 | Nhấn “Quay lại danh sách” khi không có keyword | Điều hướng đến `/movies` | Pass |
| 3 | Nhấn “Quay lại danh sách” khi có keyword | Điều hướng đến `/movies?keyword={keyword}` | Pass |
| 4 | Nhấn nút “Trailer” khi phim có trailer | Mở modal trailer và hiển thị video | Pass |
| 5 | Nhấn nút đóng modal trailer | Modal đóng và video ngừng phát | Pass |
| 6 | Nhấn ra ngoài vùng video trailer | Modal đóng và video ngừng phát | Pass |
| 7 | Phim không có trailer | Không hiển thị nút “Trailer” | Pass |

### Trường hợp thành công

- Các URL trong trang chi tiết dùng đúng `contextPath`.
- Nút “Xem lịch chiếu” điều hướng đúng movieId.
- Nút “Quay lại danh sách” điều hướng đúng theo trạng thái keyword.
- Trailer modal mở và đóng đúng.
- Khi đóng modal, iframe trailer được xóa `src` để video ngừng phát.

### Trường hợp thất bại

- Link điều hướng bị sai do thiếu `contextPath`.
- Đóng modal nhưng video vẫn tiếp tục phát.
- Phim không có trailer nhưng vẫn hiển thị nút trailer.

---

## Issue 50: [UC04] Đồng bộ comment source code với Main Flow và Alternative Flow

### Nội dung issue

Issue này kiểm tra các comment trong source code có khớp với Main Flow, Alternative Flow và Sequence Diagram của UC04 hay không. Việc bổ sung comment giúp người đọc dễ đối chiếu từ tài liệu use case sang code thực tế.

### Dữ liệu test

| STT | File kiểm tra | Các bước cần đối chiếu | Kết quả mong đợi | Kết quả |
|---|---|---|---|---|
| 1 | `MovieController.java` | `4.1.1`, `4.1.2`, `4.1.3`, `4.1.7`, `4.1.8`, A1, A2, A3, A7 | Comment đúng số bước và đúng logic | Pass |
| 2 | `MovieService.java` | `4.1.4` | Comment đúng bước Service gọi DAO | Pass |
| 3 | `MovieDAO.java` | `4.1.5`, `4.1.6`, A6, A7 | Comment đúng phần truy vấn, ánh xạ Movie và lọc `NOW_SHOWING` | Pass |
| 4 | `movies.jsp` | `4.1.0` | Comment đúng nút “Chi tiết” và điều kiện `NOW_SHOWING` | Pass |
| 5 | `movie-detail.jsp` | `4.1.9`, `4.1.10`, `4.1.11`, `4.1.12`, A4, A5, A6 | Comment đúng phần hiển thị thông tin, trailer và nút điều hướng | Pass |

### Trường hợp thành công

- Comment source code khớp với Use Case Description.
- Comment source code khớp với Sequence Diagram.
- Người đọc có thể đối chiếu các bước trong tài liệu với code.

### Trường hợp thất bại

- Comment lệch số bước.
- Comment không đúng với logic code.
- Cần cập nhật lại comment, không thay đổi logic xử lý.

---

## Issue 51: [UC04] Bổ sung unit test cho chức năng xem chi tiết phim

### Nội dung issue

Issue này bổ sung unit test cho chức năng UC04 - Xem chi tiết phim. Các unit test được viết bằng JUnit 5 và Mockito, tập trung kiểm tra các xử lý chính trong `MovieController` và `MovieService`. Nội dung unit test được lưu trong thư mục `src/test/java` và được commit lên GitHub cùng với mã nguồn.

### File test

| STT | File test | Mục tiêu |
|---|---|---|
| 1 | `src/test/java/Controller/MovieControllerTest.java` | Kiểm tra các nhánh xử lý chính trong `MovieController` khi nhận request `/movie-detail` |
| 2 | `src/test/java/Service/MovieServiceTest.java` | Kiểm tra phương thức `getMovieDetail(id)` của `MovieService` có gọi đúng `MovieDAO.findById(id)` |

### Dữ liệu test

| STT | Test case | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|---|
| 1 | `showMovieDetail_WhenMissingId_ShouldRedirectMissingId` | `id = null` | Redirect về `/movies?detailMessage=missing_id`, không gọi Service | Pass |
| 2 | `showMovieDetail_WhenInvalidId_ShouldRedirectInvalidId` | `id = "abc"` | Redirect về `/movies?detailMessage=invalid_id`, không gọi Service | Pass |
| 3 | `showMovieDetail_WhenMovieNull_ShouldRedirectMovieNotAvailable` | `id = 9999`, Service trả `null` | Redirect về `/movies?detailMessage=movie_not_available` | Pass |
| 4 | `showMovieDetail_WhenMovieExists_ShouldForwardToMovieDetailPage` | `id = 1`, `keyword = "action"`, Service trả Movie | Set attribute `movie`, `keyword` và forward sang `/movie-detail.jsp` | Pass |
| 5 | `getMovieDetail_WhenMovieExists_ShouldReturnMovie` | DAO trả về đối tượng `Movie` | Service trả về đúng đối tượng `Movie` và gọi `findById(1)` | Pass |
| 6 | `getMovieDetail_WhenMovieNotFound_ShouldReturnNull` | DAO trả về `null` | Service trả về `null` và gọi `findById(9999)` | Pass |

### Trường hợp thành công

- Unit test của `MovieControllerTest` chạy thành công.
- Unit test của `MovieServiceTest` chạy thành công.
- Các nhánh chính của UC04 được kiểm tra bằng unit test.
- Nội dung test được lưu trong source code và commit lên GitHub.

### Trường hợp thất bại

- Thiếu `id` nhưng Controller không redirect đúng.
- `id` không phải số nhưng Controller vẫn gọi Service.
- Movie null nhưng Controller không redirect về `movie_not_available`.
- Movie hợp lệ nhưng Controller không set attribute hoặc không forward.
- Service không gọi đúng `MovieDAO.findById(id)`.

### Lệnh chạy test

```bash
.\\gradlew.bat clean test --tests "*MovieControllerTest"
.\\gradlew.bat clean test --tests "*MovieServiceTest"
```

### Kết quả chạy test

| Nội dung | Kết quả |
|---|---|
| `MovieControllerTest` | BUILD SUCCESSFUL |
| `MovieServiceTest` | BUILD SUCCESSFUL |
| Tổng số unit test UC04 | 6 |
| Số test pass | 6 |
| Số test fail | 0 |
| Kết quả chung | Đạt |

---

## Kết luận

Các issue phát triển của UC04 - Xem chi tiết phim đã được kiểm thử trong quá trình development testing. Các thay đổi về giới hạn trạng thái phim, dữ liệu thay thế khi thiếu thông tin, giữ trạng thái tìm kiếm, thao tác trailer, đồng bộ comment và unit test đều đạt kết quả mong đợi.

Nội dung unit test đã được lưu trong source code tại thư mục `src/test/java` và được commit lên GitHub để đáp ứng yêu cầu kiểm thử phát triển của môn học.
