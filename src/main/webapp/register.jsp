<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CineBook - Đăng ký</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body data-page="register">
    <header class="site-header">
        <a class="logo" href="index.jsp">Cine<span>Book</span></a>
        <button class="menu-toggle" aria-label="Mở trình đơn">Trình đơn</button>
        <nav class="main-nav">
            <a href="index.jsp">Trang chủ</a>
            <a href="movies.jsp">Phim</a>
            <a href="showtimes.jsp">Lịch chiếu</a>
            <a href="booking.jsp">Đặt vé</a>
        </nav>
        <div class="header-actions">
            <a class="btn btn-ghost" href="login.jsp">Đăng nhập</a>
            <a class="btn btn-primary active" href="register.html">Đăng ký</a>
        </div>
    </header>

    <main class="auth-page">
        <section class="auth-card">
            <p class="eyebrow">UC01 - Đăng ký</p>
            <h1>Tạo tài khoản</h1>
            <form id="registerForm" novalidate>
                <label>Họ và tên
                    <input type="text" id="fullName" placeholder="Nguyen Van A">
                </label>
                <label>Email
                    <input type="email" id="registerEmail" placeholder="student@example.com">
                </label>
                <label>Số điện thoại
                    <input type="tel" id="phone" placeholder="0901234567">
                </label>
                <label>Mật khẩu
                    <input type="password" id="registerPassword" placeholder="Ít nhất 6 ký tự">
                </label>
                <label>Xác nhận mật khẩu
                    <input type="password" id="confirmPassword" placeholder="Nhập lại mật khẩu">
                </label>
                <p id="registerMessage" class="form-message"></p>
                <button class="btn btn-primary btn-full" type="submit">Đăng ký</button>
            </form>
            <p class="auth-link">Đã có tài khoản? <a href="login.jsp">Đăng nhập tại đây</a></p>
        </section>
    </main>

    <script src="js/main.js"></script>
</body>
</html>
