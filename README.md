# CineBook — CNPM Movie v2

## Thành viên mới pull code và chạy Selenium

Làm theo [hướng dẫn từ pull code đến chạy test F01–F08](selenium/HUONG-DAN-PULL-VA-CHAY.md).
Tài liệu gồm import SQL, tạo file kết nối DB, build/deploy web lên Tomcat 10.1,
đặt file cấu hình Notepad nhận từ nhóm và chạy test.

Luồng Selenium chính nằm riêng trong `selenium/src/test/java/com/cinebook/tests/f01/` … `f08/`.
Sau khi web và cấu hình đã sẵn sàng, chạy từ thư mục gốc repo:

```powershell
.\selenium\run-selenium.ps1 -Test F01LoginMainTest
.\selenium\run-selenium.ps1 -Test '*MainTest'
```

Chạy full gồm 8 lớp/9 bài. Các testcase chi tiết theo Sheet do thành viên tiếp tục bổ sung trong thư mục F phụ trách.
Xem [phân công thư mục](selenium/README.md) và [liên kết với testcase Sheet](selenium/MAIN-FLOWS.md).

SQL và cấu hình có mật khẩu nhận riêng từ nhóm; file cấu hình riêng và kết quả chạy không đưa lên GitHub.
