const movies = [
    {
        id: "m1",
        title: "Thiên Hà Nửa Đêm",
        genre: "Khoa học viễn tưởng",
        duration: "126 phút",
        rating: "13+",
        shortDescription: "Một phi công trẻ phát hiện tín hiệu có thể cứu Trái Đất.",
        description: "Một phi công trẻ tham gia nhiệm vụ giải cứu ngoài không gian sau khi nhận được tín hiệu bí ẩn từ một thuộc địa đã mất liên lạc. Bộ phim kết hợp phiêu lưu, tình bạn và cuộc chạy đua với thời gian."
    },
    {
        id: "m2",
        title: "Tình Yêu Sài Gòn",
        genre: "Tình cảm",
        duration: "105 phút",
        rating: "Mọi lứa tuổi",
        shortDescription: "Hai sinh viên gặp lại nhau trong một mùa mưa ở thành phố.",
        description: "Một câu chuyện tình cảm nhẹ nhàng về hai người bạn cũ gặp lại khi cùng chuẩn bị cho một sự kiện âm nhạc tại Sài Gòn. Nội dung đơn giản, cảm xúc và phù hợp cho một buổi xem phim thư giãn."
    },
    {
        id: "m3",
        title: "Thám Tử Cuối Cùng",
        genre: "Hành động",
        duration: "118 phút",
        rating: "16+",
        shortDescription: "Một thám tử đã nghỉ hưu trở lại cho vụ án cuối cùng.",
        description: "Sau nhiều năm rời xa công việc điều tra, một thám tử bị cuốn vào một vụ án nguy hiểm. Bộ phim có các cảnh hành động, manh mối bí ẩn và phần kết bất ngờ."
    },
    {
        id: "m4",
        title: "Căn Bếp Gia Đình",
        genre: "Hài",
        duration: "98 phút",
        rating: "Mọi lứa tuổi",
        shortDescription: "Một cuộc thi nấu ăn gia đình biến thành cuối tuần đầy tiếng cười.",
        description: "Ba thế hệ cùng tham gia cuộc thi nấu ăn ở khu phố. Những sai sót, câu chuyện hài hước và ký ức gia đình tạo nên một bộ phim nhẹ nhàng cho mọi lứa tuổi."
    },
    {
        id: "m5",
        title: "Bóng Tối Sân Trường",
        genre: "Kinh dị",
        duration: "110 phút",
        rating: "18+",
        shortDescription: "Một nhóm bạn điều tra lớp học bỏ hoang trong khuôn viên trường.",
        description: "Tin đồn trong trường trở thành sự thật khi nhóm sinh viên bước vào một tòa nhà cũ vào ban đêm. Bộ phim có không khí u ám và phù hợp với khán giả trưởng thành."
    },
    {
        id: "m6",
        title: "Bàn Thắng Vàng",
        genre: "Tâm lý",
        duration: "112 phút",
        rating: "13+",
        shortDescription: "Đội bóng học đường nỗ lực cho giải đấu cuối cùng.",
        description: "Một đội bóng học sinh với điều kiện hạn chế cố gắng vào trận chung kết thành phố. Câu chuyện tập trung vào tinh thần đồng đội, áp lực và quá trình trưởng thành."
    }
];

const showtimes = [
    { id: "s1", movieId: "m1", room: "Phòng 1", date: "2026-05-01", time: "18:30", price: 90000 },
    { id: "s2", movieId: "m1", room: "Phòng 2", date: "2026-05-01", time: "21:00", price: 95000 },
    { id: "s3", movieId: "m2", room: "Phòng 3", date: "2026-05-01", time: "19:00", price: 80000 },
    { id: "s4", movieId: "m3", room: "Phòng 1", date: "2026-05-02", time: "20:15", price: 100000 },
    { id: "s5", movieId: "m4", room: "Phòng 4", date: "2026-05-02", time: "17:45", price: 75000 },
    { id: "s6", movieId: "m5", room: "Phòng 2", date: "2026-05-03", time: "22:00", price: 100000 },
    { id: "s7", movieId: "m6", room: "Phòng 3", date: "2026-05-03", time: "18:00", price: 85000 },
    { id: "s8", movieId: "m2", room: "Phòng 1", date: "2026-05-04", time: "20:00", price: 80000 }
];

