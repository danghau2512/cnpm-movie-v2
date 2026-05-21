<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>CineBook - Đặt vé</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <style>
        .booking-page {
            padding: 3rem 6%;
        }

        .booking-title {
            margin-bottom: 2rem;
        }

        .booking-title h1 {
            font-size: clamp(2rem, 4vw, 4rem);
            margin-bottom: 0.8rem;
        }

        .booking-box {
            display: grid;
            grid-template-columns: minmax(0, 1fr) 340px;
            gap: 1.5rem;
        }

        .seat-panel,
        .booking-summary {
            border: 1px solid var(--line);
            border-radius: 0.75rem;
            background: rgba(21, 21, 29, 0.9);
            padding: 1.5rem;
        }

        .screen {
            margin: 1rem auto 2rem;
            max-width: 500px;
            padding: 0.7rem;
            border-radius: 0.4rem;
            background: linear-gradient(90deg, transparent, rgba(240, 184, 74, 0.65), transparent);
            text-align: center;
            color: var(--gold-soft);
            font-weight: 700;
        }

        .seat-grid {
            display: grid;
            grid-template-columns: repeat(8, 1fr);
            gap: 0.65rem;
            max-width: 620px;
            margin: 0 auto;
        }

        .seat-item {
            position: relative;
            display: block;
            cursor: pointer;
        }

        .seat-item input {
            display: none;
        }

        .seat-item span {
            display: grid;
            place-items: center;
            min-height: 42px;
            border-radius: 0.4rem 0.4rem 0.8rem 0.8rem;
            border: 1px solid var(--line);
            background: #2b2b35;
            color: var(--text);
            font-weight: 700;
            transition: 0.2s;
        }

        .seat-item.available span:hover {
            border-color: var(--gold);
        }

        .seat-item input:checked + span {
            background: var(--gold);
            color: #211307;
            border-color: var(--gold);
        }

        .seat-item.booked {
            cursor: not-allowed;
        }

        .seat-item.booked span {
            background: #641717;
            color: #ffb5b5;
        }

        .seat-legend {
            display: flex;
            flex-wrap: wrap;
            gap: 1rem;
            margin-bottom: 1.2rem;
            color: var(--muted);
        }

        .legend-box {
            display: inline-block;
            width: 18px;
            height: 18px;
            border-radius: 0.3rem;
            margin-right: 0.4rem;
            vertical-align: middle;
        }

        .legend-available {
            background: #2b2b35;
            border: 1px solid var(--line);
        }

        .legend-selected {
            background: var(--gold);
        }

        .legend-booked {
            background: #641717;
        }

        .booking-summary {
            position: sticky;
            top: 100px;
            height: fit-content;
        }

        .summary-line {
            display: flex;
            justify-content: space-between;
            gap: 1rem;
            margin-bottom: 1rem;
            color: var(--muted);
        }

        .summary-line strong {
            color: var(--text);
            text-align: right;
        }

        .selected-seats {
            color: var(--gold-soft);
            font-weight: 700;
        }

        .total-price {
            color: var(--gold);
            font-size: 1.4rem;
            font-weight: 900;
        }

        .error-box {
            margin-bottom: 1rem;
            padding: 1rem;
            border: 1px solid rgba(255, 143, 143, 0.5);
            border-radius: 0.5rem;
            color: #ff8f8f;
            background: rgba(100, 23, 23, 0.25);
        }

        @media (max-width: 1000px) {
            .booking-box {
                grid-template-columns: 1fr;
            }

            .booking-summary {
                position: static;
            }
        }

        @media (max-width: 600px) {
            .seat-grid {
                grid-template-columns: repeat(4, 1fr);
            }
        }
    </style>
</head>

<body data-page="booking">

<jsp:include page="/header.jsp" />

<main class="booking-page">
    <section class="booking-title">
        <p class="eyebrow">UC06 - Đặt vé</p>
        <h1>Chọn ghế và đặt vé</h1>
        <p class="muted">
            Chọn ghế còn trống cho suất chiếu đã chọn và xác nhận đặt vé.
        </p>
    </section>

    <c:if test="${not empty error}">
        <div class="error-box">
                ${error}
        </div>
    </c:if>

    <%--
    UC06 - Đặt vé
    UC06 - 6.1.6: Hiển thị giao diện chọn ghế cho khách hàng.
    UC06 - 6.1.8: Khi khách hàng xác nhận, form gửi showtimeId và seatIds về BookingController.
