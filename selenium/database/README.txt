CAI DB DEMO - CHI IMPORT 1 FILE

1. File selenium-demo.sql da gom DB goc movie_ticket_db.sql ban gui va du lieu
   bo sung. KHONG can import file goc hoac hai seed cu them lan nua.
   Dung MySQL 8+; tai khoan import can quyen CREATE DATABASE/ROUTINE.
   Chi import lan dau khi CHUA co 3 DB ten duoi day.
   Mo MySQL CLI (them -p neu MySQL co mat khau):
     mysql --default-character-set=utf8mb4 -u root
   Sau do (doi duong dan theo may ban):
     SOURCE H:/CNPM/cnpm/version2/cnpm-movie-v2/selenium/database/selenium-demo.sql;
   Thay READY o cuoi va khong co ERROR la xong.

2. Cau hinh 3 ban web tren Tomcat (cung WAR/code hien tai):
     URL http://localhost:18084/f02    -> DB cinebook_f02_demo
     URL http://localhost:18084/f03    -> DB cinebook_f03_demo
     URL http://localhost:18084/design -> DB cinebook_design_demo
   Day la URL may Hau; may khac dien cong/context da trien khai thuc te.
   Moi ban web dung WEB-INF/classes/db.properties rieng:
     db.url=jdbc:mysql://localhost:3306/TEN_DB?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh
     db.user=root
     db.password=
   Thay TEN_DB theo bang tren, dien user/password MySQL cua may ban, restart.
   Khong doi ca 3 ban ve chung 1 DB: F03 them phim Horror lam F02 sai so luong.

3. Mo selenium/database/demo.properties: sua 3 URL neu context/port khac;
   sua technique.dbUser/dbPassword theo MySQL. JDBC phai cung DB voi /design.
   Cac tai khoan web da co san trong SQL va config; khong dung tai khoan that.
   Tu thu muc goc repo chay:
     .\selenium\run-techniques.ps1 -Feature all -ConfigFile .\selenium\database\demo.properties
   Chay rieng: thay all bang F01, F02, ... F08. Mac dinh dung 2,5 giay/ca.

41 ca thiet ke; 2 ca VNPay de sau. Cac ca tu tao them phim/don qua giao dien.
F03 co lich D+7/D+8/D+9: import gan ngay demo; neu de qua 7 ngay thi dung
seed F03 cu de lam moi lich (chi khi chua co don tren cac suat 90301-90307).
DB chinh movie_ticket_db khong bi sua. File SQL khong DROP TABLE/DATABASE.
Giu DB demo de doi chieu anh; khong import lai de xoa ket qua cu.
