<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CineBook - Kết quả tìm kiếm phim</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/movie.css">
</head>
<style>
    .movie-poster {
        height: 260px;
        position: relative;
        overflow: hidden;
        border-radius: 0.45rem;
        background: linear-gradient(135deg, #2b1010, #15151d);
    }

    .movie-poster-img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        display: block;
        border-radius: 0.45rem;
    }

    .movie-poster::after {
        content: "";
        position: absolute;
        inset: 0;
        background: linear-gradient(180deg, transparent 45%, rgba(0, 0, 0, 0.75));
        pointer-events: none;
    }

    .age-tag {
        position: absolute;
        top: 12px;
        left: 12px;
        z-index: 2;
        background: #f5c542;
        color: #111827;
        padding: 6px 10px;
        border-radius: 8px;
        font-weight: 800;
    }

    .poster-placeholder {
        height: 100%;
        padding: 18px;
        display: flex;
        align-items: flex-end;
        background: linear-gradient(135deg, #4c0519, #b91c1c 45%, #f59e0b);
    }

    .poster-placeholder h3 {
        position: relative;
        z-index: 2;
        color: #ffffff;
        font-size: 22px;
    }

    .page-title {
        display: grid;
        grid-template-columns: repeat(2, minmax(0, 1fr));
        column-gap: 18px;
        row-gap: 10px;
    }

    .page-title h1,
    .page-title p {
        grid-column: 1 / -1;
    }

    .page-title label {
        min-width: 0;
    }

    .status-badge {
        display: inline-block;
        font-size: 0.7rem;
        font-weight: 700;
        padding: 2px 8px;
        border-radius: 4px;
        margin-bottom: 4px;
        text-transform: uppercase;
        letter-spacing: 0.05em;
    }

    .status-badge.now-showing {
        background: rgba(76, 175, 80, 0.2);
        color: #4caf50;
        border: 1px solid #4caf50;
    }

    .status-badge.coming-soon {
        background: rgba(240, 184, 74, 0.2);
        color: var(--gold-soft);
        border: 1px solid var(--gold-soft);
    }

    .genre-suggestions {
        margin-top: 1rem;
        padding: 1rem 1.2rem;
        border: 1px dashed rgba(240, 184, 74, 0.4);
        border-radius: 0.75rem;
        background: rgba(240, 184, 74, 0.05);
    }

    .genre-suggestions p {
        color: var(--muted);
        margin-bottom: 0.6rem;
        font-size: 0.9rem;
    }

    .genre-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem;
    }

    .genre-tag {
        display: inline-block;
        padding: 4px 12px;
        border-radius: 999px;
        border: 1px solid rgba(240, 184, 74, 0.6);
        color: var(--gold-soft);
        font-size: 0.85rem;
        text-decoration: none;
        transition: background 0.2s;
    }

    .genre-tag:hover {
        background: rgba(240, 184, 74, 0.15);
    }

    @media (max-width: 700px) {
        .page-title {
            grid-template-columns: 1fr;
        }
    }
</style>
<body data-page="movies">

<jsp:include page="/header.jsp" />

