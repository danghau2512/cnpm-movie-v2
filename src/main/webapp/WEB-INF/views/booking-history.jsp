<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>CineBook - Quản lý</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/management.css"></head><body>
<jsp:include page="/header.jsp"/><main class="management-page">

<c:if test="${not empty error}"><p class="notice error" role="alert"><c:out value="${error}"/></p></c:if>
<c:if test="${not empty message}"><p class="notice success" role="status"><c:out value="${message}"/></p></c:if>
<h1>Lịch sử đặt vé</h1><p>Các đơn đặt vé của bạn, mới nhất trước.</p><section class="panel table-wrap"><table><thead><tr><th>Mã đơn / ngày đặt</th><th>Phim / lịch chiếu</th><th>Ghế / tổng tiền</th><th>Trạng thái</th><th>Chi tiết</th></tr></thead><tbody>
<c:forEach var="b" items="${bookings}"><tr><td><c:out value="${b.bookingCode}"/><br>${b.createdAt}</td><td><c:out value="${b.movieTitle}"/><br><c:out value="${b.roomName}"/><br>${b.showDate} ${b.showTime}</td>
<td><c:out value="${b.seats}" default="Ghế đã được giải phóng"/><br>${b.quantity} vé<br><c:out value="${b.totalText}"/></td>
<td><c:out value="${b.bookingStatusText}"/><br><c:out value="${b.paymentStatusText}"/></td><td>
<c:choose><c:when test="${b.payable}"><a class="btn btn-primary" href="${pageContext.request.contextPath}/payment?bookingId=${b.bookingId}">Tiếp tục thanh toán</a></c:when>
<c:otherwise><a class="btn btn-ghost" href="${pageContext.request.contextPath}/payment-result?bookingId=${b.bookingId}">Xem chi tiết</a></c:otherwise></c:choose></td></tr></c:forEach>
<c:if test="${empty bookings and empty error}"><tr><td colspan="5">Bạn chưa có đơn đặt vé. <a href="${pageContext.request.contextPath}/showtimes">Chọn lịch chiếu</a></td></tr></c:if></tbody></table></section></main><jsp:include page="/footer.jsp"/></body></html>
