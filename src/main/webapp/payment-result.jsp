<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>CineBook - Kết quả thanh toán</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        .result-page {
            padding: 3rem 6%;
            display: grid;
            place-items: center;
        }

        .result-card {
            max-width: 620px;
            width: 100%;
            padding: 2rem;
            border: 1px solid var(--line);
            border-radius: 0.9rem;
            background: rgba(21, 21, 29, 0.92);
            text-align: center;
        }

        .result-icon {
            width: 72px;
            height: 72px;
            border-radius: 50%;
            display: grid;
            place-items: center;
            margin: 0 auto 1rem;
            background: var(--gold);
            color: #211307;
            font-size: 2rem;
            font-weight: 900;
        }

        .ticket-info {
            margin: 1.5rem 0;
            padding: 1rem;
            border: 1px dashed rgba(240, 184, 74, 0.55);
            border-radius: 0.7rem;
            text-align: left;
        }

        .ticket-info p {
            display: flex;
            justify-content: space-between;
            gap: 1rem;
            color: var(--muted);
        }

        .ticket-info strong {
            color: var(--text);
            text-align: right;
        }

        .result-actions {
            display: flex;
            justify-content: center;
            gap: 1rem;
            flex-wrap: wrap;
        }
        .status-badge {
            display: inline-block;
            padding: 6px 12px;
            border-radius: 999px;
            font-size: 14px;
            font-weight: 700;
            letter-spacing: 0.2px;
        }

        .status-pending {
            color: #f5c542;
            background: rgba(245, 197, 66, 0.12);
            border: 1px solid rgba(245, 197, 66, 0.45);
        }

        .status-success {
            color: #22c55e;
            background: rgba(34, 197, 94, 0.12);
            border: 1px solid rgba(34, 197, 94, 0.45);
        }

        .status-cancelled {
            color: #ef4444;
            background: rgba(239, 68, 68, 0.12);
            border: 1px solid rgba(239, 68, 68, 0.45);
        }

        .status-default {
            color: #d1d5db;
            background: rgba(209, 213, 219, 0.12);
            border: 1px solid rgba(209, 213, 219, 0.35);
        }
    </style>
</head>

<body data-page="payment-result">

<jsp:include page="/header.jsp" />

<main class="result-page">
    <section class="result-card">
        <c:choose>
            <%-- UC07 - 7.1.12: Hiển thị kết quả thanh toán thành công khi booking.payment_status = PAID --%>

            <%-- UC07 - 7.2.10: Hiển thị kết quả giữ vé và thanh toán tại quầy khi booking.payment_status = UNPAID --%>
            <c:when test="${paymentInfo.paymentStatus == 'PAID'}">
                <div class="result-icon">✓</div>
                <h1>Thanh toán thành công</h1>
                <p class="muted">
                    Vé của bạn đã được xác nhận. Vui lòng kiểm tra thông tin bên dưới.
                </p>
            </c:when>

            <c:otherwise>
                <div class="result-icon">!</div>
                <h1>Đặt vé thành công</h1>
                <p class="muted">
                    Vé đang được giữ tạm thời. Vui lòng thanh toán tại quầy trước khi xem phim.
                </p>
            </c:otherwise>
        </c:choose>

        <div class="ticket-info">
            <p>
                <span>Mã đặt vé</span>
                <strong>${paymentInfo.bookingCode}</strong>
            </p>

            <p>
                <span>Phim</span>
                <strong>${paymentInfo.movieTitle}</strong>
            </p>

            <p>
                <span>Suất chiếu</span>
                <strong>${paymentInfo.showDate} - ${paymentInfo.showTime}</strong>
            </p>

            <p>
                <span>Phòng</span>
                <strong>${paymentInfo.roomName}</strong>
            </p>

            <p>
                <span>Ghế</span>
                <strong>${paymentInfo.seats}</strong>
            </p>

            <p>
                <span>Tổng tiền</span>
                <strong>${paymentInfo.totalText} VNĐ</strong>
            </p>

            <p>
                <span>Trạng thái đặt vé</span>
                <strong class="status-badge ${paymentInfo.bookingStatusClass}">
                    ${paymentInfo.bookingStatusText}
                </strong>
            </p>

            <p>
                <span>Trạng thái thanh toán</span>
                <strong class="status-badge ${paymentInfo.paymentStatusClass}">
                    ${paymentInfo.paymentStatusText}
                </strong>
            </p>
        </div>

        <div class="result-actions">
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/home">
                Về trang chủ
            </a>

            <a class="btn btn-ghost" href="${pageContext.request.contextPath}/movies">
                Xem phim khác
            </a>
        </div>
    </section>
</main>

<jsp:include page="/footer.jsp" />

</body>
</html>