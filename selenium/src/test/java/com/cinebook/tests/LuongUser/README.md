# Demo luồng khách khoảng 44 giây

Chạy từ thư mục gốc repo (web và MySQL đã bật):

```powershell
.\selenium\run-selenium.ps1 -Test LuongUserTest -ConfigFile .\selenium\database\demo.properties
```

Dùng URL `baseUrl` trong config, mặc định `/design` với DB `cinebook_design_demo`.
Có thể dùng config hiện tại bằng cách thay đường dẫn `-ConfigFile`.
Nếu `/design` ở cổng 8080 trả 404, phải dùng đúng URL web đã triển khai.
Trên máy Hậu, bản web kiểm thử hiện chạy ở cổng 18084:

```powershell
.\selenium\run-selenium.ps1 -Test LuongUserTest -ConfigFile .\selenium\database\demo.properties -BaseUrl http://localhost:18084/design
```

`-BaseUrl` ghi đè URL cho lần chạy này. Các máy khác điền URL thực tế;
import SQL không tự triển khai web `/design` vào Tomcat.
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
