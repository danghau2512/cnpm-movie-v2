# Khung Selenium chung của nhóm CineBook

Repo: https://github.com/danghau2512/cnpm-movie-v2

**Bắt đầu tại [hướng dẫn từ pull code đến chạy test](HUONG-DAN-PULL-VA-CHAY.md).** Có đủ bước import DB,
tạo file cấu hình web, nhận file Notepad, chạy web và chạy từng F hoặc full.

Đã có lớp Selenium luồng chính riêng ngay trong từng thư mục `tests/f01` … `tests/f08`.
Xem [MAIN-FLOWS.md](MAIN-FLOWS.md) để cấu hình tài khoản, chạy demo và đối chiếu Sheet.
Testcase chi tiết của từng chức năng vẫn do thành viên phụ trách viết trong `tests/f01` … `tests/f08`.

## Viết test ở đâu?

Các đường dẫn dưới đây tính từ thư mục `selenium/`:

| Thư mục | Chức năng | Phụ trách theo Sheet |
|---|---|---|
| `src/test/java/com/cinebook/tests/f01/` | Đăng nhập | Nguyễn Đăng Hậu |
| `src/test/java/com/cinebook/tests/f02/` | Tìm kiếm phim | Hoàng Trần Bảo |
| `src/test/java/com/cinebook/tests/f03/` | Xem lịch chiếu | Hoàng Trần Bảo |
| `src/test/java/com/cinebook/tests/f04/` | Chọn ghế và đặt vé | Bùi Văn Hiếu |
| `src/test/java/com/cinebook/tests/f05/` | Thanh toán | Bùi Văn Hiếu |
| `src/test/java/com/cinebook/tests/f06/` | Quản lý phim | Hoàng Lê Huy |
| `src/test/java/com/cinebook/tests/f07/` | Quản lý lịch chiếu | Hoàng Lê Huy |
| `src/test/java/com/cinebook/tests/f08/` | Quản lý đơn vé | Nguyễn Đăng Hậu |

Các thành phần chung trong `src/test/java/com/cinebook/`:

- `base/BaseTest.java`: mở/đóng Chrome, URL, WebDriverWait. Không tự đăng nhập hoặc sửa DB.
- `pages/`: Page Objects dùng chung cho đăng nhập, tìm phim, lịch, ghế, thanh toán và quản trị.
- `utils/`: đọc cấu hình tài khoản riêng và chụp minh chứng từng bước.
- `tests/f01/` … `tests/f08/`: mỗi thư mục có lớp `Fxx...MainTest` để chạy luồng chính riêng; thành viên bổ sung testcase chi tiết tại đây.

`src/test/resources/selenium.properties`: cấu hình URL và headless.
`src/test/resources/testdata/`: dữ liệu nhóm tự chuẩn bị theo Sheet.

## Chạy

1. Clone repo, cài JDK 17+ và Chrome.
2. Chạy web bằng Tomcat trước. URL mặc định: `http://localhost:8080/CineBook_war_exploded`.
3. Trong IntelliJ, mở `selenium/pom.xml` dưới dạng Maven project hoặc chọn Add as Maven Project.
4. Tạo lớp `...Test extends com.cinebook.base.BaseTest` trong `tests/fXX` của mình.
5. Nhóm tự viết `@Test` và assertion theo mã ca/Expected trong Sheet.

Từ thư mục gốc repo web:

```powershell
.\selenium\run-selenium.ps1
.\selenium\run-selenium.ps1 -Test TenLopTest
.\selenium\run-selenium.ps1 -Test 'TenLopTest#tenPhuongThuc'
.\selenium\run-selenium.ps1 -BaseUrl http://localhost:8080/CineBook_war_exploded -Headless
```

Script dùng Maven Wrapper có sẵn ở gốc repo. Chưa có test thì build thành công chỉ xác nhận
cấu hình biên dịch được, chưa phải Pass F01–F08. Báo cáo khi có test: `selenium/target/surefire-reports`.

## Làm chung

Nguồn: https://docs.google.com/spreadsheets/d/1fMU4NnRac8B86_i1nFsXD4N7bxilqZirKD_RNowpDPE/edit

- Mỗi người tạo nhánh riêng, ví dụ `test/f02-search`, sửa đúng chức năng được phân công.
- Giữ Expected trong Sheet; đối chiếu tab Thiết kế ca kiểm thử trước khi viết assertion.
- Ghi mã TC trong tên phương thức hoặc DisplayName; ca bổ sung chưa có ID cần nhóm thống nhất.
- Không sửa nghiệp vụ web hoặc dữ liệu gốc để làm test xanh.
- Không commit output build, ảnh tự sinh, mật khẩu thật hoặc cấu hình IDE riêng.
