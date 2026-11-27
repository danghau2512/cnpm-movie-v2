# Các lệnh Selenium dùng chung cho nhóm

Mở PowerShell tại thư mục gốc `cnpm-movie-v2`. Pull đủ code Selenium mới.
Các lệnh dưới đây dùng cùng `selenium/database/demo.properties`, không ghi cứng URL trong lệnh.

## Cấu hình một lần trên mỗi máy

- Cần JDK 17+, Chrome, MySQL và Tomcat đang chạy code hiện tại.
- Import `selenium/database/selenium-demo.sql` một lần nếu chưa có 3 DB demo.
- Triển khai 3 bản web cùng code, mỗi bản dùng `WEB-INF/classes/db.properties` riêng:

| Khóa trong demo.properties | URL mặc định | DB của bản web |
|---|---|---|
| baseUrl | http://localhost:8080/CineBook_war_exploded | cinebook_design_demo |
| f02.baseUrl | http://localhost:8080/f02 | cinebook_f02_demo |
| f03.baseUrl | http://localhost:8080/f03 | cinebook_f03_demo |

Trên máy khác, sửa **3 URL** theo các bản web thực tế. Sửa `technique.dbUrl`,
`technique.dbUser`, `technique.dbPassword` theo MySQL; JDBC phải cùng DB với `baseUrl`.
Đăng nhập mỗi URL `/login` phải mở được trước khi chạy. Import SQL không tự triển khai web.
Mở ngày demo đủ gần lúc import: F03 có lịch D+7/D+8/D+9 và bắt buộc còn trong tương lai.
Nếu cần làm mới lịch F03, xem `TECHNIQUES-F02-F03.md`; không import lại SQL tổng đè kết quả.

Không gom F02 vào DB design: các test quản lý phim tạo dữ liệu mới sẽ làm sai số phim F02.
Các lệnh giống nhau trên mọi máy; URL/DB/mật khẩu là cấu hình của từng máy.

## 1–4. Bốn lệnh tổng

```powershell
# 1. Luồng User, khoảng 44 giây
.\selenium\run-selenium.ps1 -Test LuongUserTest -ConfigFile .\selenium\database\demo.properties

# 2. Luồng Admin, khoảng 75 giây
.\selenium\run-selenium.ps1 -Test LuongAdminTest -ConfigFile .\selenium\database\demo.properties

# 3. Tất cả luồng chính F01–F08 (8 lớp, 9 test)
.\selenium\run-selenium.ps1 -Test '*MainTest' -ConfigFile .\selenium\database\demo.properties

# 4. Tất cả kỹ thuật F01–F08 (41 test, chưa có 2 VNPay)
.\selenium\run-techniques.ps1 -Feature all -ConfigFile .\selenium\database\demo.properties
```

44/75 giây là phần demo hiển thị; chưa tính Maven, Chrome và chuẩn bị dữ liệu.
Máy/web chậm có thể lâu hơn. Hai bài demo User/Admin tách riêng khỏi `*MainTest`.

## 5. Tám lệnh luồng chính

```powershell
.\selenium\run-selenium.ps1 -Test F01LoginMainTest -ConfigFile .\selenium\database\demo.properties
.\selenium\run-selenium.ps1 -Test F02SearchMainTest -ConfigFile .\selenium\database\demo.properties
.\selenium\run-selenium.ps1 -Test F03ScheduleMainTest -ConfigFile .\selenium\database\demo.properties
.\selenium\run-selenium.ps1 -Test F04BookingMainTest -ConfigFile .\selenium\database\demo.properties
.\selenium\run-selenium.ps1 -Test F05PaymentMainTest -ConfigFile .\selenium\database\demo.properties
.\selenium\run-selenium.ps1 -Test F06MovieMainTest -ConfigFile .\selenium\database\demo.properties
.\selenium\run-selenium.ps1 -Test F07ShowtimeMainTest -ConfigFile .\selenium\database\demo.properties
.\selenium\run-selenium.ps1 -Test F08BookingManagementMainTest -ConfigFile .\selenium\database\demo.properties
```

## 6. Tám lệnh kỹ thuật kiểm thử

```powershell
.\selenium\run-techniques.ps1 -Feature F01 -ConfigFile .\selenium\database\demo.properties
.\selenium\run-techniques.ps1 -Feature F02 -ConfigFile .\selenium\database\demo.properties
.\selenium\run-techniques.ps1 -Feature F03 -ConfigFile .\selenium\database\demo.properties
.\selenium\run-techniques.ps1 -Feature F04 -ConfigFile .\selenium\database\demo.properties
.\selenium\run-techniques.ps1 -Feature F05 -ConfigFile .\selenium\database\demo.properties
.\selenium\run-techniques.ps1 -Feature F06 -ConfigFile .\selenium\database\demo.properties
.\selenium\run-techniques.ps1 -Feature F07 -ConfigFile .\selenium\database\demo.properties
.\selenium\run-techniques.ps1 -Feature F08 -ConfigFile .\selenium\database\demo.properties
```

Các kỹ thuật mặc định dừng 2,5 giây tại kết quả. Không chạy đồng thời nhiều bộ test cùng DB.
Ảnh: `selenium/target/evidence/`; báo cáo: `selenium/target/surefire-reports/`.