--%>
    <form action="${pageContext.request.contextPath}/booking" method="post" id="bookingForm">
        <input type="hidden" name="showtimeId" value="${showtime.id}">

        <div class="booking-box">
            <section class="seat-panel">
                <h2>Sơ đồ ghế</h2>

                <div class="seat-legend">
                    <span><i class="legend-box legend-available"></i>Ghế trống</span>
                    <span><i class="legend-box legend-selected"></i>Đang chọn</span>
                    <span><i class="legend-box legend-booked"></i>Đã đặt</span>
                </div>

                <div class="screen">Màn hình</div>

                <div class="seat-grid">
                    <%--
                        UC06 - 6.1.7: Khách hàng chọn một hoặc nhiều ghế còn trống.
                        Ghế đã được đặt sẽ bị disabled và không thể chọn.
                    --%>
                    <c:forEach var="seat" items="${seats}">
                        <label class="seat-item ${seat.booked ? 'booked' : 'available'}">
                            <input type="checkbox"
                                   name="seatIds"
                                   value="${seat.id}"
                                   data-code="${seat.seatCode}"
                                   data-price="${showtime.price}"
                                ${seat.booked ? 'disabled' : ''}>
                            <span>${seat.seatCode}</span>
                        </label>
                    </c:forEach>
                </div>
            </section>

            <aside class="booking-summary">
                <h2>Thông tin đặt vé</h2>

                <div class="summary-line">
                    <span>Phim</span>
                    <strong>${showtime.movieTitle}</strong>
                </div>

                <div class="summary-line">
                    <span>Phòng</span>
                    <strong>${showtime.roomName}</strong>
                </div>

                <div class="summary-line">
                    <span>Ngày chiếu</span>
                    <strong>${showtime.showDate}</strong>
                </div>

                <div class="summary-line">
                    <span>Giờ chiếu</span>
                    <strong>${showtime.showTime}</strong>
                </div>

                <div class="summary-line">
                    <span>Giá vé</span>
                    <strong>${showtime.priceText} VNĐ</strong>
                </div>

                <hr>

                <div class="summary-line">
                    <span>Ghế đã chọn</span>
                    <strong class="selected-seats" id="selectedSeats">Chưa chọn</strong>
                </div>

                <div class="summary-line">
                    <span>Số lượng</span>
                    <strong id="seatCount">0</strong>
                </div>

                <div class="summary-line">
                    <span>Tổng tiền</span>
                    <strong class="total-price" id="totalPrice">0 VNĐ</strong>
                </div>

                <button type="submit" class="btn btn-primary btn-full">
                    Xác nhận đặt vé
                </button>
            </aside>
        </div>
    </form>
</main>

<jsp:include page="/footer.jsp" />

<script>
    const form = document.getElementById("bookingForm");
    const seatInputs = document.querySelectorAll('input[name="seatIds"]');
    const selectedSeatsEl = document.getElementById("selectedSeats");
    const seatCountEl = document.getElementById("seatCount");
    const totalPriceEl = document.getElementById("totalPrice");

    function updateSummary() {
        const selected = Array.from(seatInputs).filter(item => item.checked);

        const seatCodes = selected.map(item => item.dataset.code);
        const count = selected.length;

        let total = 0;

        selected.forEach(item => {
            total += Number(item.dataset.price || 0);
        });

        selectedSeatsEl.textContent = count > 0 ? seatCodes.join(", ") : "Chưa chọn";
        seatCountEl.textContent = count;
        totalPriceEl.textContent = total.toLocaleString("vi-VN") + " VNĐ";
    }

    seatInputs.forEach(input => {
        input.addEventListener("change", updateSummary);
    });

    form.addEventListener("submit", function (event) {
        const selected = Array.from(seatInputs).filter(item => item.checked);

        if (selected.length === 0) {
            event.preventDefault();
            alert("Vui lòng chọn ít nhất một ghế.");
        }
    });
</script>

</body>
</html>