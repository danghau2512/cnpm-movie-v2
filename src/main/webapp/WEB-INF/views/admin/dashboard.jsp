<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>CineBook - Quản lý</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/management.css"></head><body>
<jsp:include page="/header.jsp"/><main class="management-page">
<jsp:include page="/WEB-INF/views/admin/nav.jsp"/>
<c:if test="${not empty error}"><p class="notice error" role="alert"><c:out value="${error}"/></p></c:if>
<c:if test="${not empty message}"><p class="notice success" role="status"><c:out value="${message}"/></p></c:if>
<h1>Quản trị CineBook</h1><div class="summary-grid">
<a class="panel" href="${pageContext.request.contextPath}/admin/movies"><strong>${movieCount}</strong> phim</a>
<a class="panel" href="${pageContext.request.contextPath}/admin/showtimes"><strong>${showtimeCount}</strong> suất chiếu</a>
<a class="panel" href="${pageContext.request.contextPath}/admin/bookings"><strong>${bookingCount}</strong> đơn đặt vé</a></div>
<p>Chọn chức năng để thêm, sửa phim và lịch chiếu hoặc xử lý đơn thanh toán tại quầy.</p></main><jsp:include page="/footer.jsp"/></body></html>
