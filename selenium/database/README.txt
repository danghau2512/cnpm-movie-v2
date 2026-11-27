CAI DAT MOT DB DUNG CHUNG CHO F01-F08

1. Neu da import bo demo cu va co cinebook_design_demo, dung lai DB nay.
   Tren may moi, mo MySQL 8+ CLI (them -p neu co mat khau):
     mysql --default-character-set=utf8mb4 -u root
   Import mot lan, doi duong dan cho dung thu muc repo tren may:
     SOURCE H:/CNPM/cnpm/version2/cnpm-movie-v2/selenium/database/selenium-demo.sql;
   Kiem tra khong co ERROR va co READY o cuoi. SQL da gom du lieu goc va
   tai khoan demo. Tai khoan import can quyen tao database va routine.

2. Trien khai CineBook tai http://localhost:8080/CineBook_war_exploded.
   Trong WEB-INF/classes/db.properties cua web, cau hinh:
     db.url=jdbc:mysql://localhost:3306/cinebook_design_demo?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh
     db.user=root
     db.password=
   Dien user/password MySQL cua may, khoi dong lai Tomcat va mo /login.

3. Trong selenium/database/demo.properties, kiem tra baseUrl tro dung web,
   technique.dbUrl tro cung DB va technique.dbUser/dbPassword dung voi MySQL.
   Tai khoan JDBC can quyen SELECT, INSERT va DELETE cho du lieu thu cua F03.
   Tu thu muc goc repo chay:
     .\selenium\run-techniques.ps1 -Feature all -ConfigFile .\selenium\database\demo.properties
   Chay rieng tung F bang cach thay all bang F01, F02, ... F08.

Chay cac lenh noi tiep. F03 tu tao va don phim/lich theo ngay demo.
Neu tat ngang Maven khi F03 dang chay, chay lai F03 den het truoc F02.
Xem selenium/LENH-CHAY.md de lay day du 20 lenh.
