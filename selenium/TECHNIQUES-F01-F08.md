# Selenium theo tab Thiết kế ca kiểm thử

Mỗi ca là một hàm `@Test` trong đúng thư mục F; mặc định dừng **2,5 giây ở kết quả**.
Ảnh và log: `selenium/target/evidence/`. Kết quả JUnit: `selenium/target/surefire-reports/`.

Nguồn: [tab Thiết kế ca kiểm thử](https://docs.google.com/spreadsheets/d/1fMU4NnRac8B86_i1nFsXD4N7bxilqZirKD_RNowpDPE/edit?gid=2026111405).

| F | Lớp test | Số ca | Liên kết với thiết kế |
|---|---|---:|---|
| F01 | F01EquivalencePartitionTest | 4 | Login_008,009,001,005: hợp lệ, chưa đăng ký, sai mật khẩu, LOCKED |
| F02 | F02EquivalencePartitionTest | 5 | Search_002–006: 4 phân vùng, thêm biến thể ba dấu cách |
| F03 | F03DecisionTableTest | 5 | Schedule_005,006,011,012,013: 5 quy tắc hiển thị lịch |
| F04 | F04DecisionTableTest | 6 | Seat_002,006,001,008,007,018: đúng ghế, CLOSED, chưa chọn, đang giữ, đã đặt, sai phòng |
| F05 | F05PaymentStateTest | 3 | Payment_002,003,008: chọn tại quầy, xác nhận, từ chối thanh toán lại |
| F06 | F06DurationBoundaryTest | 6 | Movie_006–009 và hai dòng bổ sung thời lượng 2/599 |
| F07 | F07ShowtimeDecisionTest | 9 | Showtime_003,009,010,004,011,012 và ba dòng biên 19:59/20:00/20:01 |
| F08 | F08BookingStateTest | 3 | Booking_010,012,017: xác nhận, hủy, từ chối hủy từ tab cũ |

Hai dòng F05 VNPay Sandbox (Payment_001 và Payment_009) **để sau theo yêu cầu người dùng**,
không được tính là Pass hoặc thay bằng callback giả.
Tổng hiện có **41 ca cho 43 dòng thiết kế**, gồm 10 F02/F03 đã làm trước và 31 ca còn lại.
Năm dòng chưa có ID dùng nhãn `DESIGN_...`, không tự gán mã TC chính thức hoặc sửa Sheet.

## Chuẩn bị trên laptop demo

Dùng chung web `http://localhost:8080/CineBook_war_exploded` và DB `cinebook_design_demo` cho F01–F08. Xem [hướng dẫn cấu hình và 20 lệnh](LENH-CHAY.md). Bộ SQL demo đã có tài khoản LOCKED, hai Customer ACTIVE, Admin và phòng thử INACTIVE.

`TestDatabase` dùng SELECT để đối chiếu kết quả. F03 dùng JDBC để thêm và dọn phim, suất chiếu dành cho bảng quyết định. Các thao tác nghiệp vụ vẫn đi qua trình duyệt.

F04 ghế sai phòng và F05 thanh toán lại dùng form POST trong chính phiên trình duyệt như Procedure trong Sheet.
Không đổi mật khẩu hoặc khóa tài khoản thật; tài khoản LOCKED là fixture riêng của seed.

`pageLoadStrategy=eager` chờ DOM sẵn sàng, không đợi tải hết ảnh; việc chờ phần tử vẫn dùng WebDriverWait.
Khoảng dừng 2,5 giây chỉ để quan sát sau assertion, không dùng để che lỗi chờ tải trang.

## Chạy

Từ thư mục gốc repo, sau khi web/DB thử đã sẵn sàng:

```powershell
# Một F riêng
.\selenium\run-techniques.ps1 -Feature F01
.\selenium\run-techniques.ps1 -Feature F04
.\selenium\run-techniques.ps1 -Feature F05
.\selenium\run-techniques.ps1 -Feature F06
.\selenium\run-techniques.ps1 -Feature F07
.\selenium\run-techniques.ps1 -Feature F08

# 31 ca mới, chưa gồm F02/F03 và VNPay
.\selenium\run-techniques.ps1 -Feature remaining

# Có thể đổi thời gian quan sát hoặc cấu hình
.\selenium\run-techniques.ps1 -Feature F06 -PauseMs 2000
.\selenium\run-techniques.ps1 -Feature remaining -ConfigFile C:\testing\design.properties
```

Dùng `run-techniques.ps1 -Feature all` để chạy 41 ca trên cùng web và DB. Xem [dữ liệu F02/F03](TECHNIQUES-F02-F03.md).

Các test kỹ thuật không nằm trong `*MainTest`; các lớp luồng chính vẫn chạy riêng như trước.

## Phạm vi đối chiếu

- Các ca có mã TC giữ hành vi và Expected của Sheet; dữ liệu tạo mới dùng tên/ID/ngày phát sinh để chạy độc lập.
  F04 không tái sử dụng cứng suất 9; F07 dùng phim thử 120 phút thay ID 2 và chọn ngày tương lai trống.
  Chúng kiểm tra các quy tắc thiết kế, không chứng nhận bộ dữ liệu cố định ban đầu của toàn bộ ca chi tiết.
- F06 kiểm tra khoảng 1–600 đúng như thiết kế đang ghi; Sheet còn yêu cầu nhóm duyệt giới hạn này.
  Kết quả chạy xác nhận hành vi theo khoảng đó, không tự chốt yêu cầu thay nhóm.
- F06 giá trị 0/601 được kiểm tra chặn native validation và DB không thêm bản ghi; không coi đó là kiểm tra server độc lập.
- F07 mỗi điều kiện không hợp lệ đều được thay riêng; 19:59/20:00/20:01 kiểm quanh suất kết thúc 20:00.
- F08 ca tab cũ dùng hai tab cùng phiên Admin, xác nhận ở tab mới rồi hủy ở tab chưa tải lại.
- Mỗi lần chạy tạo thêm dữ liệu thử. Không chạy đồng thời người khác sửa cùng DB; giữ dữ liệu để đối chiếu ảnh.

## Xác minh ngày 20/11/2026

Đã chạy bằng Chrome hiển thị trên bản sao web/MySQL, dừng 2.500 ms/ca:

| Phần | Đạt | Lỗi/không đạt |
|---|---:|---:|
| F01 | 4 | 0 |
| F04 | 6 | 0 |
| F05 tại quầy | 3 | 0 |
| F06 | 6 | 0 |
| F07 | 9 | 0 |
| F08 | 3 | 0 |
| Tổng ca mới | 31 | 0 |

F02/F03 đã chạy trước đó: 10/10 đạt. Hai ca VNPay chưa thực hiện, theo yêu cầu để sau.
Kết quả F06 áp dụng theo khoảng 1–600 đang ghi trong thiết kế và vẫn giữ ghi chú cần duyệt yêu cầu.
