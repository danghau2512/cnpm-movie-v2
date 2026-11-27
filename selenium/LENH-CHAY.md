# Các lệnh Selenium dùng chung cho nhóm

Mở PowerShell tại thư mục gốc `cnpm-movie-v2`. Chạy `git pull --ff-only origin master` trên nhánh `master`.
Các lệnh dưới đây dùng cùng `selenium/database/demo.properties`, do script tự tìm theo vị trí repo. Muốn chọn file khác, thêm `-ConfigFile "đường-dẫn-tới-file.properties"`.

## Cấu hình một lần trên laptop demo

1. Cài JDK 17+, Chrome, MySQL và Tomcat. Nếu đã có `cinebook_design_demo` từ bộ SQL demo trước, dùng lại DB đó. Máy mới import `selenium/database/selenium-demo.sql` một lần.
2. Trong cấu hình `db.properties` của web, đặt `db.url` trỏ tới `cinebook_design_demo` và điền tài khoản MySQL của máy. Khởi động Tomcat và mở `http://localhost:8080/CineBook_war_exploded/login`.
3. Trong `selenium/database/demo.properties`, dùng `baseUrl=http://localhost:8080/CineBook_war_exploded`. Cho `technique.dbUrl` trỏ cùng DB với web và điền `technique.dbUser`, `technique.dbPassword` theo máy. F03 dùng tài khoản này để đọc, thêm và xóa dữ liệu thử.

Chạy từng lệnh nối tiếp trên cùng DB. F03 tự tạo phim và lịch theo ngày chạy rồi dọn khi kết thúc. Nếu tắt ngang tiến trình F03, chạy lại F03 đến hết trước khi chạy F02.

## 1–4. Bốn lệnh tổng

```powershell
# 1. Luồng User, khoảng 44 giây
.\selenium\run-selenium.ps1 -Test LuongUserTest

# 2. Luồng Admin, khoảng 75 giây
.\selenium\run-selenium.ps1 -Test LuongAdminTest

# 3. Tất cả luồng chính F01–F08 (8 lớp, 9 test)
.\selenium\run-selenium.ps1 -Test '*MainTest'

# 4. Tất cả kỹ thuật F01–F08 (41 test, chưa có 2 VNPay)
.\selenium\run-techniques.ps1 -Feature all
```

44/75 giây là phần demo hiển thị; chưa tính Maven, Chrome và chuẩn bị dữ liệu.
Máy/web chậm có thể lâu hơn. Hai bài demo User/Admin tách riêng khỏi `*MainTest`.

## 5. Tám lệnh luồng chính

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

## 6. Tám lệnh kỹ thuật kiểm thử

```powershell
.\selenium\run-techniques.ps1 -Feature F01
.\selenium\run-techniques.ps1 -Feature F02
.\selenium\run-techniques.ps1 -Feature F03
.\selenium\run-techniques.ps1 -Feature F04
.\selenium\run-techniques.ps1 -Feature F05
.\selenium\run-techniques.ps1 -Feature F06
.\selenium\run-techniques.ps1 -Feature F07
.\selenium\run-techniques.ps1 -Feature F08
```

Các kỹ thuật mặc định dừng 2,5 giây tại kết quả. Không chạy đồng thời nhiều bộ test cùng DB.
Ảnh: `selenium/target/evidence/`; báo cáo: `selenium/target/surefire-reports/`.
