<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>CineBook - Quản lý</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/management.css"></head><body>
<jsp:include page="/header.jsp"/><main class="management-page">
<jsp:include page="/WEB-INF/views/admin/nav.jsp"/>
<c:if test="${not empty error}"><p class="notice error" role="alert"><c:out value="${error}"/></p></c:if>
<c:if test="${not empty message}"><p class="notice success" role="status"><c:out value="${message}"/></p></c:if>
<h1>Quản lý đơn đặt vé</h1><section class="panel"><form class="filter-form" method="get" action="${pageContext.request.contextPath}/admin/bookings">
<label>Tìm mã đơn, email hoặc phim<input name="keyword" maxlength="150" value="<c:out value="${param.keyword}"/>"></label>
<label>Trạng thái<select name="status"><option value="">Tất cả</option><option value="PENDING" ${param.status eq 'PENDING' ? 'selected' : ''}>Chờ thanh toán</option><option value="CONFIRMED" ${param.status eq 'CONFIRMED' ? 'selected' : ''}>Đã xác nhận</option><option value="CANCELLED" ${param.status eq 'CANCELLED' ? 'selected' : ''}>Đã hủy / hết hạn</option></select></label><button class="btn btn-primary">Lọc đơn</button></form></section>
<section class="panel table-wrap"><table><thead><tr><th>Đơn / khách hàng</th><th>Phim / suất chiếu</th><th>Ghế / tổng tiền</th><th>Trạng thái</th><th>Thao tác</th></tr></thead><tbody>
<c:forEach var="b" items="${bookings}"><tr><td><strong><c:out value="${b.bookingCode}"/></strong><br><c:out value="${b.customerName}"/><br><c:out value="${b.customerEmail}"/><br>${b.createdAt}</td>
<td><c:out value="${b.movieTitle}"/><br><c:out value="${b.roomName}"/><br>${b.showDate} ${b.showTime}</td>
<td><c:out value="${b.seats}" default="Ghế đã được giải phóng"/><br>${b.quantity} vé<br><c:out value="${b.totalText}"/></td>
<td><c:out value="${b.bookingStatusText}"/><br><c:out value="${b.paymentStatusText}"/><br>${b.paymentMethod eq 'PAY_AT_COUNTER' ? 'Tại quầy' : (b.paymentMethod eq 'VNPAY' ? 'VNPay' : 'Chưa chọn phương thức')}</td>
<td><c:if test="${b.bookingStatus eq 'PENDING' and b.paymentStatus eq 'UNPAID'}"><div class="row-actions">
<c:if test="${b.payable and b.paymentMethod eq 'PAY_AT_COUNTER'}"><form method="post" action="${pageContext.request.contextPath}/admin/bookings" onsubmit="return confirm('Đã nhận đủ tiền tại quầy cho đơn này?');">
<input type="hidden" name="csrfToken" value="<c:out value="${sessionScope.csrfToken}"/>"><input type="hidden" name="id" value="${b.bookingId}"><button class="btn btn-primary" name="action" value="confirm">Xác nhận thu tiền</button></form></c:if>
<form method="post" action="${pageContext.request.contextPath}/admin/bookings" onsubmit="return confirm('Hủy đơn và giải phóng ghế?');">
<input type="hidden" name="csrfToken" value="<c:out value="${sessionScope.csrfToken}"/>"><input type="hidden" name="id" value="${b.bookingId}"><button class="btn btn-danger" name="action" value="cancel">Hủy đơn</button></form></div></c:if></td></tr></c:forEach>
<c:if test="${empty bookings}"><tr><td colspan="5">Không có đơn phù hợp.</td></tr></c:if></tbody></table></section></main><jsp:include page="/footer.jsp"/></body></html>
