<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>CineBook - Lịch chiếu</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        .showtime-page {
            padding: 3rem 6%;
        }

        .showtime-header {
            margin-bottom: 2rem;
        }

        .showtime-header h1 {
            font-size: clamp(2rem, 4vw, 4rem);
            margin-bottom: 0.8rem;
        }

        .showtime-table {
            display: grid;
            gap: 1rem;
        }

        .showtime-filter {
            display: flex;
            align-items: center;
            gap: 0.8rem;
            margin-top: 1.5rem;
            flex-wrap: wrap;
        }

        .showtime-filter input[type="date"] {
            padding: 0.75rem 1rem;
            border: 1px solid var(--line);
            border-radius: 0.6rem;
            background: rgba(21, 21, 29, 0.88);
            color: var(--text);
            outline: none;
        }

        .showtime-card {
            display: grid;
            grid-template-columns: 1.4fr 0.8fr 0.8fr 0.8fr 0.8fr auto;
            gap: 1rem;
            align-items: center;
            padding: 1.2rem;
            border: 1px solid var(--line);
            border-radius: 0.75rem;
            background: rgba(21, 21, 29, 0.88);
        }

        .showtime-card h3 {
            margin: 0 0 0.3rem;
        }

        .showtime-card p {
            margin: 0;
            color: var(--muted);
        }

        .showtime-info span {
            display: block;
            color: var(--gold-soft);
            font-weight: 700;
            margin-bottom: 0.2rem;
        }

        .showtime-info small {
            color: var(--muted);
        }

        .empty-showtime {
            padding: 2rem;
            border: 1px dashed rgba(240, 184, 74, 0.55);
            border-radius: 0.75rem;
            color: var(--gold-soft);
            text-align: center;
        }

        @media (max-width: 1000px) {
            .showtime-card {
                grid-template-columns: 1fr 1fr;
            }
        }

        @media (max-width: 600px) {
            .showtime-card {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>

<body data-page="showtimes">

<jsp:include page="/header.jsp" />

<main class="showtime-page">
    <section class="showtime-header">
        <p class="eyebrow">UC05 - Xem lịch chiếu</p>

        <h1>Lịch chiếu phim</h1>

        <c:choose>
            <c:when test="${not empty movieId}">
                <p class="muted">
                    Danh sách suất chiếu của phim đã chọn.
                </p>
            </c:when>

            <c:otherwise>
                <p class="muted">
                    Danh sách các suất chiếu đang mở trong hệ thống.
                </p>
            </c:otherwise>
        </c:choose>
    </section>

    <section class="showtime-table">
        <c:choose>
            <c:when test="${empty showtimes}">
                <div class="empty-showtime">
                    Hiện chưa có lịch chiếu phù hợp.
                </div>
            </c:when>

            <c:otherwise>
                <c:forEach var="item" items="${showtimes}">
                    <div class="showtime-card">
                        <div>
                            <h3>${item.movieTitle}</h3>
                            <p>${item.roomName}</p>
                        </div>

                        <div class="showtime-info">
                            <span>Ngày chiếu</span>
                            <small>${item.showDate}</small>
                        </div>

                        <div class="showtime-info">
                            <span>Giờ chiếu</span>
                            <small>${item.showTime}</small>
                        </div>

                        <div class="showtime-info">
                            <span>Giá vé</span>
                            <small>${item.priceText} VNĐ</small>
                        </div>

                        <div class="showtime-info">
                            <span>Trạng thái</span>
                            <small>Còn vé</small>
                        </div>
                        <a class="btn btn-primary"
                           href="${pageContext.request.contextPath}/booking?showtimeId=${item.id}">
                            Đặt vé
                        </a>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>

        <form method="get" action="${pageContext.request.contextPath}/showtimes" class="showtime-filter">
            <c:if test="${not empty movieId}">
                <input type="hidden" name="movieId" value="${movieId}">
            </c:if>

            <input type="date" name="showDate" value="${showDate}">

            <button type="submit" class="btn btn-primary">
                Lọc lịch chiếu
            </button>

            <a class="btn" href="${pageContext.request.contextPath}/showtimes">
                Xóa lọc
            </a>
        </form>

    </section>
</main>

<jsp:include page="/footer.jsp" />

</body>
</html>