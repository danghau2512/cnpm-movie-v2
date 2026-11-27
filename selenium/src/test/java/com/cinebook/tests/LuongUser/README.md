# Demo luồng khách khoảng 44 giây

Chạy từ thư mục gốc repo (web và MySQL đã bật):

```powershell
.\selenium\run-selenium.ps1 -Test LuongUserTest -ConfigFile .\selenium\database\demo.properties
```

Dùng `baseUrl` trong cấu hình chung, mặc định là http://localhost:8080/CineBook_war_exploded với DB `cinebook_design_demo`.
Cần dữ liệu phim gốc ID 2, thể loại Horror, phòng 1 hoạt động có ghế,
tài khoản Admin và Customer trong config. Không cần thêm SQL mới.

Test tự tạo một suất chiếu tương lai cho phim Phí Phông bằng phiên Admin ẩn.
Phần này và khởi động Chrome nằm ngoài 44 giây demo khách.

Luồng: nhập tài khoản → đăng nhập → danh sách phim → tìm theo tên → lọc thể loại
→ lọc độ tuổi → lọc đang chiếu → chi tiết phim → chọn phim/thể loại/ngày chiếu
→ chọn suất → chọn ghế, kiểm tổng tiền → chọn tại quầy → đặt vé thành công
→ kiểm lịch sử. Có assertion ở từng chức năng; ảnh tại `selenium/target/evidence/`.

Thanh toán tại quầy giữ đơn Chờ thanh toán / Chưa thanh toán, chưa phải trả tiền.
Không gồm chức năng Admin, đăng ký hay các trường hợp ngoại lệ. VNPay để sau.
44 giây là mốc quan sát mục tiêu; máy hoặc web chậm có thể lâu hơn, test vẫn chờ
phần tử thật và kiểm kết quả. Mỗi lần chạy tạo một suất và đơn demo mới.
