<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>CineBook - Lịch chiếu</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/showtimes.css">
</head>

<body data-page="showtimes">

<jsp:include page="/header.jsp" />

<main class="showtime-page">
    <section class="showtime-header">
        <p class="eyebrow">UC05 - Xem lịch chiếu</p>

        <h1>Lịch chiếu phim</h1>

        <p class="muted">
            Chọn phim, thể loại hoặc ngày chiếu để tìm suất chiếu phù hợp nhanh hơn.
        </p>
    </section>

    <section class="showtime-filter-box">
        <div class="showtime-filter-title">
            <div>
                <h2>Bộ lọc lịch chiếu</h2>
                <p>Lọc theo tên phim, thể loại phim hoặc ngày chiếu.</p>
            </div>
        </div>

        <form method="get" action="${pageContext.request.contextPath}/showtimes" class="showtime-filter">
            <div class="filter-group">
                <label for="movieId">Tên phim</label>
                <select id="movieId" name="movieId">
                    <option value="">Tất cả phim</option>
                    <c:forEach var="movie" items="${movies}">
                        <option value="${movie.id}" ${movieId == movie.id ? 'selected="selected"' : ''}>
                            ${movie.title}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="filter-group">
                <label for="genreName">Thể loại</label>
                <select id="genreName" name="genreName">
                    <option value="">Tất cả thể loại</option>
                    <c:forEach var="genre" items="${genres}">
                        <option value="${genre}" ${genreName == genre ? 'selected="selected"' : ''}>
                            ${genre}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="filter-group">
                <label for="showDate">Ngày chiếu</label>
                <input id="showDate" type="date" name="showDate" value="${showDate}">
            </div>

            <button type="submit" class="btn btn-primary">
                Lọc lịch chiếu
            </button>

            <a class="btn" href="${pageContext.request.contextPath}/showtimes">
                Xóa lọc
            </a>
        </form>
    </section>

    <div class="showtime-result-summary">
        <c:choose>
            <c:when test="${empty showtimes}">
                Không tìm thấy lịch chiếu phù hợp với bộ lọc hiện tại.
            </c:when>
            <c:otherwise>
                Tìm thấy <strong>${fn:length(showtimes)}</strong> lịch chiếu phù hợp.
            </c:otherwise>
        </c:choose>
    </div>


    <section class="showtime-table">
        <c:choose>
            <c:when test="${empty showtimes}">
                <div class="empty-showtime">
                    <h3>Chưa có lịch chiếu phù hợp</h3>
                    <p>
                        Không tìm thấy suất chiếu theo bộ lọc hiện tại. Bạn có thể chọn ngày khác,
                        đổi phim hoặc xóa bộ lọc để xem toàn bộ lịch chiếu đang mở.
                    </p>

                    <div class="empty-actions">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/showtimes">
                            Xóa bộ lọc
                        </a>
                    </div>
                </div>
            </c:when>

            <c:otherwise>
                <c:forEach var="item" items="${showtimes}">
                    <div class="showtime-card">
                        <div class="showtime-movie">
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
                            <small class="status-pill">Còn vé</small>
                        </div>

                        <a class="btn btn-primary"
                           href="${pageContext.request.contextPath}/booking?showtimeId=${item.id}">
                            Đặt vé
                        </a>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </section>
</main>

<jsp:include page="/footer.jsp" />

</body>
</html>
