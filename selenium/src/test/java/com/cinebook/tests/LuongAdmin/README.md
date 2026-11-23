# Demo Admin khoảng 75 giây

Từ thư mục gốc dự án, sau khi web/MySQL đã chạy:

```powershell
.\selenium\run-selenium.ps1 -Test LuongAdminTest -ConfigFile .\selenium\database\demo.properties -BaseUrl http://localhost:18084/design
```

Thay `-BaseUrl` bằng URL web thực tế trên máy bạn. Cổng 18084 chỉ là bản web
kiểm thử hiện chạy trên máy Hậu. Dùng DB demo, tài khoản Admin/Customer và
phòng hoạt động có ghế trong config. Không cần import thêm SQL.

Luồng hiển thị: đăng nhập Admin → tổng quan → nhập/thêm/sửa phim, chọn thể loại
→ thêm lịch (phim/phòng/ngày/giờ/giá) → sửa giá và đóng lịch → xóa lịch chưa có đơn
→ xóa phim thử → tìm mã đơn → lọc trạng thái → xác nhận thu tiền tại quầy
→ hủy một đơn khác → đăng xuất. Có kiểm tra kết quả từng thao tác.

Trước demo, test dùng Chrome ẩn tạo phim/suất và hai đơn tại quầy qua UI.
Phần chuẩn bị và khởi động Chrome không nằm trong 75 giây; máy/web chậm có thể
khiến luồng dài hơn. Chỉ sửa/xóa phim, lịch, đơn do lần chạy này tạo ra.
Phim/suất dùng cho hai đơn và kết quả xử lý được giữ lại để đối chiếu;
ảnh tại `selenium/target/evidence/`. Không kiểm các trường hợp ngoại lệ.
