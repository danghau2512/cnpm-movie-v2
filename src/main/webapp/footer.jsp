<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<style>
    .site-footer {
        background: #0b0712;
        color: #e5e7eb;
        border-top: 1px solid #2b2436;
        padding-top: 40px;
        margin-top: 60px;
    }

    .footer-container {
        width: min(1200px, 92%);
        margin: 0 auto;
        display: grid;
        grid-template-columns: 1.3fr 1fr 1fr 1fr;
        gap: 40px;
        align-items: start;
    }

    .footer-col h3 {
        font-size: 24px;
        margin-bottom: 16px;
        color: #ffffff;
    }

    .footer-logo {
        font-size: 28px;
        margin-bottom: 14px;
        color: #ffffff;
    }

    .footer-logo span {
        color: #f5c542;
    }

    .footer-col p {
        font-size: 15px;
        line-height: 1.7;
        color: #d1d5db;
        margin-bottom: 10px;
    }

    .footer-col ul {
        list-style: none;
        padding: 0;
        margin: 0;
    }

    .footer-col ul li {
        margin-bottom: 10px;
    }

    .footer-col ul li a {
        text-decoration: none;
        color: #d1d5db;
        transition: 0.2s;
    }

    .footer-col ul li a:hover {
        color: #f5c542;
    }

    .footer-bottom {
        width: min(1200px, 92%);
        margin: 30px auto 0;
        padding: 18px 0;
        border-top: 1px solid #2b2436;
        text-align: center;
        color: #9ca3af;
        font-size: 14px;
    }

    @media (max-width: 992px) {
        .footer-container {
            grid-template-columns: repeat(2, 1fr);
        }
    }

    @media (max-width: 600px) {
        .footer-container {
            grid-template-columns: 1fr;
        }
    }
</style>
<footer class="site-footer">
    <div class="footer-container">
        <div class="footer-col">
            <h2 class="footer-logo">Cine<span>Book</span></h2>
            <p>
                CineBook là hệ thống đặt vé xem phim trực tuyến, hỗ trợ người dùng
                xem phim, lịch chiếu và đặt vé nhanh chóng.
            </p>
        </div>

        <div class="footer-col">
            <h3>Liên kết nhanh</h3>
            <ul>
                <li><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                <li><a href="${pageContext.request.contextPath}/movies">Phim</a></li>
                <li><a href="${pageContext.request.contextPath}/showtimes">Lịch chiếu</a></li>
                <li><a href="${pageContext.request.contextPath}/booking">Đặt vé</a></li>
            </ul>
        </div>

        <div class="footer-col">
            <h3>Hỗ trợ</h3>
            <ul>
                <li><a href="#">Hướng dẫn đặt vé</a></li>
                <li><a href="#">Chính sách thanh toán</a></li>
                <li><a href="#">Câu hỏi thường gặp</a></li>
                <li><a href="#">Liên hệ</a></li>
            </ul>
        </div>

        <div class="footer-col">
            <h3>Liên hệ</h3>
            <p>Hotline: 1900 1234</p>
            <p>Email: support@cinebook.vn</p>
            <p>Địa chỉ: TP. Hồ Chí Minh</p>
        </div>
    </div>

    <div class="footer-bottom">
        <p>© 2025 CineBook. Tất cả quyền được bảo lưu.</p>
    </div>
</footer>