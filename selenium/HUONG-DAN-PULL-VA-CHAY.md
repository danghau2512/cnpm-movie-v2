# Từ pull code đến chạy Selenium F01–F08

Làm theo thứ tự dưới đây trên máy của từng thành viên. Nếu web trên máy đã chạy và đăng nhập được,
có thể bỏ qua phần dựng web và chuyển tới bước 7.

## Bước 1 — Chuẩn bị phần mềm và file nhận từ nhóm

- Git, JDK 17 (đặt `JAVA_HOME` đúng thư mục JDK), Chrome, MySQL, Tomcat **10.1**.
- File SQL dự án do nhóm gửi riêng. Repo hiện không chứa file SQL khởi tạo DB.
- File cấu hình Selenium Hậu gửi qua Notepad, nếu có.

Không cần cài Maven riêng hoặc tự tải ChromeDriver: repo có Maven Wrapper và Selenium Manager.
Lần chạy đầu cần Internet để tải thư viện/driver.

## Bước 2 — Clone lần đầu hoặc pull bản mới

**Máy chưa có repo:**

```powershell
git clone https://github.com/danghau2512/cnpm-movie-v2.git
cd cnpm-movie-v2
git switch master
```

**Máy đã có repo:** mở PowerShell/Terminal tại thư mục `cnpm-movie-v2`:

```powershell
git status
git switch master
git pull --ff-only origin master
```

Nếu có code đang làm, lưu bằng commit trên nhánh của mình trước khi đổi nhánh/pull.
Nếu Git báo conflict hoặc không thể fast-forward, xử lý phần đó trước; không dùng reset/force để xóa code đang làm.

Tất cả lệnh PowerShell phía dưới đều chạy từ **thư mục gốc `cnpm-movie-v2`**, nơi có `mvnw.cmd` và `selenium/`.

## Bước 3 — Tạo DB thử và import SQL

Khởi động MySQL. Ví dụ tạo DB thử riêng tên `cinebook_selenium` bằng MySQL Workbench, Navicat hoặc phpMyAdmin:

```sql
CREATE DATABASE cinebook_selenium CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Chọn đúng DB này rồi import file SQL nhóm gửi. File dump có lệnh `DROP TABLE`, nên dùng DB thử mới,
không import đè vào DB có công việc đang làm. Nếu dùng MySQL Command Line Client:

```sql
USE cinebook_selenium;
SOURCE C:/duong-dan/movie_ticket_db.sql;
```

Thay đường dẫn bằng file SQL thực tế; trong MySQL dùng dấu `/` cho đường dẫn Windows.
Kiểm tra có dữ liệu trong `users`, `movies`, `rooms`, `seats`, `showtimes`.
Phải có phòng ACTIVE và ghế; mặc định Selenium dùng phòng ID 1.

## Bước 4 — Tạo file kết nối DB của web

Chỉ copy mẫu nếu máy chưa có file cấu hình này:

```powershell
Copy-Item .\src\main\resources\db.properties.example .\src\main\resources\db.properties
notepad .\src\main\resources\db.properties
```

Nếu đã có file, chỉ mở sửa, không ghi đè cấu hình đang dùng.
Ví dụ khi dùng DB thử bước 3:

```properties
db.url=jdbc:mysql://localhost:3306/cinebook_selenium?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh
db.user=root
db.password=
```

Sửa `db.user`, `db.password`, cổng và tên DB theo MySQL trên máy mình.
Chỉ để `db.password=` rỗng khi tài khoản MySQL thật sự không có mật khẩu.
Lưu file với tên **db.properties**, không phải `db.properties.txt`. File này được Git bỏ qua.

## Bước 5 — Build web thành WAR

```powershell
java -version
.\mvnw.cmd "-Dmaven.test.skip=true" package
```

Phải thấy `BUILD SUCCESS` và file `target/ROOT.war`.
Lệnh này build web; các bài Selenium sẽ chạy riêng ở bước 9.
Sau khi sửa `db.properties`, cần build và deploy lại WAR để Tomcat nhận cấu hình mới.

## Bước 6 — Deploy và chạy Tomcat

Nếu đã có cấu hình Tomcat trong IntelliJ hoạt động, deploy WAR mới qua cấu hình đó rồi mở web kiểm tra.
Hoặc dùng cách WAR dưới đây trên **bản Tomcat 10.1 local mới**, ví dụ đặt tại `C:\Tomcat`:

```powershell
# Thay hai đường dẫn theo máy mình
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
$teamTomcat = 'C:\Tomcat'

