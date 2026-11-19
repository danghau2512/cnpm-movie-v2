# Pull code và chạy Selenium F01–F08

Dành cho các bạn đã có project và chạy web được trên máy.

## 1. Pull bản mới

Mở Terminal tại thư mục `cnpm-movie-v2`:

```powershell
git pull --ff-only origin master
```

Nếu có code đang làm, commit trên nhánh của mình trước khi đổi nhánh/pull; không reset/force để xóa code đang làm.

## 2. Kiểm tra file cấu hình đã có sẵn

Sau khi pull, file nằm tại:

```text
cnpm-movie-v2/selenium/mainflows.local.properties
```

Không cần tạo file Notepad hay copy file mẫu nữa. Nếu URL web khác, sửa `baseUrl` trong file.
Ba tài khoản trong file phải tồn tại trong DB trên máy mình: 1 ADMIN/ACTIVE và 2 CUSTOMER/ACTIVE khác nhau.
File này chỉ dùng cho tài khoản test. Nếu cần cấu hình riêng, đặt file ngoài repo và dùng `-ConfigFile <đường dẫn>`.

Không có thay đổi cấu trúc DB cần import thêm cho bộ Selenium này. File cấu hình không tự tạo tài khoản:
nếu DB thiếu Customer B, đăng ký một Customer thử trên web rồi dùng thông tin đó trong cấu hình riêng.

## 3. Bật web và chạy test

Bật MySQL/Tomcat như bình thường, mở được trang đăng nhập rồi chạy từ thư mục gốc repo:

```powershell
.\selenium\run-selenium.ps1 -Test F01LoginMainTest
.\selenium\run-selenium.ps1 -Test F02SearchMainTest
.\selenium\run-selenium.ps1 -Test F03ScheduleMainTest
.\selenium\run-selenium.ps1 -Test F04BookingMainTest
.\selenium\run-selenium.ps1 -Test F05PaymentMainTest
.\selenium\run-selenium.ps1 -Test F06MovieMainTest
.\selenium\run-selenium.ps1 -Test F07ShowtimeMainTest
.\selenium\run-selenium.ps1 -Test F08BookingManagementMainTest
```

Hoặc chạy full luồng chính:

```powershell
.\selenium\run-selenium.ps1 -Test '*MainTest'
```

Full gồm 8 lớp/9 bài. Mỗi bài tự chuẩn bị dữ liệu, không cần chạy F01 trước các F khác.
Các bài sẽ tạo thêm phim, lịch và đơn thử trong DB. Dùng DB thử riêng.

Kết quả đạt: `Tests run: 9, Failures: 0, Errors: 0, Skipped: 0` và `BUILD SUCCESS`.
Ảnh minh chứng nằm trong `selenium/target/evidence/`; báo cáo JUnit trong `selenium/target/surefire-reports/`.

Xem [MAIN-FLOWS.md](MAIN-FLOWS.md) để biết lớp tương ứng từng F và liên kết với Sheet.
