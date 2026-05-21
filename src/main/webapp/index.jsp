<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CineBook - Trang chủ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<style>
    .user-name {
        color: #ffffff;
        font-weight: 700;
        font-size: 15px;
        white-space: nowrap;
    }
    .home-page {
        min-height: calc(100vh - 80px);
        background: radial-gradient(circle at top left, rgba(220, 38, 38, 0.25), transparent 35%),
        linear-gradient(135deg, #08070c, #120916);
        color: #fff;
    }

    .hero {
        padding: 90px 100px;
        position: relative;
        overflow: hidden;
        isolation: isolate;
        border-bottom: 1px solid rgba(255, 255, 255, 0.12);
    }

    .hero::before {
        content: "";
        position: absolute;
        inset: -12px;
        z-index: -2;
        background-image: url("${pageContext.request.contextPath}/images/banner-home.jpg");
        background-size: cover;
        background-position: center right;
        opacity: 0.58;
        filter: blur(3px);
        transform: scale(1.03);
    }

    .hero::after {
        content: "";
        position: absolute;
        inset: 0;
        z-index: -1;
        background:
            linear-gradient(90deg, rgba(8, 7, 12, 0.98) 0%, rgba(8, 7, 12, 0.86) 36%, rgba(8, 7, 12, 0.36) 72%, rgba(8, 7, 12, 0.12) 100%),
            linear-gradient(180deg, rgba(8, 7, 12, 0.2), rgba(8, 7, 12, 0.64));
    }

    .hero-content {
        max-width: 720px;
        position: relative;
        z-index: 1;
    }

    .home-page .hero {
        font-family: "Segoe UI", Arial, Helvetica, sans-serif;
    }

    .hero h1 {
        max-width: 760px;
        font-size: clamp(42px, 5vw, 62px);
        line-height: 1.08;
        font-weight: 800;
        margin: 16px 0 20px;
    }

    .hero .eyebrow {
        font-size: 15px;
        line-height: 1.45;
        font-weight: 800;
        color: #f5c542;
    }

    .hero p {
        max-width: 760px;
        color: #d1d5db;
        font-size: 18px;
        line-height: 1.75;
        font-weight: 400;
    }

    @media (max-width: 700px) {
        .hero {
            padding: 56px 24px;
        }

        .hero h1 {
            font-size: clamp(34px, 10vw, 44px);
        }

        .hero p {
            font-size: 16px;
        }
    }

    .hero-actions {
        display: flex;
        gap: 16px;
        margin-top: 28px;
    }

    .feature-section {
        padding: 40px 100px 80px;
    }

    .feature-section h2 {
        margin-bottom: 24px;
    }

    .feature-grid {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 24px;
    }

    .feature-card {
        background: #15141c;
        border: 1px solid #2d2a36;
        border-radius: 18px;
        padding: 28px;
    }

    .feature-card h3 {
        margin-bottom: 12px;
        color: #f5c542;
    }

    .feature-card p {
        color: #cbd5e1;
        line-height: 1.6;
        margin-bottom: 16px;
    }

    .feature-card a {
        color: #f5c542;
        text-decoration: none;
        font-weight: 700;
    }

</style>
<body data-page="home">

<jsp:include page="/header.jsp" />

<main class="home-page">
    <section class="hero">
        <div class="hero-content">
            <p class="eyebrow">CineBook - Đặt vé xem phim trực tuyến</p>

            <h1>Đặt vé xem phim dễ dàng, nhanh chóng</h1>

            <p>
                Khám phá các bộ phim đang chiếu, xem lịch chiếu phù hợp
                và đặt vé trực tuyến ngay trên hệ thống.
            </p>

            <div class="hero-actions">
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/movies">
                    Xem phim
                </a>

                <a class="btn btn-ghost" href="${pageContext.request.contextPath}/showtimes">
                    Xem lịch chiếu
                </a>
            </div>
        </div>
    </section>

    <jsp:include page="/footer.jsp" />

</main>

</body>
</html>
