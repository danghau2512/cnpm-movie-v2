# UC03 - Development Testing

## Mục đích

Tài liệu này ghi lại quá trình kiểm thử phát triển do cá nhân thực hiện cho các issue liên quan đến source code của chức năng UC03 - Tìm kiếm phim.

Các test case trong tài liệu này tập trung vào việc kiểm tra các thay đổi do cá nhân thực hiện trong quá trình phát triển, bao gồm kiểm tra từ khóa đầu vào, xử lý nghiệp vụ tìm kiếm, giao diện kết quả, gợi ý thể loại, lọc trạng thái phim và chuẩn hóa ký tự đặc biệt.

---

## Issue 24: [UC03] Cải tiến chức năng tìm kiếm phim

### Nội dung issue

Issue này xử lý trường hợp người dùng truy cập chức năng tìm kiếm với `keyword` bị thiếu, rỗng hoặc chỉ chứa khoảng trắng. Mục tiêu là tránh lỗi và không cho hệ thống thực hiện tìm kiếm với dữ liệu không hợp lệ, đồng thời hiển thị danh sách phim đang chiếu khi chưa nhập từ khóa.

### Dữ liệu test

| STT | Dữ liệu test                                         | Kết quả mong đợi                                        | Kết quả |
|---|------------------------------------------------------|---------------------------------------------------------|---|
| 1 | `/search?keyword=heo`                                | Hiển thị danh sách phim khớp với từ khóa "heo"          | Pass |
| 2 | `/search` (không có param keyword)                   | Hiển thị danh sách phim đang chiếu, không báo lỗi       | Pass |
| 3 | `/search?keyword=` (keyword rỗng)                    | Hiển thị thông báo yêu cầu nhập từ khóa, không tìm kiếm | Pass |
| 4 | `/search?keyword=   ` (chỉ khoảng trắng)             | Hiển thị thông báo yêu cầu nhập từ khóa, không tìm kiếm | Pass |
| 5 | `/search?keyword=  heo  ` (có khoảng trắng đầu cuối) | Tìm kiếm với từ khóa "heo" đã được trim                 | Pass |

### Trường hợp thành công

- `keyword` hợp lệ và không rỗng.
- Hệ thống gọi `MovieService.searchMovies()` để tìm kiếm.
- Hệ thống trả về danh sách phim phù hợp.

### Trường hợp thất bại

- `keyword` rỗng hoặc chỉ chứa khoảng trắng.
- Hệ thống không thực hiện truy vấn tìm kiếm.
- Hệ thống hiển thị thông báo yêu cầu nhập từ khóa.
- Hệ thống không phát sinh lỗi 500.

---

## Issue 30: [UC03: Tìm kiếm phim]: Gợi ý thêm khi không có kết quả phim cần tìm kiếm

### Nội dung issue

Issue này kiểm tra hành vi của hệ thống khi từ khóa hợp lệ nhưng không có phim nào khớp. Hệ thống cần hiển thị thông báo không tìm thấy kết quả và gợi ý danh sách thể loại phổ biến để người dùng thử lại.

### Dữ liệu test

| STT | Dữ liệu test                                        | Kết quả mong đợi | Kết quả |
|---|-----------------------------------------------------|---|---|
| 1 | `/search?keyword=heo` (phim tồn tại)                | Hiển thị danh sách phim, không hiển thị gợi ý | Pass |
| 2 | `/search?keyword=xyzkhongtontai` (không có kết quả) | Hiển thị thông báo không tìm thấy và danh sách thể loại gợi ý | Pass |
| 3 | Nhấn vào thể loại gợi ý (ví dụ: "Hành động")        | Tìm kiếm lại với từ khóa là tên thể loại | Pass |
| 4 | Từ khóa hợp lệ nhưng không khớp bất kỳ phim nào     | `suggestedGenres` được truyền sang JSP và hiển thị | Pass |

### Trường hợp thành công

- Tìm kiếm có kết quả.
- Hệ thống hiển thị danh sách phim, không hiển thị khối gợi ý thể loại.

### Trường hợp thất bại

- Không có phim khớp với từ khóa.
- Hệ thống hiển thị thông báo: `"Không tìm thấy phim phù hợp với từ khóa: \"...\""`.
- Hệ thống lấy danh sách thể loại từ `MovieService.getAllGenres()` và hiển thị gợi ý.

---

## Issue 32: [UC03: Tìm kiếm phim]: Hiển thị tiêu đề trang tìm kiếm động

### Nội dung issue

Issue này kiểm tra việc hiển thị tiêu đề và mô tả trang thay đổi theo trạng thái tìm kiếm. Khi có từ khóa, tiêu đề hiển thị từ khóa đang tìm và số lượng kết quả. Khi không có từ khóa, tiêu đề hiển thị "Danh sách phim".

### Dữ liệu test