Copy-Item .\target\ROOT.war "$teamTomcat\webapps\CineBook_war_exploded.war"
& "$teamTomcat\bin\startup.bat"
```

Đợi Tomcat deploy rồi mở:

```text
http://localhost:8080/CineBook_war_exploded/login
```

Chỉ chạy một server trên cổng 8080. Nếu dùng IntelliJ thì không đồng thời bật thêm `startup.bat`.
Nếu đã có bản deploy cũ, dừng server và cập nhật đúng deployment đang dùng; tránh lấy nhầm thư mục WAR cũ.
Nếu context/cổng khác, ghi đúng URL thực tế vào cấu hình Selenium ở bước 7.

## Bước 7 — Tạo file cấu hình Selenium

### Nếu nhận file Notepad Hậu gửi

Đặt nội dung file vào đường dẫn:

```text
cnpm-movie-v2/selenium/mainflows.local.properties
```

Có thể copy trực tiếp bằng PowerShell, kể cả file nhận được có đuôi `.txt`:

```powershell
Copy-Item 'C:\duong-dan\file-Hau-gui.txt' .\selenium\mainflows.local.properties
notepad .\selenium\mainflows.local.properties
```

Thay đường dẫn nguồn bằng file nhận được. Đích phải có tên chính xác **mainflows.local.properties**,
không có `.txt` phía sau. Không cần copy mẫu khi đã dùng file Hậu gửi.

### Nếu chưa nhận file cấu hình

```powershell
Copy-Item .\selenium\mainflows.local.properties.example .\selenium\mainflows.local.properties
notepad .\selenium\mainflows.local.properties
```

Điền các giá trị:

```properties
baseUrl=http://localhost:8080/CineBook_war_exploded
headless=false
flow.admin.email=EMAIL_ADMIN
flow.admin.password=MAT_KHAU_ADMIN
flow.customer.email=EMAIL_CUSTOMER_A
flow.customer.password=MAT_KHAU_CUSTOMER_A
flow.otherCustomer.email=EMAIL_CUSTOMER_B
flow.otherCustomer.password=MAT_KHAU_CUSTOMER_B
flow.roomId=1
flow.daysAhead=14
flow.screenshots=true
```

`baseUrl` không có `/login` hoặc `/home` ở cuối.
File này bị Git bỏ qua; mỗi người giữ bản riêng. Không đưa mật khẩu vào file mẫu để push lên GitHub.

## Bước 8 — Kiểm tra tài khoản trước khi chạy

- Admin phải có role ADMIN, trạng thái ACTIVE.
- Hai Customer phải là hai tài khoản khác nhau, role CUSTOMER, trạng thái ACTIVE.
- Thử đăng nhập bằng các thông tin đã điền trên web đang chạy.

**File Notepad chỉ chứa cấu hình, không tự tạo tài khoản trong DB.** Nếu DB vừa import thiếu Customer B
hoặc mật khẩu khác file được gửi, tạo Customer thử qua trang Đăng ký rồi sửa email/mật khẩu trong file cấu hình.
Admin phải dùng tài khoản Admin có sẵn của nhóm trong DB đó. Không đổi mật khẩu tài khoản khác chỉ để chạy test.

## Bước 9 — Chạy riêng từng chức năng hoặc chạy full

Chạy F01 trước để kiểm tra URL và đăng nhập:

```powershell
.\selenium\run-selenium.ps1 -Test F01LoginMainTest
```

Chạy chức năng phụ trách:

```powershell
.\selenium\run-selenium.ps1 -Test F02SearchMainTest
.\selenium\run-selenium.ps1 -Test F03ScheduleMainTest
.\selenium\run-selenium.ps1 -Test F04BookingMainTest
.\selenium\run-selenium.ps1 -Test F05PaymentMainTest
.\selenium\run-selenium.ps1 -Test F06MovieMainTest
.\selenium\run-selenium.ps1 -Test F07ShowtimeMainTest
.\selenium\run-selenium.ps1 -Test F08BookingManagementMainTest
```

Chạy full các luồng chính:

```powershell
.\selenium\run-selenium.ps1 -Test '*MainTest'
```

Full là **8 lớp, 9 bài** vì F08 có hai bài. Mỗi bài tự chuẩn bị dữ liệu, không cần chạy F01 trước các F khác.
Đây là các luồng chính đã viết, không phải toàn bộ testcase trong Sheet.
Chrome tự mở để demo. Muốn chạy ẩn, thêm `-Headless`:

```powershell
.\selenium\run-selenium.ps1 -Test '*MainTest' -Headless
```

Nếu PowerShell chặn chạy file `.ps1`, dùng lệnh cho lần chạy đó:

```powershell
powershell -ExecutionPolicy Bypass -File .\selenium\run-selenium.ps1 -Test '*MainTest'
```

## Bước 10 — Xem kết quả và tiếp tục làm bài của mình

Lần chạy full đạt sẽ có:

```text
Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

- Ảnh từng bước và `steps.log`: `selenium/target/evidence/`.
- Kết quả JUnit: `selenium/target/surefire-reports/`.
- Lớp test: `selenium/src/test/java/com/cinebook/tests/f01/` … `f08/`.
- Thành viên viết thêm testcase chi tiết/kỹ thuật kiểm thử tại đúng thư mục F của mình, dùng lại `pages/` khi phù hợp.
- Mỗi lần chạy tạo thêm phim, suất và đơn thử. Dùng DB thử riêng; giữ dữ liệu để đối chiếu minh chứng.

Sau mỗi lần pull code web mới: build/deploy lại web nếu phần web thay đổi. Sau khi chỉ sửa lớp Selenium,
chạy lại lệnh test; Maven tự biên dịch test mới. File cấu hình riêng vẫn ở máy mình.

## Lỗi hay gặp

| Biểu hiện | Kiểm tra |
|---|---|
| Không kết nối được localhost:8080 | MySQL/Tomcat đã bật chưa, có nhầm cổng không |
| Web 404 | Context phải khớp tên WAR/deployment; đợi deploy, xem log Tomcat |
| Web 500 | DB import đủ chưa, `db.properties` đúng chưa, đã build/deploy lại chưa |
| Missing flow... hoặc còn YOUR_... | File đúng tên/đúng thư mục chưa, đã điền và lưu chưa |
| Sai tài khoản/role | Email/mật khẩu và ADMIN/CUSTOMER/ACTIVE trong DB đang kết nối |
| F08 không chạy với khách khác | Hai Customer có khác nhau và cùng tồn tại trong DB chưa |
| Không có phòng/ghế phù hợp | `flow.roomId` phải là phòng ACTIVE có dữ liệu ghế |

Xem thêm [bảng liên kết với Sheet và phạm vi các luồng](MAIN-FLOWS.md).
