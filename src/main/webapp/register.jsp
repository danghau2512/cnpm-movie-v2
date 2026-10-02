<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>CineBook - Đăng ký</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head><body>
<jsp:include page="/header.jsp"/><main class="auth-page"><section class="auth-card"><p class="eyebrow">UC01 - Đăng ký</p><h1>Tạo tài khoản</h1>
<form action="${pageContext.request.contextPath}/register" method="post"><input type="hidden" name="csrfToken" value="<c:out value="${sessionScope.csrfToken}"/>">
<label>Họ và tên<input type="text" name="fullName" autocomplete="name" required maxlength="100" value="<c:out value="${param.fullName}"/>"></label>
<label>Email<input type="email" name="email" autocomplete="email" required maxlength="100" value="<c:out value="${param.email}"/>"></label>
<label>Số điện thoại<input type="tel" name="phone" autocomplete="tel" required pattern="0[0-9]{9}" maxlength="10" placeholder="0901234567" value="<c:out value="${param.phone}"/>"></label>
<label>Mật khẩu<input type="password" name="password" autocomplete="new-password" required minlength="6" maxlength="128" placeholder="Ít nhất 6 ký tự"></label>
<label>Xác nhận mật khẩu<input type="password" name="confirmPassword" autocomplete="new-password" required minlength="6" maxlength="128"></label>
<p class="form-message" role="alert"><c:out value="${error}"/></p><button class="btn btn-primary btn-full">Đăng ký</button></form>
<p class="auth-link">Đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng nhập tại đây</a></p></section></main><jsp:include page="/footer.jsp"/></body></html>