| STT | Dữ liệu test                                        | Kết quả mong đợi                                                | Kết quả |
|---|-----------------------------------------------------|-----------------------------------------------------------------|---|
| 1 | `/search` (không có keyword)                        | Tiêu đề: "Danh sách phim", mô tả: "Đang hiển thị X phim"        | Pass |
| 2 | `/search?keyword=heo` (có kết quả)                  | Tiêu đề: `Kết quả cho: "heo"`, mô tả: "Tìm thấy X phim phù hợp" | Pass |
| 3 | `/search?keyword=xyzkhongtontai` (không có kết quả) | Tiêu đề: `Kết quả cho: "xyzkhongtontai"`, số lượng = 0          | Pass |
| 4 | Tìm kiếm trả về nhiều phim                          | Số lượng hiển thị khớp với kết quả thực tế                      | Pass |

### Trường hợp thành công

- `keyword` được truyền sang JSP và hiển thị đúng trong tiêu đề.
- Số lượng phim hiển thị khớp với kết quả trả về.

### Trường hợp thất bại

- Không có từ khóa.
- Giao diện hiển thị tiêu đề mặc định "Danh sách phim".

---

## Issue 34: [UC03: Tìm kiếm phim]: Thêm dropdown "Trạng thái"

### Nội dung issue

Issue này kiểm tra chức năng lọc phim theo trạng thái trên giao diện trang tìm kiếm. Dropdown "Trạng thái" cho phép người dùng lọc theo 3 lựa chọn: Tất cả, Đang chiếu (`NOW_SHOWING`), Sắp chiếu (`COMING_SOON`). Chức năng lọc thực hiện phía client bằng JavaScript.

### Dữ liệu test

| STT | Dữ liệu test | Kết quả mong đợi | Kết quả |
|---|---|---|---|
| 1 | Chọn "Tất cả" | Hiển thị toàn bộ phim, không ẩn phim nào | Pass |
| 2 | Chọn "Đang chiếu" | Chỉ hiển thị phim có `data-status="NOW_SHOWING"` | Pass |
| 3 | Chọn "Sắp chiếu" | Chỉ hiển thị phim có `data-status="COMING_SOON"` | Pass |
| 4 | Lọc "Đang chiếu" khi không có phim đang chiếu | Danh sách trống, không hiển thị phim nào | Pass |
| 5 | Kết hợp lọc Trạng thái + Thể loại cùng lúc | Hiển thị phim thỏa cả hai điều kiện | Pass |

### Trường hợp thành công

- Dropdown hoạt động đúng với cả 3 lựa chọn.
- Danh sách phim cập nhật ngay khi thay đổi lựa chọn, không cần reload trang.

### Trường hợp thất bại

- Không có phim nào thỏa điều kiện lọc.
- Giao diện hiển thị danh sách trống.

---

## Issue 52: [UC03]: Cập nhật báo cáo UC03 – Tìm kiếm phim

### Nội dung issue

Issue này cập nhật tài liệu báo cáo UC03 và bổ sung cải tiến tìm kiếm hai bước: bước 1 tìm chính xác có dấu trong database, bước 2 nếu không có kết quả thì tìm không dấu bằng cách chuẩn hóa cả từ khóa lẫn tên phim trong Java. Đặc biệt xử lý ký tự `đ/Đ` không phân tách được qua NFD.

### Dữ liệu test

| STT | Dữ liệu test                          | Kết quả mong đợi                                             | Kết quả |
|---|---------------------------------------|--------------------------------------------------------------|---|
| 1 | Tìm "Heo năm móng" (đúng chính tả)    | Tìm thấy phim qua truy vấn database, không qua bước fallback | Pass |
| 2 | Tìm "heo nam mong" (chữ thường)       | Tìm thấy phim nhờ `LOWER()` trong SQL                        | Pass |
| 3 | Tìm "hanh dong" (không dấu, thể loại) | Tìm thấy phim có thể loại "Hành Động" qua bước fallback      | Pass |
| 4 | Tìm "duong pho" (không dấu, có đ)     | Tìm thấy phim có "Đường Phố" qua xử lý `đ→d` trước NFD       | Pass |
| 5 | Tìm "  heo  " (khoảng trắng thừa)     | Keyword được chuẩn hóa về "heo" trước khi tìm                | Pass |
| 6 | Tìm phim bắt đầu bằng keyword         | Phim bắt đầu bằng keyword được ưu tiên xuất hiện trên        | Pass |

### Trường hợp thành công

- Tìm kiếm có dấu: truy vấn thẳng vào database, trả về kết quả.
- Tìm kiếm không dấu: fallback sang lọc trong Java, `đ/Đ` được chuyển về `d/D` trước khi NFD.
- Kết quả sắp xếp theo độ ưu tiên: bắt đầu bằng keyword > chứa keyword.

### Trường hợp thất bại

- Không tìm thấy phim ở cả hai bước.
- Hệ thống trả về danh sách rỗng và hiển thị gợi ý thể loại.

---
