# Các lệnh Selenium dùng chung cho nhóm

Mở PowerShell tại thư mục gốc `cnpm-movie-v2`. Pull đủ code Selenium mới.
Các lệnh dưới đây dùng cùng `selenium/database/demo.properties`, không ghi cứng URL trong lệnh.

## Cấu hình một lần trên laptop demo

1. Cài JDK 17+, Chrome, MySQL và Tomcat. Nếu đã có `cinebook_design_demo` từ bộ SQL demo trước, dùng lại DB đó. Máy mới import `selenium/database/selenium-demo.sql` một lần.
2. Trong cấu hình `db.properties` của web, đặt `db.url` trỏ tới `cinebook_design_demo` và điền tài khoản MySQL của máy. Khởi động Tomcat và mở `http://localhost:8080/CineBook_war_exploded/login`.
3. Trong `selenium/database/demo.properties`, dùng `baseUrl=http://localhost:8080/CineBook_war_exploded`. Cho `technique.dbUrl` trỏ cùng DB với web và điền `technique.dbUser`, `technique.dbPassword` theo máy. F03 dùng tài khoản này để đọc, thêm và xóa dữ liệu thử.

Chạy từng lệnh nối tiếp trên cùng DB. F03 tự tạo phim và lịch theo ngày chạy rồi dọn khi kết thúc. Nếu tắt ngang tiến trình F03, chạy lại F03 đến hết trước khi chạy F02.

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
