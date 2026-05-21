<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="site-header">
    <a class="logo" href="${pageContext.request.contextPath}/home">
        Cine<span>Book</span>
    </a>

    <%-- UC03 - 3.1.0 + 3.1.1: Khách hàng nhập keyword và gửi yêu cầu tìm kiếm phim --%>
    <form class="header-search" action="${pageContext.request.contextPath}/search" method="get">
        <input
                type="search"
                name="keyword"
                placeholder="Nhập tên phim hoặc thể loại..."
                value="${param.keyword}">
        <%-- UC03 - 3.1.2: Form gửi keyword đến SearchController qua route /search để xử lý tìm kiếm --%>
        <button type="submit" class="search-btn">Tìm</button>
    </form>

    <button class="menu-toggle" aria-label="Mở trình đơn">Trình đơn</button>

    <nav class="main-nav">
        <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/movies">Phim</a>
        <a href="${pageContext.request.contextPath}/showtimes">Lịch chiếu</a>
    </nav>


    <div class="header-actions">
        <c:choose>
            <c:when test="${not empty sessionScope.currentUser}">
                <span class="user-name">
                    Xin chào, ${sessionScope.currentUser.fullName}
                </span>

                <a class="btn btn-ghost" href="${pageContext.request.contextPath}/logout">
                    Đăng xuất
                </a>
            </c:when>

            <c:otherwise>
                <a class="btn btn-ghost" href="${pageContext.request.contextPath}/login">
                    Đăng nhập
                </a>

                <a class="btn btn-primary" href="${pageContext.request.contextPath}/register">
                    Đăng ký
                </a>
            </c:otherwise>
        </c:choose>
    </div>
</header>
