# CineBook · Trình bày kết quả kiểm thử F02 và F03

**Hoàng Trần Bảo · Nhóm 3 · 28/11/2026**

Bản dùng để thu minh chứng là commit [`bfa07e3`](https://github.com/danghau2512/cnpm-movie-v2/commit/bfa07e3ea843778d77d2f3bbf147590ed79acae9), chạy trên Tomcat cổng 8080 với MySQL demo. Nhánh `Hoang` dùng phiên bản đó để trình bày các lỗi đang mở.

## Mở code khi trình bày

| Bug / test case | Kết quả để trình bày | Code cần mở |
| --- | --- | --- |
| `BUG_F02_01` / `F02_Search_014` | Ngày 28/11, cùng một từ khóa hiển thị khác nhau giữa tiêu đề và thông báo. Ca XSS có kết quả Fail lịch sử trên KAN-27. Khả năng thực thi XSS trên bản hiện tại chưa được xác nhận. | [`SearchController.java:76`](../src/main/java/Controller/SearchController.java#L76-L81) tạo thông báo. [`movies.jsp:156`](../src/main/webapp/movies.jsp#L156) dùng `c:out` cho tiêu đề, còn [`movies.jsp:171`](../src/main/webapp/movies.jsp#L171) in trực tiếp nội dung thông báo. |
| `BUG_F02_02` / `F02_Search_016` | Tìm **Horror**, chọn **Sắp chiếu**. Tiêu đề báo 2 phim, dòng đếm và danh sách hiển thị 0. | [`movies.jsp:158`](../src/main/webapp/movies.jsp#L158) in số phim khi tải trang. [`movies.jsp:344–360`](../src/main/webapp/movies.jsp#L344-L360) cập nhật số phim còn hiển thị sau khi lọc. |
| `BUG_F02_03` / `F02_Search_017` | Với từ khóa không khớp, trang hiện hai thông báo không tìm thấy phim. | [`SearchController.java:76–81`](../src/main/java/Controller/SearchController.java#L76-L81) đặt `message`. [`movies.jsp:169–171`](../src/main/webapp/movies.jsp#L169-L171) in thông báo đó và [`movies.jsp:214–218`](../src/main/webapp/movies.jsp#L214-L218) in thêm thông báo khi danh sách rỗng. |
| `BUG_F03_01` / `F03_Schedule_010` | Khi mã phim là chữ, trang lịch chiếu trả HTTP 500. | [`ShowtimeService.java:27–40`](../src/main/java/Service/ShowtimeService.java#L27-L40) xử lý mã phim không hợp lệ khi truy vấn. [`ShowtimeController.java:35–41`](../src/main/java/Controller/ShowtimeController.java#L35-L41) vẫn đưa giá trị ban đầu sang JSP. [`showtimes.jsp:43`](../src/main/webapp/showtimes.jsp#L43) so sánh giá trị đó với ID dạng số. |
| `BUG_F03_02` / `F03_Schedule_016` | Cùng một suất chiếu báo **Còn vé**, nhưng sơ đồ khóa 32/32 ghế. | [`showtimes.jsp:130–137`](../src/main/webapp/showtimes.jsp#L130-L137) hiển thị nhãn cố định. [`SeatDAO.java:29–49`](../src/main/java/Dao/SeatDAO.java#L29-L49) tính trạng thái từng ghế. [`booking.jsp:236–244`](../src/main/webapp/booking.jsp#L236-L244) khóa các ghế đã đặt. |

## Mở minh chứng và kết quả

Mở `Phieu_minh_chung_F02_F03.pdf` trong gói `MinhChung_Bao_F02_F03_20261128.zip`. Trang 2–6 lần lượt tương ứng với năm dòng trong bảng trên. Riêng ca hết vé có hai ảnh của cùng suất 90309. Suất này được tạo trong dữ liệu demo và đã được dọn sau khi chụp.

- [Google Sheet F02, hai ca bổ sung ở dòng 23–24](https://docs.google.com/spreadsheets/d/1fMU4NnRac8B86_i1nFsXD4N7bxilqZirKD_RNowpDPE/edit#gid=2069634708&range=B23:I24)
- [Google Sheet F03, ca bổ sung ở dòng 23](https://docs.google.com/spreadsheets/d/1fMU4NnRac8B86_i1nFsXD4N7bxilqZirKD_RNowpDPE/edit#gid=94883431&range=B23:I23)
- [Jira KAN-27, hồ sơ kiểm thử F02](https://softwaretesting4.atlassian.net/browse/KAN-27)
- [Jira KAN-51, hồ sơ kiểm thử F03](https://softwaretesting4.atlassian.net/browse/KAN-51)

Có thể mở riêng mã Selenium của [luồng chính F02](../selenium/src/test/java/com/cinebook/tests/f02/F02SearchMainTest.java) và [luồng chính F03](../selenium/src/test/java/com/cinebook/tests/f03/F03ScheduleMainTest.java) để trình bày cách nhóm tự động hóa hai chức năng. Kết quả từng lỗi trong bảng lấy từ phiên thu minh chứng và hồ sơ được dẫn ở trên.