const bookedSeatsByShowtime = {
    s1: ["A3", "B5", "C2"],
    s2: ["A1", "A2", "D6"],
    s3: ["B1", "B2"],
    s4: ["C4", "C5", "D1"],
    s5: ["A8", "B8"],
    s6: ["A4", "B4", "C4"],
    s7: ["D2", "D3"],
    s8: ["A6", "B6"]
};

const storageKeys = {
    selectedMovie: "cinebookSelectedMovie",
    selectedShowtime: "cinebookSelectedShowtime",
    booking: "cinebookBooking",
    payment: "cinebookPayment"
};

document.addEventListener("DOMContentLoaded", () => {
    setupMobileMenu();

    const page = document.body.dataset.page;
    if (page === "home") initHome();
    if (page === "register") initRegister();
    if (page === "login") initLogin();
    if (page === "movies") initMovies();
    if (page === "movie-detail") initMovieDetail();
    if (page === "showtimes") initShowtimes();
    if (page === "booking") initBooking();
    if (page === "payment") initPayment();
    if (page === "payment-result") initPaymentResult();
});

function setupMobileMenu() {
    const header = document.querySelector(".site-header");
    const toggle = document.querySelector(".menu-toggle");
    if (!header || !toggle) return;

    toggle.addEventListener("click", () => {
        header.classList.toggle("open");
    });
}

function formatPrice(value) {
    return `${value.toLocaleString("vi-VN")} VND`;
}

function getMovie(movieId) {
    return movies.find(movie => movie.id === movieId);
}

function getShowtime(showtimeId) {
    return showtimes.find(showtime => showtime.id === showtimeId);
}

function getQueryValue(name) {
    return new URLSearchParams(window.location.search).get(name);
}

function saveSelection(movieId, showtimeId = "") {
    localStorage.setItem(storageKeys.selectedMovie, movieId);
    if (showtimeId) {
        localStorage.setItem(storageKeys.selectedShowtime, showtimeId);
    }
}

function movieCard(movie) {
    return `
        <article class="movie-card">
            <div class="poster">${movie.title}</div>
            <h3>${movie.title}</h3>
            <div class="meta">
                <span>${movie.genre}</span>
                <span>${movie.duration}</span>
                <span>${movie.rating}</span>
            </div>
            <p>${movie.shortDescription}</p>
            <div class="card-actions">
                <a class="btn btn-ghost" href="movie-detail.html?id=${movie.id}" data-select-movie="${movie.id}">Xem chi tiết</a>
                <a class="btn btn-primary" href="booking.html?movie=${movie.id}" data-select-movie="${movie.id}">Đặt vé</a>
            </div>
        </article>
    `;
}

function initHome() {
    const featured = document.querySelector("#featuredMovies");
    featured.innerHTML = movies.slice(0, 3).map(movieCard).join("");
    bindMovieSelectionLinks();
}

function initRegister() {
    const form = document.querySelector("#registerForm");
    const message = document.querySelector("#registerMessage");

    form.addEventListener("submit", event => {
        event.preventDefault();

        const fullName = document.querySelector("#fullName").value.trim();
        const email = document.querySelector("#registerEmail").value.trim();
        const phone = document.querySelector("#phone").value.trim();
        const password = document.querySelector("#registerPassword").value;
        const confirmPassword = document.querySelector("#confirmPassword").value;

        if (!fullName || !email || !phone || !password || !confirmPassword) {
            setMessage(message, "Vui lòng nhập đầy đủ thông tin.", "error");
            return;
        }

        if (!email.includes("@")) {
            setMessage(message, "Vui lòng nhập email hợp lệ.", "error");
            return;
        }

        if (phone.length < 9) {
            setMessage(message, "Số điện thoại phải có ít nhất 9 chữ số.", "error");
            return;
        }

        if (password.length < 6) {
            setMessage(message, "Mật khẩu phải có ít nhất 6 ký tự.", "error");
            return;
        }

        if (password !== confirmPassword) {
            setMessage(message, "Mật khẩu xác nhận không khớp.", "error");
            return;
        }

        setMessage(message, "Đăng ký thành công. Bạn có thể đăng nhập ngay.", "success");
        form.reset();
    });
}

