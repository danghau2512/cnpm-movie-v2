<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="vi"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>CineBook - Quản lý</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/management.css"></head><body>
<jsp:include page="/header.jsp"/><main class="management-page">
<jsp:include page="/WEB-INF/views/admin/nav.jsp"/>
<c:if test="${not empty error}"><p class="notice error" role="alert"><c:out value="${error}"/></p></c:if>
<c:if test="${not empty message}"><p class="notice success" role="status"><c:out value="${message}"/></p></c:if>
<h1>Quản lý phim</h1><section class="panel"><h2>${empty editingMovie ? 'Thêm phim' : 'Sửa phim'}</h2>
<form class="management-form" action="${pageContext.request.contextPath}/admin/movies" method="post">
<input type="hidden" name="csrfToken" value="<c:out value="${sessionScope.csrfToken}"/>"><input type="hidden" name="action" value="save"><input type="hidden" name="id" value="${empty editingMovie ? 0 : editingMovie.id}">
<label>Tên phim<input type="text" name="title" value="<c:out value="${editingMovie.title}"/>" required maxlength="150"></label>
<label>Thời lượng (phút)<input type="number" name="durationMinutes" value="<c:out value="${editingMovie.durationMinutes}"/>" required min="1" max="600"></label>
<label>Giới hạn độ tuổi<input type="text" name="ageRating" value="<c:out value="${editingMovie.ageRating}"/>" required maxlength="20" placeholder="P, T13, T16, T18"></label>
<label>Ngày khởi chiếu<input type="date" name="releaseDate" value="<c:out value="${editingMovie.releaseDate}"/>" ></label>
<label>Poster (URL)<input type="url" name="posterUrl" value="<c:out value="${editingMovie.posterUrl}"/>" maxlength="255"></label>
<label>Trailer (URL)<input type="url" name="trailerUrl" value="<c:out value="${editingMovie.trailerUrl}"/>" maxlength="255"></label>
<label>Trạng thái<select name="status"><option value="NOW_SHOWING" ${editingMovie.status eq 'NOW_SHOWING' ? 'selected' : ''}>Đang chiếu</option><option value="COMING_SOON" ${editingMovie.status eq 'COMING_SOON' ? 'selected' : ''}>Sắp chiếu</option><option value="HIDDEN" ${editingMovie.status eq 'HIDDEN' ? 'selected' : ''}>Ẩn</option></select></label>
<fieldset class="wide"><legend>Thể loại</legend><div class="genre-options"><c:forEach var="g" items="${genres}">
<label><input type="checkbox" name="genreIds" value="${g.id}" ${selectedGenres.contains(g.id) ? 'checked' : ''}><c:out value="${g.name}"/></label>
</c:forEach></div></fieldset>
<label class="wide">Mô tả ngắn<textarea name="shortDescription" maxlength="255"><c:out value="${editingMovie.shortDescription}"/></textarea></label>
<label class="wide">Nội dung<textarea name="description" rows="5" maxlength="16000"><c:out value="${editingMovie.description}"/></textarea></label>
<div class="form-actions wide"><button class="btn btn-primary" type="submit">Lưu phim</button><a class="btn btn-ghost" href="${pageContext.request.contextPath}/admin/movies">Nhập mới</a></div>
</form></section><section class="panel table-wrap"><table><thead><tr><th>Mã</th><th>Tên phim</th><th>Thời lượng</th><th>Trạng thái</th><th>Thao tác</th></tr></thead><tbody>
<c:forEach var="m" items="${movies}"><tr><td>${m.id}</td><td><c:out value="${m.title}"/></td><td>${m.durationMinutes} phút</td>
<td><c:choose><c:when test="${m.status eq 'NOW_SHOWING'}">Đang chiếu</c:when><c:when test="${m.status eq 'COMING_SOON'}">Sắp chiếu</c:when><c:otherwise>Ẩn</c:otherwise></c:choose></td>
<td><div class="row-actions"><a class="btn btn-ghost" href="${pageContext.request.contextPath}/admin/movies?edit=${m.id}">Sửa</a>
<form method="post" action="${pageContext.request.contextPath}/admin/movies" onsubmit="return confirm('Xóa phim này? Chỉ xóa được phim chưa có lịch chiếu.');">
<input type="hidden" name="csrfToken" value="<c:out value="${sessionScope.csrfToken}"/>"><input type="hidden" name="id" value="${m.id}"><button class="btn btn-danger" name="action" value="delete">Xóa</button></form></div></td></tr></c:forEach>
<c:if test="${empty movies}"><tr><td colspan="5">Chưa có phim.</td></tr></c:if></tbody></table></section></main><jsp:include page="/footer.jsp"/></body></html>
