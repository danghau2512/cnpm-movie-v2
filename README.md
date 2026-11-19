# CineBook — CNPM Movie v2

## Thành viên mới pull code và chạy Selenium

Làm theo [hướng dẫn từ pull code đến chạy test F01–F08](selenium/HUONG-DAN-PULL-VA-CHAY.md).
Hướng dẫn ngắn dành cho các bạn đã có project và chạy web được: pull code,
kiểm tra file cấu hình có sẵn rồi chạy từng F hoặc full.

Luồng Selenium chính nằm riêng trong `selenium/src/test/java/com/cinebook/tests/f01/` … `f08/`.
Sau khi web và cấu hình đã sẵn sàng, chạy từ thư mục gốc repo:

```powershell
.\selenium\run-selenium.ps1 -Test F01LoginMainTest
.\selenium\run-selenium.ps1 -Test '*MainTest'
```

Chạy full gồm 8 lớp/9 bài. Các testcase chi tiết theo Sheet do thành viên tiếp tục bổ sung trong thư mục F phụ trách.
Xem [phân công thư mục](selenium/README.md) và [liên kết với testcase Sheet](selenium/MAIN-FLOWS.md).

File `selenium/mainflows.local.properties` đã có sẵn tài khoản test dùng chung; pull về không cần tạo lại.
Các tài khoản phải tồn tại trong DB của từng máy. SQL nhận riêng từ nhóm; kết quả chạy không đưa lên GitHub.
