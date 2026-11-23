# Chạy web bằng IntelliJ / Tomcat 10.1

Các thay đổi local chỉ phục vụ build/deploy và cấu hình DB; source nghiệp vụ trong src được giữ theo Git HEAD.

1. Mở project version2, Reload Gradle.
2. Chọn cấu hình **CineBook - Tomcat (Gradle)** trong thanh Run, bấm ▶.
3. Mở http://localhost:8080/CineBook_war_exploded/home hoặc /login.

Cấu hình chạy prepareIntellijWar trước khi deploy artifact; không trộn class output của Maven/IntelliJ với Gradle.
`src/main/resources/db.properties` là cấu hình DB local, không commit. Có file db.properties.example để sao chép.

Cập nhật bản deploy trên Tomcat đang chạy:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/update-intellij.ps1
```

Script build và đồng bộ đúng WAR, dọn file build cũ, rồi reload context trên Tomcat hiện tại.
Không chạy đồng thời server thứ hai trên cổng 8080.
`out/`, `build/`, `target/` là output, không đưa lên GitHub.
