<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>CineBook - Quản lý</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/management.css"></head><body>
<jsp:include page="/header.jsp"/><main class="management-page">
<jsp:include page="/WEB-INF/views/admin/nav.jsp"/>
<c:if test="${not empty error}"><p class="notice error" role="alert"><c:out value="${error}"/></p></c:if>
<c:if test="${not empty message}"><p class="notice success" role="status"><c:out value="${message}"/></p></c:if>
<h1>Quản lý lịch chiếu</h1><section class="panel"><h2>${empty editingShowtime ? 'Thêm lịch chiếu' : 'Sửa lịch chiếu'}</h2>
<p>Lịch chiếu đã có đơn đặt vé chỉ được đổi trạng thái. Đóng lịch để ngừng nhận đặt vé mới.</p>
<form class="management-form" method="post" action="${pageContext.request.contextPath}/admin/showtimes">
<input type="hidden" name="csrfToken" value="<c:out value="${sessionScope.csrfToken}"/>"><input type="hidden" name="action" value="save"><input type="hidden" name="id" value="${empty editingShowtime ? 0 : editingShowtime.id}">
<label>Phim<select name="movieId" required><option value="">Chọn phim</option><c:forEach var="m" items="${movies}">
<option value="${m.id}" ${editingShowtime.movieId eq m.id ? 'selected' : ''}><c:out value="${m.title}"/></option></c:forEach></select></label>
<label>Phòng chiếu<select name="roomId" required><option value="">Chọn phòng</option><c:forEach var="r" items="${rooms}">
<option value="${r.id}" ${editingShowtime.roomId eq r.id ? 'selected' : ''}><c:out value="${r.name}"/> (${r.status eq 'ACTIVE' ? 'Hoạt động' : 'Tạm ngừng'})</option></c:forEach></select></label>
<label>Ngày chiếu<input type="date" name="showDate" value="<c:out value="${editingShowtime.showDate}"/>" required></label>
<label>Giờ chiếu<input type="time" name="showTime" value="<c:out value="${editingShowtime.showTime}"/>" required></label>
<label>Giá vé (VNĐ)<input type="number" name="price" value="<c:out value="${editingShowtime.price}"/>" required min="1" max="9999999999" step="1"></label>
<label>Trạng thái<select name="status"><option value="OPEN" ${editingShowtime.status eq 'OPEN' ? 'selected' : ''}>Mở đặt vé</option><option value="CLOSED" ${editingShowtime.status eq 'CLOSED' ? 'selected' : ''}>Đóng đặt vé</option><option value="CANCELLED" ${editingShowtime.status eq 'CANCELLED' ? 'selected' : ''}>Đã hủy</option></select></label>
<div class="form-actions wide"><button class="btn btn-primary">Lưu lịch chiếu</button><a class="btn btn-ghost" href="${pageContext.request.contextPath}/admin/showtimes">Nhập mới</a></div></form></section>
<section class="panel table-wrap"><table><thead><tr><th>Phim</th><th>Phòng</th><th>Ngày giờ</th><th>Giá vé</th><th>Trạng thái</th><th>Thao tác</th></tr></thead><tbody>
<c:forEach var="s" items="${showtimes}"><tr><td><c:out value="${s.movieTitle}"/></td><td><c:out value="${s.roomName}"/></td><td>${s.showDate} ${s.showTime}</td><td>${s.price} VNĐ</td>
<td><c:choose><c:when test="${s.status eq 'OPEN'}">Mở đặt vé</c:when><c:when test="${s.status eq 'CLOSED'}">Đóng đặt vé</c:when><c:otherwise>Đã hủy</c:otherwise></c:choose></td>
<td><div class="row-actions"><a class="btn btn-ghost" href="${pageContext.request.contextPath}/admin/showtimes?edit=${s.id}">Sửa</a>
<form method="post" action="${pageContext.request.contextPath}/admin/showtimes" onsubmit="return confirm('Xóa lịch chiếu này?');">
<input type="hidden" name="csrfToken" value="<c:out value="${sessionScope.csrfToken}"/>"><input type="hidden" name="id" value="${s.id}"><button class="btn btn-danger" name="action" value="delete">Xóa</button></form></div></td></tr></c:forEach>
<c:if test="${empty showtimes}"><tr><td colspan="6">Chưa có lịch chiếu.</td></tr></c:if></tbody></table></section></main><jsp:include page="/footer.jsp"/></body></html>
