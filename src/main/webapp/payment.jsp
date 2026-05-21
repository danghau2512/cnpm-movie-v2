<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>CineBook - Thanh toán</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        .payment-page {
            padding: 3rem 6%;
        }

        .payment-layout {
            display: grid;
            grid-template-columns: minmax(0, 1fr) 420px;
            gap: 1.5rem;
        }

        .payment-box,
        .payment-summary {
            border: 1px solid var(--line);
            border-radius: 0.75rem;
            background: rgba(21, 21, 29, 0.9);
            padding: 1.5rem;
        }

        .summary-row {
            display: flex;
            justify-content: space-between;
            gap: 1rem;
            margin-bottom: 1rem;
            color: var(--muted);
        }

        .summary-row strong {
            color: var(--text);
            text-align: right;
        }

        .payment-method {
            display: block;
            padding: 1rem;
            margin-bottom: 1rem;
            border: 1px solid var(--line);
            border-radius: 0.65rem;
            background: rgba(255,255,255,0.04);
            cursor: pointer;
        }

        .payment-method input {
            width: auto;
            min-height: auto;
            margin-right: 0.6rem;
        }

        .payment-method span {
            font-weight: 800;
            color: var(--gold-soft);
        }

        .payment-method p {
            margin: 0.5rem 0 0 1.7rem;
            color: var(--muted);
        }

        .total-money {
            color: var(--gold);
            font-size: 1.5rem;
            font-weight: 900;
        }

        @media (max-width: 900px) {
            .payment-layout {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>

<body data-page="payment">

<jsp:include page="/header.jsp" />

<main class="payment-page">
    <section class="page-title">
        <p class="eyebrow">UC07 - Thanh toán</p>
        <h1>Thanh toán vé</h1>
        <p class="muted">
            Kiểm tra thông tin đặt vé và chọn phương thức thanh toán.
        </p>
    </section>

    <form action="${pageContext.request.contextPath}/payment" method="post">
        <input type="hidden" name="bookingId" value="${paymentInfo.bookingId}">

        <div class="payment-layout">
            <section class="payment-box">
                <h2>Chọn phương thức thanh toán</h2>

                <label class="payment-method">
                    <input type="radio" name="paymentMethod" value="VNPAY" checked>
                    <span>Thanh toán VNPay</span>
                    <p>Mô phỏng thanh toán online. Sau khi xác nhận, đơn sẽ được thanh toán thành công.</p>
                </label>

                <label class="payment-method">
                    <input type="radio" name="paymentMethod" value="PAY_AT_COUNTER">
                    <span>Thanh toán tại quầy</span>
                    <p>Giữ ghế tạm thời. Khách hàng thanh toán khi đến rạp.</p>
                </label>

                <button type="submit" class="btn btn-primary btn-full">
                    Xác nhận thanh toán
                </button>
            </section>

            <aside class="payment-summary">
                <h2>Thông tin vé</h2>

                <div class="summary-row">
                    <span>Mã đặt vé</span>
                    <strong>${paymentInfo.bookingCode}</strong>
                </div>

                <div class="summary-row">
                    <span>Phim</span>
                    <strong>${paymentInfo.movieTitle}</strong>
                </div>

                <div class="summary-row">
                    <span>Phòng</span>
                    <strong>${paymentInfo.roomName}</strong>
                </div>

                <div class="summary-row">
                    <span>Ngày chiếu</span>
                    <strong>${paymentInfo.showDate}</strong>
                </div>

                <div class="summary-row">
                    <span>Giờ chiếu</span>
                    <strong>${paymentInfo.showTime}</strong>
                </div>

                <div class="summary-row">
                    <span>Ghế</span>
                    <strong>${paymentInfo.seats}</strong>
                </div>

                <div class="summary-row">
                    <span>Số lượng</span>
                    <strong>${paymentInfo.quantity}</strong>
                </div>

                <hr>

                <div class="summary-row">
                    <span>Tổng tiền</span>
                    <strong class="total-money">${paymentInfo.totalText} VNĐ</strong>
                </div>
            </aside>
        </div>
    </form>
</main>

<jsp:include page="/footer.jsp" />

</body>
</html>