function initLogin() {
    const form = document.querySelector("#loginForm");
    const message = document.querySelector("#loginMessage");

    form.addEventListener("submit", event => {
        event.preventDefault();

        const email = document.querySelector("#loginEmail").value.trim();
        const password = document.querySelector("#loginPassword").value;

        if (!email || !password) {
            setMessage(message, "Vui lòng nhập email và mật khẩu.", "error");
            return;
        }

        if (!email.includes("@")) {
            setMessage(message, "Vui lòng nhập email hợp lệ.", "error");
            return;
        }

        if (password.length < 6) {
            setMessage(message, "Mật khẩu phải có ít nhất 6 ký tự.", "error");
            return;
        }

        setMessage(message, "Đăng nhập thành công với tài khoản demo.", "success");
    });
}

function setMessage(element, text, type) {
    element.textContent = text;
    element.className = `form-message ${type}`;
}

function initMovies() {
    const genreFilter = document.querySelector("#genreFilter");
    const ratingFilter = document.querySelector("#ratingFilter");
    const searchInput = document.querySelector("#movieSearch");

    [...new Set(movies.map(movie => movie.genre))].forEach(genre => {
        genreFilter.insertAdjacentHTML("beforeend", `<option value="${genre}">${genre}</option>`);
    });

    [...new Set(movies.map(movie => movie.rating))].forEach(rating => {
        ratingFilter.insertAdjacentHTML("beforeend", `<option value="${rating}">${rating}</option>`);
    });

    [genreFilter, ratingFilter, searchInput].forEach(input => {
        input.addEventListener("input", renderMovies);
        input.addEventListener("change", renderMovies);
    });

    renderMovies();
}

function renderMovies() {
    const list = document.querySelector("#movieList");
    const noMovies = document.querySelector("#noMovies");
    const count = document.querySelector("#movieCount");
    const searchText = document.querySelector("#movieSearch").value.trim().toLowerCase();
    const genre = document.querySelector("#genreFilter").value;
    const rating = document.querySelector("#ratingFilter").value;

    const filtered = movies.filter(movie => {
        const matchesSearch = movie.title.toLowerCase().includes(searchText);
        const matchesGenre = !genre || movie.genre === genre;
        const matchesRating = !rating || movie.rating === rating;
        return matchesSearch && matchesGenre && matchesRating;
    });

    list.innerHTML = filtered.map(movieCard).join("");
    count.textContent = `Tìm thấy ${filtered.length} phim`;
    noMovies.classList.toggle("hidden", filtered.length > 0);
    bindMovieSelectionLinks();
}

function bindMovieSelectionLinks() {
    document.querySelectorAll("[data-select-movie]").forEach(link => {
        link.addEventListener("click", () => {
            saveSelection(link.dataset.selectMovie);
        });
    });
}

function initMovieDetail() {
    const detail = document.querySelector("#movieDetail");
    const movieId = getQueryValue("id") || localStorage.getItem(storageKeys.selectedMovie) || movies[0].id;
    const movie = getMovie(movieId);

    if (!movie) {
        detail.innerHTML = `<p class="empty-message">Không tìm thấy phim. <a href="movies.html">Quay lại danh sách phim</a></p>`;
        return;
    }

    saveSelection(movie.id);
    const movieShowtimes = showtimes.filter(showtime => showtime.movieId === movie.id);

    detail.innerHTML = `
        <div class="poster detail-poster">${movie.title}</div>
        <div class="detail-content">
            <p class="eyebrow">UC04 - Xem chi tiết phim</p>
            <h1>${movie.title}</h1>
            <div class="meta">
                <span>${movie.genre}</span>
                <span>${movie.duration}</span>
                <span>${movie.rating}</span>
            </div>
            <p class="muted">${movie.description}</p>
            <h2>Suất chiếu hiện có</h2>
            <div class="showtime-chips">
                ${movieShowtimes.map(showtime => `
                    <button class="showtime-chip" data-showtime="${showtime.id}">
                        ${showtime.date} - ${showtime.time}
                    </button>
                `).join("")}
            </div>
            <div class="hero-actions">
                <a class="btn btn-secondary" href="showtimes.html?movie=${movie.id}">Xem lịch chiếu</a>
                <a class="btn btn-primary" href="booking.html?movie=${movie.id}">Đặt vé</a>
            </div>
        </div>
    `;

    document.querySelectorAll("[data-showtime]").forEach(button => {
        button.addEventListener("click", () => {
            saveSelection(movie.id, button.dataset.showtime);
            window.location.href = `booking.html?movie=${movie.id}&showtime=${button.dataset.showtime}`;
        });
    });
}

