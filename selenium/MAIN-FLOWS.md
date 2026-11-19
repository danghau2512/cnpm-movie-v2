# Luồng Selenium chính theo từng F01–F08

Mỗi chức năng có lớp test riêng trong thư mục F tương ứng. Có thể bấm Run từng lớp trong IntelliJ
hoặc chạy từng lớp bằng script. Các lớp không phụ thuộc thứ tự F01 → F08 và không dùng chung đơn từ lần chạy trước.

## Vị trí và lệnh chạy

Các thư mục dưới đây nằm trong `selenium/src/test/java/com/cinebook/tests/`.

| Thư mục | Lớp test | Luồng chính | Ca Sheet tham chiếu về hành vi |
|---|---|---|---|
| f01 | F01LoginMainTest | Customer/Admin đăng nhập đúng trang đích và đăng xuất | F01_Login_004, 006, 008 |
| f02 | F02SearchMainTest | Tìm phim theo tên, kiểm tra thẻ kết quả | F02_Search_002 |
| f03 | F03ScheduleMainTest | Lọc phim/ngày, chọn đúng lịch và mở sơ đồ ghế | F03_Schedule_015 |
| f04 | F04BookingMainTest | Chọn một ghế, kiểm tra số lượng/tổng và tạo đơn | F04_Seat_002 |
| f05 | F05PaymentMainTest | Chọn thanh toán tại quầy, đơn vẫn chờ/chưa thanh toán | F05_Payment_002, 004 |
| f06 | F06MovieMainTest | Admin thêm phim và đọc lại thông tin/thể loại | F06_Movie_003 |
| f07 | F07ShowtimeMainTest | Admin thêm suất OPEN tương lai, đọc lại dữ liệu | F07_Showtime_003 |
| f08 | F08BookingManagementMainTest | Hai bài riêng: xác nhận thu tiền; hủy đơn và khách khác đặt lại ghế | F08_Booking_010, 012; F05_Payment_003 |

Ví dụ từ thư mục gốc repo:

```powershell
# Chạy riêng chức năng đang làm
.\selenium\run-selenium.ps1 -Test F01LoginMainTest
.\selenium\run-selenium.ps1 -Test F02SearchMainTest
.\selenium\run-selenium.ps1 -Test F03ScheduleMainTest
.\selenium\run-selenium.ps1 -Test F04BookingMainTest
.\selenium\run-selenium.ps1 -Test F05PaymentMainTest
.\selenium\run-selenium.ps1 -Test F06MovieMainTest
.\selenium\run-selenium.ps1 -Test F07ShowtimeMainTest
.\selenium\run-selenium.ps1 -Test F08BookingManagementMainTest

# Chạy tất cả lớp luồng chính trong tám thư mục (9 tests)
.\selenium\run-selenium.ps1 -Test '*MainTest'

# Chạy một nhánh của F08 hoặc chạy Chrome ẩn
.\selenium\run-selenium.ps1 -Test 'F08BookingManagementMainTest#cancelAndReleaseSeat'
.\selenium\run-selenium.ps1 -Test '*MainTest' -Headless
```

## Cấu hình một lần

1. Chạy Tomcat/MySQL với DB thử đã import SQL dự án, có thể truy cập web bình thường.
2. Chuẩn bị tài khoản ADMIN/ACTIVE và CUSTOMER/ACTIVE; riêng F08 cần hai Customer khác nhau.
3. Repo đã có file cấu hình tài khoản test. Mở để kiểm tra URL/tài khoản nếu máy mình khác:

```powershell
notepad .\selenium\mainflows.local.properties
```

Điền URL, email/mật khẩu tài khoản thử, phòng ACTIVE có ghế (`flow.roomId`, mặc định 1).
File cấu hình test dùng chung được commit theo yêu cầu của nhóm. Chỉ dùng với DB thử;
nếu cần cấu hình riêng, đặt ngoài repo rồi truyền bằng `-ConfigFile` để không ghi đè bản chung.
Cần JDK 17+ và Chrome; Maven Wrapper nằm trong repo.
Trong IntelliJ, Add as Maven Project cho `selenium/pom.xml`, rồi Run lớp trong `f01` … `f08`.
Working directory là `selenium`; nếu dùng thư mục khác, đặt VM option
`-Dcinebook.configFile=<đường dẫn tuyệt đối tới file cấu hình>`.
Script cũng hỗ trợ `-ConfigFile <đường dẫn>` và `-BaseUrl <URL>`.

## Các phần liên kết nhau thế nào?

`pages/` chứa thao tác trang dùng chung; `base/FeatureMainTest` chỉ cung cấp điều kiện chuẩn bị bằng UI.
Phương thức `@Test` và hành vi chính nằm trong từng lớp `f01` … `f08`.

- F01 dùng tài khoản đã cấu hình.
- F02 cần phim: bước SETUP tạo phim thử riêng.
- F03/F04 cần suất: SETUP tạo phim và suất OPEN tương lai, đăng nhập Customer.
- F05 cần đơn: SETUP tạo đơn chưa thanh toán rồi dừng tại trang thanh toán.
- F06 chỉ chuẩn bị phiên Admin; phần thêm phim nằm trong bài test F06.
- F07 chuẩn bị phim; phần tạo lịch nằm trong bài test F07.
- F08 chuẩn bị đơn tại quầy; phần tìm/xác nhận/hủy đơn nằm trong bài test F08.

SETUP được ghi riêng trong minh chứng, không coi là kết quả testcase của chức năng khác.
Mỗi lớp tự chuẩn bị điều kiện cần nên chạy riêng F05 hay F08 vẫn được, không phải chạy F01/F06/F07 trước.

## Đối chiếu Sheet và minh chứng

Nguồn: [Sheet testcase của nhóm](https://docs.google.com/spreadsheets/d/1fMU4NnRac8B86_i1nFsXD4N7bxilqZirKD_RNowpDPE/edit).
Mã ca trong bảng là tham chiếu hành vi. Các test chính dùng phim `SEL_MAIN_<UUID>`, ID/ngày sinh khi chạy,
giá 80.000 đồng; không tái hiện nguyên dữ liệu cố định của từng ca Sheet.
Các bước đối chiếu DB trong Sheet vẫn cần kiểm tra riêng; các lớp này xác nhận bằng giao diện.
Thành viên tiếp tục viết các testcase chi tiết và kỹ thuật kiểm thử ngay trong thư mục F của mình.

- Ảnh từng bước và `steps.log`: `selenium/target/evidence/<lần chạy>-<lớp>-<phương thức>/`.
- Kết quả JUnit: `selenium/target/surefire-reports/`.
- Dữ liệu được tạo qua form web, không dùng JDBC/DAO/API trong bộ Selenium.
- Mỗi lần chạy tạo thêm phim/suất/đơn thử; dữ liệu được giữ để đối chiếu minh chứng.
- Thời hạn giữ ghế vẫn theo ứng dụng. Không chạy đồng thời người khác sửa lịch cùng DB.
- `target/` bị Git bỏ qua. Lỗi thực tế được ném cho JUnit, không đổi lỗi thành Pass.

## Kết quả xác minh

Ngày 19/11/2026, chạy cả tám lớp bằng Chrome headless trên Tomcat/MySQL với bản sao web và DB riêng:
**9 tests, 0 failures, 0 errors, 0 skipped**. F01–F07 mỗi lớp 1 bài; F08 có 2 bài độc lập.
Kết quả này chỉ áp dụng cho các luồng chính đã viết, không đại diện toàn bộ testcase trong Sheet.
