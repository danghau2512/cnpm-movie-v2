# KAN-53: F02 phân vùng tương đương / F03 bảng quyết định

Nguồn: [KAN-53](https://softwaretesting4.atlassian.net/browse/KAN-53),
[KAN-8](https://softwaretesting4.atlassian.net/browse/KAN-8), [KAN-10](https://softwaretesting4.atlassian.net/browse/KAN-10)
và tab F02/F03 trong [Sheet của nhóm](https://docs.google.com/spreadsheets/d/1fMU4NnRac8B86_i1nFsXD4N7bxilqZirKD_RNowpDPE/edit).

Code nằm trong đúng thư mục `tests/f02` và `tests/f03`, không thay các lớp luồng chính đã có.
Mỗi ca là một hàm `@Test` riêng, tên hàm ngắn và có mã TC trong `@DisplayName`.
Mặc định giữ màn hình kết quả **2.500 ms mỗi ca**. Ảnh và log nằm trong `selenium/target/evidence/`.

## F02 — 4 phân vùng, 5 ca

| Phân vùng | Đại diện | Mã TC | Kỳ vọng |
|---|---|---|---|
| EP1: khớp tên | Phí Phông | F02_Search_002 | Đúng 1 phim, thông tin theo Sheet |
| EP2: khớp thể loại | Horror | F02_Search_003 | Đúng 2 phim Horror theo Sheet |
| EP3: hợp lệ nhưng không khớp | ZZZKHONGCOPHIM987 | F02_Search_004 | 0 phim, thông báo và gợi ý thể loại |
| EP4: không hợp lệ | Chuỗi rỗng | F02_Search_005 | Yêu cầu nhập từ khóa, 0 phim |
| EP4: biến thể cùng nhóm | Ba dấu cách U+0020 | F02_Search_006 | Yêu cầu nhập từ khóa, 0 phim |

DB demo có 8 phim gốc. F02 kiểm tra chính xác tên và số lượng phim Horror theo Sheet.
Nếu đã thêm các phim Horror thử vào cùng DB, TC003 phải báo sai số lượng; không tự loại phim để làm xanh test.

```powershell
.\selenium\run-selenium.ps1 -Test F02EquivalencePartitionTest -ConfigFile .\selenium\database\demo.properties -PauseMs 2500
```

## F03 — 5 quy tắc đại diện

| Quy tắc | Trạng thái | Thời gian | Khớp lọc | Hiển thị | Mã TC |
|---|---|---|---|---|---|
| R1 | OPEN | Tương lai | Có | Có: 90301,90302 | F03_Schedule_005 |
| R2 | OPEN | Tương lai | Không: Action | Không | F03_Schedule_006 |
| R3 | OPEN | Đã qua | Có | Không: 90304 | F03_Schedule_011 |
| R4 | CLOSED | Tương lai | Có | Không: 90305 / 90307 sau đổi | F03_Schedule_012 |
| R5 | CANCELLED | Tương lai | Có | Không: 90306 / 90307 sau đổi | F03_Schedule_013 |

Đây là 5 quy tắc đại diện KAN-53, không phải mọi tổ hợp điều kiện.
R4/R5 còn kiểm tra trước/sau đổi trạng thái, URL đặt vé cũ và khôi phục OPEN trong `finally`.
Admin thao tác qua một Chrome ẩn riêng, Customer giữ nguyên phiên trong Chrome hiển thị.
Tài khoản đọc từ cấu hình hiện có; không dùng mật khẩu riêng ghi trong Sheet.
R3 không tự kết luận các nhánh đúng giây bắt đầu (Q01) hoặc URL cũ sau bắt đầu (Q04) chưa chốt.

F02 và F03 dùng chung `cinebook_design_demo` và một bản web. Khi bắt đầu F03, Selenium dùng JDBC để thêm 3 phim và 7 suất cho 5 quy tắc trên. Các mốc thời gian được tính theo ngày chạy trên MySQL. Khi lớp test kết thúc, Selenium dọn các dòng này để F02 vẫn thấy đúng hai phim Horror. Nếu tắt ngang Maven, chạy lại F03 đến hết trước F02.

Từ thư mục gốc repo, dùng kế tiếp hai lệnh sau để demo từng phần:

```powershell
.\selenium\run-techniques.ps1 -Feature F02 -ConfigFile .\selenium\database\demo.properties
.\selenium\run-techniques.ps1 -Feature F03 -ConfigFile .\selenium\database\demo.properties
```

Không chạy song song các ca F03 vì R4/R5 dùng chung suất 90307. `-PauseMs 2000` đổi thời gian quan sát thành 2 giây.
Hai lớp này không nằm trong mẫu `*MainTest`; muốn chạy kỹ thuật phải chọn tên lớp như trên.

## Lần chạy 20/11/2026

Đã chạy bằng Chrome hiển thị trên hai bản web/DB thử riêng: F02 **5/5 đạt**, F03 **5/5 đạt**;
tổng **10 tests, 0 failures, 0 errors, 0 skipped**. Dừng 2.500 ms ở kết quả mỗi ca.
Suất 90307 được khôi phục OPEN sau R4/R5. DB gốc không được import fixture F03.