function initShowtimes() {
    const selectedMovieId = getQueryValue("movie");
    const dates = [...new Set(showtimes.map(showtime => showtime.date))];
    const dateTabs = document.querySelector("#dateTabs");
    let activeDate = dates[0];

    dateTabs.innerHTML = dates.map((date, index) => `
        <button class="date-tab ${index === 0 ? "active" : ""}" data-date="${date}">${date}</button>
    `).join("");

    dateTabs.addEventListener("click", event => {
        const button = event.target.closest("[data-date]");
        if (!button) return;
        activeDate = button.dataset.date;
        document.querySelectorAll(".date-tab").forEach(tab => tab.classList.remove("active"));
        button.classList.add("active");
        renderShowtimes(activeDate, selectedMovieId);
    });

    renderShowtimes(activeDate, selectedMovieId);
}

function renderShowtimes(date, selectedMovieId = "") {
    const list = document.querySelector("#showtimeList");
    const items = showtimes.filter(showtime => {
        const sameDate = showtime.date === date;
        const sameMovie = !selectedMovieId || showtime.movieId === selectedMovieId;
        return sameDate && sameMovie;
    });

    if (!items.length) {
        list.innerHTML = `<p class="empty-message">Không có suất chiếu cho ngày này.</p>`;
        return;
    }

    list.innerHTML = items.map(showtime => {
        const movie = getMovie(showtime.movieId);
        return `
            <article class="showtime-item">
                <strong>${movie.title}</strong>
                <span>${showtime.room}</span>
                <span>${showtime.date}</span>
                <span>${showtime.time}</span>
                <span>${formatPrice(showtime.price)}</span>
                <a class="btn btn-primary" href="booking.html?movie=${movie.id}&showtime=${showtime.id}" data-showtime-link="${showtime.id}" data-movie="${movie.id}">Tiếp tục</a>
            </article>
        `;
    }).join("");

    document.querySelectorAll("[data-showtime-link]").forEach(link => {
        link.addEventListener("click", () => {
            saveSelection(link.dataset.movie, link.dataset.showtimeLink);
        });
    });
}

function initBooking() {
    const movieSelect = document.querySelector("#bookingMovie");
    const showtimeSelect = document.querySelector("#bookingShowtime");
    const selectedMovieId = getQueryValue("movie") || localStorage.getItem(storageKeys.selectedMovie) || movies[0].id;
    const selectedShowtimeId = getQueryValue("showtime") || localStorage.getItem(storageKeys.selectedShowtime);
    let selectedSeats = [];

    movieSelect.innerHTML = movies.map(movie => `<option value="${movie.id}">${movie.title}</option>`).join("");
    movieSelect.value = selectedMovieId;

    function updateShowtimeOptions() {
        const movieShowtimes = showtimes.filter(showtime => showtime.movieId === movieSelect.value);
        showtimeSelect.innerHTML = movieShowtimes.map(showtime => `
            <option value="${showtime.id}">${showtime.date} ${showtime.time} - ${showtime.room} - ${formatPrice(showtime.price)}</option>
        `).join("");

        if (movieShowtimes.some(showtime => showtime.id === selectedShowtimeId)) {
            showtimeSelect.value = selectedShowtimeId;
        }

        selectedSeats = [];
        renderSeatMap(selectedSeats);
        updateBookingSummary(selectedSeats);
        saveSelection(movieSelect.value, showtimeSelect.value);
    }

    movieSelect.addEventListener("change", updateShowtimeOptions);
    showtimeSelect.addEventListener("change", () => {
        selectedSeats = [];
        renderSeatMap(selectedSeats);
        updateBookingSummary(selectedSeats);
        saveSelection(movieSelect.value, showtimeSelect.value);
    });

    document.querySelector("#confirmBooking").addEventListener("click", () => {
        if (!selectedSeats.length) {
            alert("Vui lòng chọn ít nhất một ghế.");
            return;
        }

        const showtime = getShowtime(showtimeSelect.value);
        const movie = getMovie(movieSelect.value);
        const booking = {
            movieId: movie.id,
            movieTitle: movie.title,
            showtimeId: showtime.id,
            room: showtime.room,
            date: showtime.date,
            time: showtime.time,
            seats: selectedSeats,
            quantity: selectedSeats.length,
            price: showtime.price,
            total: selectedSeats.length * showtime.price
        };

        localStorage.setItem(storageKeys.booking, JSON.stringify(booking));
        window.location.href = "payment.jsp";
    });

    window.selectSeat = seatId => {
        if (selectedSeats.includes(seatId)) {
            selectedSeats = selectedSeats.filter(seat => seat !== seatId);
        } else {
            selectedSeats.push(seatId);
        }
        renderSeatMap(selectedSeats);
        updateBookingSummary(selectedSeats);
    };

    updateShowtimeOptions();
}

