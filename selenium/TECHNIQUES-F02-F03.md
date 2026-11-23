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

F02 dùng DB gốc theo precondition của Sheet: 8 phim gốc, chưa thêm phim F03 vào đó.
Nếu đã thêm các phim Horror thử vào cùng DB, TC003 phải báo sai số lượng; không tự loại phim để làm xanh test.

```powershell
.\selenium\run-selenium.ps1 -Test F02EquivalencePartitionTest -PauseMs 2500
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

F03 cần DB **riêng** tên bắt đầu bằng `cinebook_f03`, đã import SQL gốc của dự án.
Trong MySQL client, chọn DB thử này rồi import fixture kèm repo:

```sql
USE cinebook_f03_test;
SOURCE H:/CNPM/cnpm/version2/cnpm-movie-v2/selenium/src/test/resources/testdata/f03/F03_KAN53_seed.sql;
```

Thay đường dẫn theo máy. Seed dùng D = CURDATE() của MySQL, tạo lại mốc tương lai/quá khứ khi chạy lại.
Seed chỉ dành cho nhóm TC của KAN-53, không phải toàn bộ fixture của 15 ca F03.
Nó từ chối DB gốc `movie_ticket_db`, ID trùng dữ liệu khác hoặc suất fixture đã có booking.
Web chạy F03 phải trỏ đúng DB đã seed; không import fixture F03 vào DB chạy F02.

```powershell
.\selenium\run-selenium.ps1 -Test F03DecisionTableTest -BaseUrl http://localhost:8080/CineBook_war_exploded -PauseMs 2500
```

Muốn dùng 2 bản web/DB và chạy cả hai lớp trong một lệnh, đặt URL tương ứng trong file cấu hình riêng:

```properties
f02.baseUrl=http://localhost:18083/f02
f03.baseUrl=http://localhost:18083/f03
headless=false
technique.pauseMs=2500
```

Giữ các dòng tài khoản admin/customer như cấu hình hiện có rồi chạy:

```powershell
.\selenium\run-selenium.ps1 -Test 'F02EquivalencePartitionTest,F03DecisionTableTest' -ConfigFile .\selenium\techniques.local.properties -PauseMs 2500
```

Không chạy song song các ca F03 vì R4/R5 dùng chung suất 90307. `-PauseMs 2000` đổi thời gian quan sát thành 2 giây.
Hai lớp này không nằm trong mẫu `*MainTest`; muốn chạy kỹ thuật phải chọn tên lớp như trên.

## Lần chạy 20/11/2026

Đã chạy bằng Chrome hiển thị trên hai bản web/DB thử riêng: F02 **5/5 đạt**, F03 **5/5 đạt**;
tổng **10 tests, 0 failures, 0 errors, 0 skipped**. Dừng 2.500 ms ở kết quả mỗi ca.
Suất 90307 được khôi phục OPEN sau R4/R5. DB gốc không được import fixture F03.
