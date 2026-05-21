<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CineBook - Đăng nhập</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body data-page="login">
<header class="site-header">
    <a class="logo" href="${pageContext.request.contextPath}/home">Cine<span>Book</span></a>

    <button class="menu-toggle" aria-label="Mở trình đơn">Trình đơn</button>

    <nav class="main-nav">
        <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/movies">Phim</a>
        <a href="${pageContext.request.contextPath}/showtimes">Lịch chiếu</a>
        <a href="${pageContext.request.contextPath}/booking">Đặt vé</a>
    </nav>

    <div class="header-actions">
        <a class="btn btn-ghost active" href="${pageContext.request.contextPath}/login">Đăng nhập</a>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/register">Đăng ký</a>
    </div>
</header>

<main class="auth-page">
    <section class="auth-card">
        <p class="eyebrow">UC02 - Đăng nhập</p>
        <h1>Chào mừng trở lại</h1>

        <form id="loginForm" action="${pageContext.request.contextPath}/login" method="post">
            <label>Email
                <input type="email" name="email" id="loginEmail"
                       placeholder="student@example.com"
                       value="${email}" required>
            </label>

            <label>Mật khẩu
                <input type="password" name="password" id="loginPassword"
                       placeholder="Mật khẩu của bạn" required>
            </label>

            <p id="loginMessage" class="form-message">${error}</p>

            <button class="btn btn-primary btn-full" type="submit">Đăng nhập</button>
        </form>

        <p class="auth-link">
            Chưa có tài khoản?
            <a href="${pageContext.request.contextPath}/register">Tạo tài khoản</a>
        </p>
    </section>
</main>

</body>
</html>