function renderSeatMap(selectedSeats) {
    const seatMap = document.querySelector("#seatMap");
    const showtimeId = document.querySelector("#bookingShowtime").value;
    const bookedSeats = bookedSeatsByShowtime[showtimeId] || [];
    const rows = ["A", "B", "C", "D"];
    const seats = [];

    rows.forEach(row => {
        for (let number = 1; number <= 8; number += 1) {
            seats.push(`${row}${number}`);
        }
    });

    seatMap.innerHTML = seats.map(seatId => {
        const isBooked = bookedSeats.includes(seatId);
        const isSelected = selectedSeats.includes(seatId);
        const status = isBooked ? "booked" : isSelected ? "selected" : "available";
        return `
            <button class="seat ${status}" ${isBooked ? "disabled" : ""} onclick="selectSeat('${seatId}')">
                ${seatId}
            </button>
        `;
    }).join("");
}

function updateBookingSummary(selectedSeats) {
    const movie = getMovie(document.querySelector("#bookingMovie").value);
    const showtime = getShowtime(document.querySelector("#bookingShowtime").value);
    const total = showtime ? selectedSeats.length * showtime.price : 0;

    document.querySelector("#summaryMovie").textContent = movie ? movie.title : "-";
    document.querySelector("#summaryShowtime").textContent = showtime ? `${showtime.date} ${showtime.time} - ${showtime.room}` : "-";
    document.querySelector("#summarySeats").textContent = selectedSeats.length ? selectedSeats.join(", ") : "Chưa chọn";
    document.querySelector("#summaryTotal").textContent = formatPrice(total);
}

function initPayment() {
    const booking = readBooking();
    const summary = document.querySelector("#paymentSummary");
    const form = document.querySelector("#paymentForm");
    const message = document.querySelector("#paymentMessage");

    if (!booking) {
        summary.innerHTML = `<p class="empty-message">Chưa có thông tin đặt vé. Vui lòng đặt vé trước.</p>`;
        form.classList.add("hidden");
        return;
    }

    summary.innerHTML = bookingSummaryHtml(booking);

    form.addEventListener("submit", event => {
        event.preventDefault();
        const method = document.querySelector("input[name='paymentMethod']:checked").value;
        localStorage.setItem(storageKeys.payment, JSON.stringify({
            ...booking,
            method,
            paidAt: new Date().toLocaleString()
        }));
        setMessage(message, "Thanh toán đã được xác nhận. Đang chuyển đến trang kết quả...", "success");
        setTimeout(() => {
            window.location.href = "payment-result.jsp";
        }, 700);
    });
}

function initPaymentResult() {
    const ticketDetails = document.querySelector("#ticketDetails");
    const payment = JSON.parse(localStorage.getItem(storageKeys.payment) || "null");
    const booking = payment || readBooking();

    if (!booking) {
        ticketDetails.innerHTML = `<p>Không tìm thấy thông tin vé.</p>`;
        return;
    }

    ticketDetails.innerHTML = `
        ${bookingSummaryHtml(booking)}
        <p><strong>Phương thức thanh toán:</strong> ${booking.method || "Chưa chọn"}</p>
        <p><strong>Trạng thái:</strong> Thành công</p>
    `;
}

function readBooking() {
    return JSON.parse(localStorage.getItem(storageKeys.booking) || "null");
}

function bookingSummaryHtml(booking) {
    return `
        <p><strong>Phim:</strong> ${booking.movieTitle}</p>
        <p><strong>Suất chiếu:</strong> ${booking.date} ${booking.time}</p>
        <p><strong>Phòng chiếu:</strong> ${booking.room}</p>
        <p><strong>Ghế đã chọn:</strong> ${booking.seats.join(", ")}</p>
        <p><strong>Số lượng vé:</strong> ${booking.quantity}</p>
        <p><strong>Tổng tiền:</strong> ${formatPrice(booking.total)}</p>
    `;
}