<main class="page-shell">
    <section class="page-title">
        <%-- UC03 - 3.1.12: Hiển thị khu vực kết quả tìm kiếm phim cho khách hàng --%>
        <p class="eyebrow">UC03 - Tìm kiếm phim</p>

        <c:choose>
            <c:when test="${not empty keyword}">
                <h1>Kết quả cho: &ldquo;${keyword}&rdquo;</h1>
                <p class="muted">
                    Tìm thấy <strong>${movies.size()}</strong> phim phù hợp.
                </p>
            </c:when>
            <c:otherwise>
                <h1>Danh sách phim</h1>
                <p class="muted">
                    Tìm phim theo tên hoặc thể loại. Đang hiển thị <strong>${movies.size()}</strong> phim.
                </p>
            </c:otherwise>
        </c:choose>

        <c:if test="${not empty message}">
            <%-- UC03 - 3.2.1 + 3.2.3: Hiển thị thông báo khi keyword rỗng hoặc không tìm thấy phim --%>
            <p class="empty-message">${message}</p>

            <c:if test="${not empty suggestedGenres}">
                <div class="genre-suggestions">
                    <p>Bạn có thể thử tìm theo thể loại phổ biến:</p>
                    <div class="genre-tags">
                        <c:forEach var="genre" items="${suggestedGenres}">
                            <a class="genre-tag"
                               href="${pageContext.request.contextPath}/search?keyword=${genre}">
                                ${genre}
                            </a>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
        </c:if>
        <label>Thể loại
            <select id="genreFilter">
                <option value="">Tất cả thể loại</option>
            </select>
        </label>
        <label>Độ tuổi
            <select id="ratingFilter">
                <option value="">Tất cả độ tuổi</option>
            </select>
        </label>
        <label>Trạng thái
            <select id="statusFilter">
                <option value="">Tất cả</option>
                <option value="NOW_SHOWING">Đang chiếu</option>
                <option value="COMING_SOON">Sắp chiếu</option>
            </select>
        </label>
    </section>



    <%-- UC03 - 3.1.12: Hiển thị số lượng phim tìm được theo keyword --%>
    <p class="muted movie-count">
        Có ${movies.size()} phim được hiển thị.
    </p>

    <div class="movie-grid" id="movieList">
        <c:choose>
            <c:when test="${empty movies}">
                <%-- UC03 - 3.2.3: Hiển thị trạng thái không có kết quả tìm kiếm --%>
                <p class="empty-message">
                    Không tìm thấy phim phù hợp. Hãy thử tên phim khác.
                </p>
            </c:when>

            <c:otherwise>
                <%-- UC03 - 3.1.12: Duyệt danh sách phim phù hợp và hiển thị lên giao diện --%>
                <c:forEach var="movie" items="${movies}">
                    <div class="movie-card" data-movie-card data-genre="${movie.genreNames}" data-rating="${movie.ageRating}" data-status="${movie.status}">
                        <div class="movie-poster">
                            <span class="age-tag">${movie.ageRating}</span>

                            <c:choose>
                                <c:when test="${not empty movie.posterUrl}">
                                    <img src="${movie.posterUrl}" alt="${movie.title}" class="movie-poster-img">
                                </c:when>

                                <c:otherwise>
                                    <div class="poster-placeholder">
                                        <h3>${movie.title}</h3>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="movie-info">
                            <c:choose>
                                <c:when test="${movie.status == 'NOW_SHOWING'}">
                                    <span class="status-badge now-showing">Đang chiếu</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge coming-soon">Sắp chiếu</span>
                                </c:otherwise>
                            </c:choose>

                            <h3>${movie.title}</h3>

                            <p>Thời lượng: ${movie.durationMinutes} phút</p>
                            <p>Độ tuổi: ${movie.ageRating}</p>
                            <p>Thể loại: ${movie.genreNames}</p>

                            <div class="movie-actions">
                                <a class="btn btn-ghost"
                                   href="${pageContext.request.contextPath}/movie-detail?id=${movie.id}">
                                    Chi tiết
                                </a>

                                <c:choose>
                                    <c:when test="${movie.status == 'NOW_SHOWING'}">
                                        <a class="btn btn-primary"
                                           href="${pageContext.request.contextPath}/showtimes?movieId=${movie.id}">
                                            Đặt vé
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn btn-primary" disabled style="opacity:0.4;cursor:not-allowed;">
                                            Sắp chiếu
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>

        <p class="empty-message hidden" id="noFilteredMovies">
            Không có phim phù hợp với bộ lọc đang chọn.
        </p>
    </div>
</main>

<jsp:include page="/footer.jsp" />

<script>
    document.addEventListener("DOMContentLoaded", function () {
        var genreFilter = document.getElementById("genreFilter");
        var ratingFilter = document.getElementById("ratingFilter");
        var statusFilter = document.getElementById("statusFilter");
        var movieCount = document.querySelector(".movie-count");
        var noFilteredMovies = document.getElementById("noFilteredMovies");
        var movieCards = Array.prototype.slice.call(document.querySelectorAll("[data-movie-card]"));

        if (!genreFilter || !ratingFilter || !movieCount) {
            return;
        }

        function getGenres(card) {
            return (card.dataset.genre || "")
                .split(",")
                .map(function (genre) {
                    return genre.trim();
                })
                .filter(Boolean);
        }

        function addOptions(select, values) {
            values
                .filter(function (value, index, array) {
                    return value && array.indexOf(value) === index;
                })
                .sort(function (first, second) {
                    return first.localeCompare(second, "vi");
                })
                .forEach(function (value) {
                    var option = document.createElement("option");
                    option.value = value;
                    option.textContent = value;
                    select.appendChild(option);
                });
        }

        addOptions(genreFilter, movieCards.flatMap(getGenres));
        addOptions(ratingFilter, movieCards.map(function (card) {
            return card.dataset.rating || "";
        }));

        function filterMovies() {
            var selectedGenre = genreFilter.value;
            var selectedRating = ratingFilter.value;
            var selectedStatus = statusFilter ? statusFilter.value : "";
            var visibleCount = 0;

            movieCards.forEach(function (card) {
                var matchesGenre = !selectedGenre || getGenres(card).indexOf(selectedGenre) !== -1;
                var matchesRating = !selectedRating || card.dataset.rating === selectedRating;
                var matchesStatus = !selectedStatus || card.dataset.status === selectedStatus;
                var isVisible = matchesGenre && matchesRating && matchesStatus;

                card.classList.toggle("hidden", !isVisible);
                if (isVisible) {
                    visibleCount += 1;
                }
            });

            movieCount.textContent = "Có " + visibleCount + " phim được hiển thị.";
            if (noFilteredMovies) {
                noFilteredMovies.classList.toggle("hidden", visibleCount > 0);
            }
        }

        genreFilter.addEventListener("change", filterMovies);
        ratingFilter.addEventListener("change", filterMovies);
        if (statusFilter) {
            statusFilter.addEventListener("change", filterMovies);
        }
    });
</script>

</body>
</html>
