-- Fresh install only. Existing cinebook_design_demo from the previous bundle is compatible.
SET NAMES utf8mb4;
CREATE DATABASE `cinebook_design_demo` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `cinebook_design_demo`;
/*
 Navicat Premium Dump SQL

 Source Server         : DoAn
 Source Server Type    : MySQL
 Source Server Version : 90100 (9.1.0)
 Source Host           : localhost:3306
 Source Schema         : movie_ticket_db

 Target Server Type    : MySQL
 Target Server Version : 90100 (9.1.0)
 File Encoding         : 65001

 Date: 14/11/2026 09:19:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for booking_seats
-- ----------------------------
CREATE TABLE `booking_seats`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `booking_id` int NOT NULL,
  `showtime_id` int NOT NULL,
  `seat_id` int NOT NULL,
  `seat_price` decimal(10, 0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_showtime_seat`(`showtime_id` ASC, `seat_id` ASC) USING BTREE,
  INDEX `fk_booking_seats_booking_fk`(`booking_id` ASC) USING BTREE,
  INDEX `fk_booking_seats_seat_fk`(`seat_id` ASC) USING BTREE,
  CONSTRAINT `fk_booking_seats_booking_fk` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_booking_seats_seat_fk` FOREIGN KEY (`seat_id`) REFERENCES `seats` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_booking_seats_showtime_fk` FOREIGN KEY (`showtime_id`) REFERENCES `showtimes` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of booking_seats
-- ----------------------------
INSERT INTO `booking_seats` VALUES (1, 1, 1, 12, 90000);
INSERT INTO `booking_seats` VALUES (2, 1, 1, 13, 90000);
INSERT INTO `booking_seats` VALUES (3, 1, 1, 14, 90000);
INSERT INTO `booking_seats` VALUES (4, 2, 1, 20, 90000);
INSERT INTO `booking_seats` VALUES (5, 2, 1, 21, 90000);
INSERT INTO `booking_seats` VALUES (6, 3, 8, 5, 80000);
INSERT INTO `booking_seats` VALUES (7, 3, 8, 6, 80000);
INSERT INTO `booking_seats` VALUES (8, 4, 1, 4, 90000);
INSERT INTO `booking_seats` VALUES (9, 4, 1, 5, 90000);
INSERT INTO `booking_seats` VALUES (10, 5, 8, 13, 80000);
INSERT INTO `booking_seats` VALUES (11, 5, 8, 14, 80000);
INSERT INTO `booking_seats` VALUES (12, 6, 8, 15, 80000);
INSERT INTO `booking_seats` VALUES (13, 7, 1, 28, 90000);
INSERT INTO `booking_seats` VALUES (14, 7, 1, 29, 90000);
INSERT INTO `booking_seats` VALUES (15, 8, 4, 1, 100000);
INSERT INTO `booking_seats` VALUES (16, 9, 1, 7, 90000);
INSERT INTO `booking_seats` VALUES (17, 10, 1, 6, 90000);
INSERT INTO `booking_seats` VALUES (18, 11, 1, 15, 90000);
INSERT INTO `booking_seats` VALUES (19, 11, 1, 23, 90000);
INSERT INTO `booking_seats` VALUES (20, 12, 1, 22, 90000);
INSERT INTO `booking_seats` VALUES (21, 13, 1, 3, 90000);
INSERT INTO `booking_seats` VALUES (22, 14, 1, 2, 90000);
INSERT INTO `booking_seats` VALUES (23, 15, 7, 91, 85000);
INSERT INTO `booking_seats` VALUES (24, 15, 7, 103, 85000);
INSERT INTO `booking_seats` VALUES (25, 16, 1, 30, 90000);
INSERT INTO `booking_seats` VALUES (37, 26, 9, 4, 80000);
INSERT INTO `booking_seats` VALUES (38, 26, 9, 5, 80000);
INSERT INTO `booking_seats` VALUES (39, 27, 9, 13, 80000);

-- ----------------------------
-- Table structure for bookings
-- ----------------------------
CREATE TABLE `bookings`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `showtime_id` int NOT NULL,
  `booking_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `quantity` int NOT NULL,
  `total_amount` decimal(10, 0) NOT NULL,
  `booking_status` enum('PENDING','CONFIRMED','CANCELLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `payment_status` enum('UNPAID','PAID','FAILED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'UNPAID',
  `hold_expires_at` datetime NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `booking_code`(`booking_code` ASC) USING BTREE,
  INDEX `fk_bookings_user_fk`(`user_id` ASC) USING BTREE,
  INDEX `fk_bookings_showtime_fk`(`showtime_id` ASC) USING BTREE,
  INDEX `idx_bookings_hold_expires`(`booking_status` ASC, `payment_status` ASC, `hold_expires_at` ASC) USING BTREE,
  CONSTRAINT `fk_bookings_showtime_fk` FOREIGN KEY (`showtime_id`) REFERENCES `showtimes` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_bookings_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bookings
-- ----------------------------
INSERT INTO `bookings` VALUES (1, 2, 1, 'BK1777528633566', 3, 270000, 'PENDING', 'UNPAID', NULL, '2026-04-30 12:57:13');
INSERT INTO `bookings` VALUES (2, 2, 1, 'BK1777528718795', 2, 180000, 'PENDING', 'UNPAID', NULL, '2026-04-30 12:58:38');
INSERT INTO `bookings` VALUES (3, 2, 8, 'BK1777529474838', 2, 160000, 'PENDING', 'UNPAID', NULL, '2026-04-30 13:11:14');
INSERT INTO `bookings` VALUES (4, 2, 1, 'BK1777529709000', 2, 180000, 'PENDING', 'UNPAID', NULL, '2026-04-30 13:15:09');
INSERT INTO `bookings` VALUES (5, 2, 8, 'BK1777529771792', 2, 160000, 'PENDING', 'UNPAID', NULL, '2026-04-30 13:16:11');
INSERT INTO `bookings` VALUES (6, 2, 8, 'BK1777529781447', 1, 80000, 'CONFIRMED', 'PAID', NULL, '2026-04-30 13:16:21');
INSERT INTO `bookings` VALUES (7, 2, 1, 'BK1777530774088', 2, 180000, 'CONFIRMED', 'PAID', NULL, '2026-04-30 13:32:54');
INSERT INTO `bookings` VALUES (8, 2, 4, 'BK1777532695763', 1, 100000, 'PENDING', 'UNPAID', NULL, '2026-04-30 14:04:55');
INSERT INTO `bookings` VALUES (9, 2, 1, 'BK1779374242714', 1, 90000, 'PENDING', 'UNPAID', NULL, '2026-05-21 21:37:22');
INSERT INTO `bookings` VALUES (10, 2, 1, 'BK1779377690007', 1, 90000, 'PENDING', 'UNPAID', NULL, '2026-05-21 22:34:50');
INSERT INTO `bookings` VALUES (11, 2, 1, 'BK1779985685444', 2, 180000, 'PENDING', 'UNPAID', NULL, '2026-05-28 23:28:05');
INSERT INTO `bookings` VALUES (12, 2, 1, 'BK1779986262987', 1, 90000, 'PENDING', 'UNPAID', NULL, '2026-05-28 23:37:42');
INSERT INTO `bookings` VALUES (13, 2, 1, 'BK1779986355209', 1, 90000, 'PENDING', 'UNPAID', NULL, '2026-05-28 23:39:15');
INSERT INTO `bookings` VALUES (14, 2, 1, 'BK1779986531353', 1, 90000, 'PENDING', 'UNPAID', NULL, '2026-05-28 23:42:11');
INSERT INTO `bookings` VALUES (15, 2, 7, 'BK1779987095356', 2, 170000, 'PENDING', 'UNPAID', NULL, '2026-05-28 23:51:35');
INSERT INTO `bookings` VALUES (16, 2, 1, 'BK1780073023024', 1, 90000, 'PENDING', 'UNPAID', NULL, '2026-05-29 23:43:43');
INSERT INTO `bookings` VALUES (17, 2, 1, 'BK1780073175334', 1, 90000, 'CANCELLED', 'FAILED', NULL, '2026-05-29 23:46:15');
INSERT INTO `bookings` VALUES (18, 2, 1, 'BK1780073755156', 1, 90000, 'CANCELLED', 'FAILED', NULL, '2026-05-29 23:55:55');
INSERT INTO `bookings` VALUES (19, 1, 1, 'BK1780814888123', 1, 90000, 'CANCELLED', 'FAILED', NULL, '2026-06-07 13:48:08');
INSERT INTO `bookings` VALUES (20, 1, 1, 'BK1780814888240', 1, 90000, 'CANCELLED', 'FAILED', NULL, '2026-06-07 13:48:08');
INSERT INTO `bookings` VALUES (21, 1, 1, 'BK1780814888319', 1, 90000, 'CANCELLED', 'FAILED', NULL, '2026-06-07 13:48:08');
INSERT INTO `bookings` VALUES (23, 1, 1, 'BK1780814888458', 1, 90000, 'CANCELLED', 'FAILED', NULL, '2026-06-07 13:48:08');
INSERT INTO `bookings` VALUES (24, 1, 1, 'BK1780814888486', 1, 90000, 'CANCELLED', 'FAILED', NULL, '2026-06-07 13:48:08');
INSERT INTO `bookings` VALUES (25, 3, 9, 'BK1790951546662', 2, 160000, 'CANCELLED', 'FAILED', NULL, '2026-10-02 21:32:26');
INSERT INTO `bookings` VALUES (26, 3, 9, 'BK1790952378577', 2, 160000, 'PENDING', 'UNPAID', '2026-10-02 21:56:18', '2026-10-02 21:46:18');
INSERT INTO `bookings` VALUES (27, 3, 9, 'BK1790952602613', 1, 80000, 'CONFIRMED', 'PAID', NULL, '2026-10-02 21:50:02');

-- ----------------------------
-- Table structure for genres
-- ----------------------------
CREATE TABLE `genres`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of genres
-- ----------------------------
INSERT INTO `genres` VALUES (3, 'Action');
INSERT INTO `genres` VALUES (4, 'Comedy');
INSERT INTO `genres` VALUES (6, 'Drama');
INSERT INTO `genres` VALUES (7, 'Family');
INSERT INTO `genres` VALUES (5, 'Horror');
INSERT INTO `genres` VALUES (2, 'Romance');
INSERT INTO `genres` VALUES (1, 'Sci-Fi');

-- ----------------------------
-- Table structure for movie_genres
-- ----------------------------
CREATE TABLE `movie_genres`  (
  `movie_id` int NOT NULL,
  `genre_id` int NOT NULL,
  PRIMARY KEY (`movie_id`, `genre_id`) USING BTREE,
  INDEX `fk_movie_genres_genre_fk`(`genre_id` ASC) USING BTREE,
  CONSTRAINT `fk_movie_genres_genre_fk` FOREIGN KEY (`genre_id`) REFERENCES `genres` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_movie_genres_movie_fk` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of movie_genres
-- ----------------------------
INSERT INTO `movie_genres` VALUES (5, 2);
INSERT INTO `movie_genres` VALUES (7, 2);
INSERT INTO `movie_genres` VALUES (3, 3);
INSERT INTO `movie_genres` VALUES (6, 3);
INSERT INTO `movie_genres` VALUES (2, 5);
INSERT INTO `movie_genres` VALUES (4, 5);
INSERT INTO `movie_genres` VALUES (1, 6);
INSERT INTO `movie_genres` VALUES (2, 6);
INSERT INTO `movie_genres` VALUES (4, 6);
INSERT INTO `movie_genres` VALUES (7, 6);
INSERT INTO `movie_genres` VALUES (8, 6);
INSERT INTO `movie_genres` VALUES (2, 7);
INSERT INTO `movie_genres` VALUES (8, 7);

-- ----------------------------
-- Table structure for movies
-- ----------------------------
CREATE TABLE `movies`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `duration_minutes` int NOT NULL,
  `age_rating` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `short_description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `poster_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `trailer_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `release_date` date NULL DEFAULT NULL,
  `status` enum('NOW_SHOWING','COMING_SOON','HIDDEN') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NOW_SHOWING',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of movies
-- ----------------------------
INSERT INTO `movies` VALUES (1, 'Heo Năm Móng', 103, '18+', 'Heo Năm Móng gợi mở truyền thuyết Cô Năm Hợi đầy ám ảnh về nghiệp báo và sự tái sinh. \nNhân vật Sa bị cuốn vào những uẩn khúc giữa quá khứ, tội lỗi và oán linh chưa thể hóa giải.', 'Truyền thuyết Cô Năm Hợi được truyền miệng qua nhiều thế hệ, như một lời nhắc về sự tái sinh đầy nghiệt ngã: linh hồn chết oan hoặc mang nhiều nghiệp quả nên mắc kẹt trong thân xác loài vật, mang theo ký ức và oán lệnh chưa thể hóa giải. Và giữa những hoa văn Khmer cổ kính và ánh đỏ linh thiêng, Sa - nữ chính của Heo Năm Móng hiện lên như một linh hồn đang đứng giữa hai cõi, như thể hai số phận đang chồng lên nhau. Liệu Sa còn điều gì vướng mắc ở cõi trần? Hay chính cô là kẻ từng gây nên tội lỗi, để rồi phải gánh chịu nghiệp báo?', 'https://cdn.moveek.com/storage/media/cache/tall/69d32479337d8636080459.jpg', 'https://www.youtube.com/embed/LzMixm8ijY8', '2026-04-24', 'HIDDEN', '2026-04-28 22:40:23');
INSERT INTO `movies` VALUES (2, 'Phí Phông: Quỷ Máu Rừng Thiêng', 120, '16+', 'Hai pháp sư tập sự Còn và Dương lên núi giải cứu người mẹ bị lời nguyền Phí Phông ám hại. Tại bản sâu, những cái chết ghê rợn liên tiếp xảy ra, kéo họ vào cuộc truy lùng bí ẩn giữa rừng thiêng.', 'Phí Phông, loài quỷ khát máu trong truyền thuyết dân gian của đồng bào miền núi gây ám ảnh bao đời nay. Phim xoay quanh Còn (Kiều Minh Tuấn) và Dương (Minh Anh), hai pháp sư tập sự lên núi cứu người mẹ đang bị lời nguyền Phí Phông đánh gục. Cùng lúc đó, trong bản sâu cũng xảy ra nhiều cái chết ghê rợn. Mọi nghi ngờ đổ dồn về hai mẹ con Mon (Diệp Bảo Ngọc) và Lua (Nina Nutthacha), những người mang đặc tính y hệt Phí Phông. Thế nhưng, vẫn còn những bí mật động trời bị chôn vùi trong chốn rừng thiêng nước độc, cuốn hai anh em Còn và Dương vào cuộc truy lùng “Phí Phông” không hồi kết.', 'https://cdn.moveek.com/storage/media/cache/tall/69d4908c16db9598942516.jpg', 'https://www.youtube.com/embed/4Lv-xRYgwCM', '2026-04-24', 'NOW_SHOWING', '2026-04-28 22:40:23');
INSERT INTO `movies` VALUES (3, 'Anh Hùng - 2', 122, '13+', 'Người cha đơn thân Hùng, một tài xế taxi, bất ngờ bị cuốn vào phi vụ lừa đảo từ thiện tiền tỉ. Trong lúc đó, anh phải chạy đua với thời gian để cứu cô con gái nhỏ đang nguy kịch.', 'Câu chuyện phim theo chân Hùng (Thái Hòa) - người cha đơn thân kiêm tài xế taxi và đồng nghiệp hãng xe là Tuấn (Võ Tấn Phát) bị cuốn vào một phi vụ lừa đảo từ thiện tiền tỉ trong khi sinh mạng cô con gái nhỏ của anh đang nằm gọn trong tay tử thần.', 'https://cdn.moveek.com/storage/media/cache/tall/69b7beb4106e9777364973.jpg', 'https://www.youtube.com/embed/zPexIyX38IA', '2026-04-24', 'NOW_SHOWING', '2026-04-28 22:40:23');
INSERT INTO `movies` VALUES (4, 'Đại Tiệc Trăng Máu 8', 98, '16+', 'Một vị đạo diễn bị coi thường quyết thực hiện bộ phim 35 phút chỉ bằng một cú máy. Giữa hàng loạt tình huống dở khóc dở cười, ông phải hoàn thành tác phẩm để giành lại sự tôn trọng từ con gái.', 'Đại Tiệc Trăng Máu 8 theo chân một vị đạo diễn hay bị coi thường (Vân Sơn đóng) trong dự án thử thách nhất đời ông: thực hiện một bộ phim dài 35 phút chỉ với một cú máy. Hàng loạt tình huống dở khóc dở cười xảy ra khi các diễn viên liên tục gây chuyện “khó đỡ”. Thế nhưng, việc hoàn thành tác phẩm là cơ hội cuối cùng để ông giành lại sự tôn trọng từ cô con gái đam mê nghệ thuật.', 'https://cdn.moveek.com/storage/media/cache/tall/69bcc2a5cfe73633073073.jpg', 'https://www.youtube.com/embed/ar8o8xtGCv0', '2026-04-24', 'NOW_SHOWING', '2026-04-28 22:40:23');
INSERT INTO `movies` VALUES (5, '\r\nYêu Nữ Thích Hàng Hiệu 2', 119, '13+', 'Tại Làng Sứa Đỏ nghèo khó, Trùm Sò nổi tiếng giàu có nhưng keo kiệt. Sau khi mất số vàng lớn, gã bị cuốn vào một vụ cướp đầy rắc rối cùng Thị Hến và Tôm Hùm.', 'Hai mươi năm sau màn hóa thân kinh điển vào các vai diễn Miranda, Andy, Emily và Nigel — Meryl Streep, Anne Hathaway, Emily Blunt và Stanley Tucci sẽ chính thức trở lại với những con phố thời thượng của New York và văn phòng sang trọng của Tạp chí Runway trong \"The Devil Wears Prada 2\" (Yêu Nữ Thích Hàng Hiệu 2). Đây là phần phim tiếp theo cực kỳ được mong đợi từ 20th Century Studios, kế thừa sức hút từ hiện tượng điện ảnh năm 2006 từng định hình phong cách cho cả một thế hệ.', 'https://cdn.moveek.com/storage/media/cache/tall/69d86ef126b96736991070.jpg', 'https://www.youtube.com/embed/ZS77vl5hLpg', '2026-04-24', 'NOW_SHOWING', '2026-04-28 22:40:23');
INSERT INTO `movies` VALUES (6, 'Lúc Đó Tôi Đã Chuyển Sinh Thành Smile', 105, '13+', 'Sau Lễ hội Khai quốc Tempest, Rimuru cùng bạn bè đến khu nghỉ dưỡng của Sarion để thư giãn. Nhưng sự xuất hiện của Yura đã kéo họ vào những bí ẩn và hiểm nguy đang trỗi dậy dưới đại dương.', 'Lúc đó tôi đã chuyển sinh thành Slime: Nước Mắt Đại Dương là phần phim điện ảnh thứ 2 thuộc franchise anime nổi tiếng \"Lúc đó tôi đã chuyển sinh thành Slime\". Lấy bối cảnh sau Lễ hội Khai quốc Tempest, Rimuru cùng những người bạn đồng hành quyết định ghé thăm một khu nghỉ dưỡng sang trọng do Elmesia, người đứng đầu Đế chế Pháp Thuật Sarion, cai quản, để tận hưởng thời gian thư giãn hiếm hoi sau những biến cố. Tuy nhiên, chuyến nghỉ dưỡng nhanh chóng bị xáo trộn khi nhân vật bí ẩn tên Yura xuất hiện, kéo theo một chuỗi những sự kiện bí ẩn và mối nguy đang âm thầm trỗi dậy dưới đại dương.', 'https://cdn.moveek.com/storage/media/cache/tall/69d866d7be2b6869759553.jpg', 'https://www.youtube.com/embed/h-e7rFvl0_M', '2026-04-24', 'NOW_SHOWING', '2026-04-28 22:40:23');
INSERT INTO `movies` VALUES (7, 'Thỏ Ơi', 127, '18+', 'Lấy cảm hứng từ những câu chuyện có thật, Thỏ Ơi!! phơi bày mặt tối của tình yêu và hôn nhân, nơi những chiếc \"mặt nạ\" sự thật dần bị tháo bỏ.', 'Lấy cảm hứng từ những câu chuyện có thật, Thỏ Ơi!! phơi bày mặt tối của tình yêu và hôn nhân, nơi những chiếc \"mặt nạ\" sự thật dần bị tháo bỏ.', 'https://cdn.moveek.com/storage/media/cache/tall/69856f050d648746621289.jpg', 'https://www.youtube.com/embed/8W34pVYW8i0', '2026-04-24', 'NOW_SHOWING', '2026-04-30 12:36:55');
INSERT INTO `movies` VALUES (8, 'Bố Già Trở Lại', 95, '18+', 'Ông Đằng, một trinh sát ngầm đã rời xa quá khứ máu lửa, buộc phải trở lại cuộc chiến khi con gái bị bắt cóc. Hành trình giải cứu không chỉ đầy nguy hiểm mà còn là cơ hội để ông hàn gắn tình cảm gia đình.', 'Bộ phim kể về ông Đằng, một trinh sát ngầm tưởng chừng được khép lại quá khứ đầy máu lửa – buộc phải dấn thân vào một cuộc chiến khốc liệt khi con gái ông, Yến, bất ngờ bị bắt cóc. Cuộc giải cứu con gái không chỉ là một hành trình đầy kịch tính, kết hợp giữa yếu tố hành động chống lại cái ác một cách không khoan nhượng mà còn là một hành trình hàn gắn tình cảm gia đình của ông sau những rạn nứt, đổ vỡ.', 'https://cdn.moveek.com/storage/media/cache/tall/695dc176e1178808223383.jpg', 'https://www.youtube.com/embed/https://www.youtube.com/embed/', '2026-04-24', 'NOW_SHOWING', '2026-04-30 12:38:16');

-- ----------------------------
-- Table structure for payments
-- ----------------------------
CREATE TABLE `payments`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `booking_id` int NOT NULL,
  `payment_method` enum('VNPAY','PAY_AT_COUNTER') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `amount` decimal(10, 0) NOT NULL,
  `payment_status` enum('PENDING','SUCCESS','FAILED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `paid_at` datetime NULL DEFAULT NULL,
  `transaction_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_payments_booking_fk`(`booking_id` ASC) USING BTREE,
  CONSTRAINT `fk_payments_booking_fk` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of payments
-- ----------------------------
INSERT INTO `payments` VALUES (1, 4, 'PAY_AT_COUNTER', 180000, 'PENDING', NULL, NULL);
INSERT INTO `payments` VALUES (2, 6, 'VNPAY', 80000, 'SUCCESS', '2026-04-30 13:18:47', 'VNPAY_DEMO_1777529927052');
INSERT INTO `payments` VALUES (3, 7, 'VNPAY', 180000, 'SUCCESS', '2026-04-30 13:33:48', '15518053');
INSERT INTO `payments` VALUES (4, 12, 'PAY_AT_COUNTER', 90000, 'PENDING', NULL, NULL);
INSERT INTO `payments` VALUES (5, 14, 'PAY_AT_COUNTER', 90000, 'PENDING', NULL, NULL);
INSERT INTO `payments` VALUES (6, 15, 'PAY_AT_COUNTER', 170000, 'PENDING', NULL, NULL);
INSERT INTO `payments` VALUES (7, 18, 'PAY_AT_COUNTER', 90000, 'FAILED', NULL, NULL);
INSERT INTO `payments` VALUES (9, 25, 'VNPAY', 160000, 'PENDING', NULL, '25_1790951574668');
INSERT INTO `payments` VALUES (10, 26, 'VNPAY', 160000, 'PENDING', NULL, '26_1790952382788');
INSERT INTO `payments` VALUES (11, 27, 'VNPAY', 80000, 'SUCCESS', '2026-10-02 21:50:42', '15693636');

-- ----------------------------
-- Table structure for rooms
-- ----------------------------
CREATE TABLE `rooms`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `total_seats` int NOT NULL,
  `status` enum('ACTIVE','INACTIVE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of rooms
-- ----------------------------
INSERT INTO `rooms` VALUES (1, 'Room 1', 32, 'ACTIVE');
INSERT INTO `rooms` VALUES (2, 'Room 2', 32, 'ACTIVE');
INSERT INTO `rooms` VALUES (3, 'Room 3', 32, 'ACTIVE');
INSERT INTO `rooms` VALUES (4, 'Room 4', 32, 'ACTIVE');

-- ----------------------------
-- Table structure for seats
-- ----------------------------
CREATE TABLE `seats`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `room_id` int NOT NULL,
  `seat_row` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `seat_number` int NOT NULL,
  `seat_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_room_seat`(`room_id` ASC, `seat_code` ASC) USING BTREE,
  CONSTRAINT `fk_seats_room_fk` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 129 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of seats
-- ----------------------------
INSERT INTO `seats` VALUES (1, 1, 'A', 1, 'A1');
INSERT INTO `seats` VALUES (2, 1, 'A', 2, 'A2');
INSERT INTO `seats` VALUES (3, 1, 'A', 3, 'A3');
INSERT INTO `seats` VALUES (4, 1, 'A', 4, 'A4');
INSERT INTO `seats` VALUES (5, 1, 'A', 5, 'A5');
INSERT INTO `seats` VALUES (6, 1, 'A', 6, 'A6');
INSERT INTO `seats` VALUES (7, 1, 'A', 7, 'A7');
INSERT INTO `seats` VALUES (8, 1, 'A', 8, 'A8');
INSERT INTO `seats` VALUES (9, 1, 'B', 1, 'B1');
INSERT INTO `seats` VALUES (10, 1, 'B', 2, 'B2');
INSERT INTO `seats` VALUES (11, 1, 'B', 3, 'B3');
INSERT INTO `seats` VALUES (12, 1, 'B', 4, 'B4');
INSERT INTO `seats` VALUES (13, 1, 'B', 5, 'B5');
INSERT INTO `seats` VALUES (14, 1, 'B', 6, 'B6');
INSERT INTO `seats` VALUES (15, 1, 'B', 7, 'B7');
INSERT INTO `seats` VALUES (16, 1, 'B', 8, 'B8');
INSERT INTO `seats` VALUES (17, 1, 'C', 1, 'C1');
INSERT INTO `seats` VALUES (18, 1, 'C', 2, 'C2');
INSERT INTO `seats` VALUES (19, 1, 'C', 3, 'C3');
INSERT INTO `seats` VALUES (20, 1, 'C', 4, 'C4');
INSERT INTO `seats` VALUES (21, 1, 'C', 5, 'C5');
INSERT INTO `seats` VALUES (22, 1, 'C', 6, 'C6');
INSERT INTO `seats` VALUES (23, 1, 'C', 7, 'C7');
INSERT INTO `seats` VALUES (24, 1, 'C', 8, 'C8');
INSERT INTO `seats` VALUES (25, 1, 'D', 1, 'D1');
INSERT INTO `seats` VALUES (26, 1, 'D', 2, 'D2');
INSERT INTO `seats` VALUES (27, 1, 'D', 3, 'D3');
INSERT INTO `seats` VALUES (28, 1, 'D', 4, 'D4');
INSERT INTO `seats` VALUES (29, 1, 'D', 5, 'D5');
INSERT INTO `seats` VALUES (30, 1, 'D', 6, 'D6');
INSERT INTO `seats` VALUES (31, 1, 'D', 7, 'D7');
INSERT INTO `seats` VALUES (32, 1, 'D', 8, 'D8');
INSERT INTO `seats` VALUES (33, 2, 'D', 1, 'D1');
INSERT INTO `seats` VALUES (34, 3, 'D', 1, 'D1');
INSERT INTO `seats` VALUES (35, 4, 'D', 1, 'D1');
INSERT INTO `seats` VALUES (36, 2, 'C', 1, 'C1');
INSERT INTO `seats` VALUES (37, 3, 'C', 1, 'C1');
INSERT INTO `seats` VALUES (38, 4, 'C', 1, 'C1');
INSERT INTO `seats` VALUES (39, 2, 'B', 1, 'B1');
INSERT INTO `seats` VALUES (40, 3, 'B', 1, 'B1');
INSERT INTO `seats` VALUES (41, 4, 'B', 1, 'B1');
INSERT INTO `seats` VALUES (42, 2, 'A', 1, 'A1');
INSERT INTO `seats` VALUES (43, 3, 'A', 1, 'A1');
INSERT INTO `seats` VALUES (44, 4, 'A', 1, 'A1');
INSERT INTO `seats` VALUES (45, 2, 'D', 2, 'D2');
INSERT INTO `seats` VALUES (46, 3, 'D', 2, 'D2');
INSERT INTO `seats` VALUES (47, 4, 'D', 2, 'D2');
INSERT INTO `seats` VALUES (48, 2, 'C', 2, 'C2');
INSERT INTO `seats` VALUES (49, 3, 'C', 2, 'C2');
INSERT INTO `seats` VALUES (50, 4, 'C', 2, 'C2');
INSERT INTO `seats` VALUES (51, 2, 'B', 2, 'B2');
INSERT INTO `seats` VALUES (52, 3, 'B', 2, 'B2');
INSERT INTO `seats` VALUES (53, 4, 'B', 2, 'B2');
INSERT INTO `seats` VALUES (54, 2, 'A', 2, 'A2');
INSERT INTO `seats` VALUES (55, 3, 'A', 2, 'A2');
INSERT INTO `seats` VALUES (56, 4, 'A', 2, 'A2');
INSERT INTO `seats` VALUES (57, 2, 'D', 3, 'D3');
INSERT INTO `seats` VALUES (58, 3, 'D', 3, 'D3');
INSERT INTO `seats` VALUES (59, 4, 'D', 3, 'D3');
INSERT INTO `seats` VALUES (60, 2, 'C', 3, 'C3');
INSERT INTO `seats` VALUES (61, 3, 'C', 3, 'C3');
INSERT INTO `seats` VALUES (62, 4, 'C', 3, 'C3');
INSERT INTO `seats` VALUES (63, 2, 'B', 3, 'B3');
INSERT INTO `seats` VALUES (64, 3, 'B', 3, 'B3');
INSERT INTO `seats` VALUES (65, 4, 'B', 3, 'B3');
INSERT INTO `seats` VALUES (66, 2, 'A', 3, 'A3');
INSERT INTO `seats` VALUES (67, 3, 'A', 3, 'A3');
INSERT INTO `seats` VALUES (68, 4, 'A', 3, 'A3');
INSERT INTO `seats` VALUES (69, 2, 'D', 4, 'D4');
INSERT INTO `seats` VALUES (70, 3, 'D', 4, 'D4');
INSERT INTO `seats` VALUES (71, 4, 'D', 4, 'D4');
INSERT INTO `seats` VALUES (72, 2, 'C', 4, 'C4');
INSERT INTO `seats` VALUES (73, 3, 'C', 4, 'C4');
INSERT INTO `seats` VALUES (74, 4, 'C', 4, 'C4');
INSERT INTO `seats` VALUES (75, 2, 'B', 4, 'B4');
INSERT INTO `seats` VALUES (76, 3, 'B', 4, 'B4');
INSERT INTO `seats` VALUES (77, 4, 'B', 4, 'B4');
INSERT INTO `seats` VALUES (78, 2, 'A', 4, 'A4');
INSERT INTO `seats` VALUES (79, 3, 'A', 4, 'A4');
INSERT INTO `seats` VALUES (80, 4, 'A', 4, 'A4');
INSERT INTO `seats` VALUES (81, 2, 'D', 5, 'D5');
INSERT INTO `seats` VALUES (82, 3, 'D', 5, 'D5');
INSERT INTO `seats` VALUES (83, 4, 'D', 5, 'D5');
INSERT INTO `seats` VALUES (84, 2, 'C', 5, 'C5');
INSERT INTO `seats` VALUES (85, 3, 'C', 5, 'C5');
INSERT INTO `seats` VALUES (86, 4, 'C', 5, 'C5');
INSERT INTO `seats` VALUES (87, 2, 'B', 5, 'B5');
INSERT INTO `seats` VALUES (88, 3, 'B', 5, 'B5');
INSERT INTO `seats` VALUES (89, 4, 'B', 5, 'B5');
INSERT INTO `seats` VALUES (90, 2, 'A', 5, 'A5');
INSERT INTO `seats` VALUES (91, 3, 'A', 5, 'A5');
INSERT INTO `seats` VALUES (92, 4, 'A', 5, 'A5');
INSERT INTO `seats` VALUES (93, 2, 'D', 6, 'D6');
INSERT INTO `seats` VALUES (94, 3, 'D', 6, 'D6');
INSERT INTO `seats` VALUES (95, 4, 'D', 6, 'D6');
INSERT INTO `seats` VALUES (96, 2, 'C', 6, 'C6');
INSERT INTO `seats` VALUES (97, 3, 'C', 6, 'C6');
INSERT INTO `seats` VALUES (98, 4, 'C', 6, 'C6');
INSERT INTO `seats` VALUES (99, 2, 'B', 6, 'B6');
INSERT INTO `seats` VALUES (100, 3, 'B', 6, 'B6');
INSERT INTO `seats` VALUES (101, 4, 'B', 6, 'B6');
INSERT INTO `seats` VALUES (102, 2, 'A', 6, 'A6');
INSERT INTO `seats` VALUES (103, 3, 'A', 6, 'A6');
INSERT INTO `seats` VALUES (104, 4, 'A', 6, 'A6');
INSERT INTO `seats` VALUES (105, 2, 'D', 7, 'D7');
INSERT INTO `seats` VALUES (106, 3, 'D', 7, 'D7');
INSERT INTO `seats` VALUES (107, 4, 'D', 7, 'D7');
INSERT INTO `seats` VALUES (108, 2, 'C', 7, 'C7');
INSERT INTO `seats` VALUES (109, 3, 'C', 7, 'C7');
INSERT INTO `seats` VALUES (110, 4, 'C', 7, 'C7');
INSERT INTO `seats` VALUES (111, 2, 'B', 7, 'B7');
INSERT INTO `seats` VALUES (112, 3, 'B', 7, 'B7');
INSERT INTO `seats` VALUES (113, 4, 'B', 7, 'B7');
INSERT INTO `seats` VALUES (114, 2, 'A', 7, 'A7');
INSERT INTO `seats` VALUES (115, 3, 'A', 7, 'A7');
INSERT INTO `seats` VALUES (116, 4, 'A', 7, 'A7');
INSERT INTO `seats` VALUES (117, 2, 'D', 8, 'D8');
INSERT INTO `seats` VALUES (118, 3, 'D', 8, 'D8');
INSERT INTO `seats` VALUES (119, 4, 'D', 8, 'D8');
INSERT INTO `seats` VALUES (120, 2, 'C', 8, 'C8');
INSERT INTO `seats` VALUES (121, 3, 'C', 8, 'C8');
INSERT INTO `seats` VALUES (122, 4, 'C', 8, 'C8');
INSERT INTO `seats` VALUES (123, 2, 'B', 8, 'B8');
INSERT INTO `seats` VALUES (124, 3, 'B', 8, 'B8');
INSERT INTO `seats` VALUES (125, 4, 'B', 8, 'B8');
INSERT INTO `seats` VALUES (126, 2, 'A', 8, 'A8');
INSERT INTO `seats` VALUES (127, 3, 'A', 8, 'A8');
INSERT INTO `seats` VALUES (128, 4, 'A', 8, 'A8');

-- ----------------------------
-- Table structure for showtimes
-- ----------------------------
CREATE TABLE `showtimes`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `movie_id` int NOT NULL,
  `room_id` int NOT NULL,
  `start_time` datetime NOT NULL,
  `price` decimal(10, 0) NOT NULL,
  `status` enum('OPEN','CLOSED','CANCELLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'OPEN',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_showtimes_movie_fk`(`movie_id` ASC) USING BTREE,
  INDEX `fk_showtimes_room_fk`(`room_id` ASC) USING BTREE,
  CONSTRAINT `fk_showtimes_movie_fk` FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_showtimes_room_fk` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of showtimes
-- ----------------------------
INSERT INTO `showtimes` VALUES (1, 1, 1, '2026-11-01 18:30:00', 90000, 'OPEN');
INSERT INTO `showtimes` VALUES (2, 1, 2, '2026-11-01 21:00:00', 95000, 'OPEN');
INSERT INTO `showtimes` VALUES (3, 2, 3, '2026-11-01 19:00:00', 80000, 'OPEN');
INSERT INTO `showtimes` VALUES (4, 3, 1, '2026-11-02 20:15:00', 100000, 'OPEN');
INSERT INTO `showtimes` VALUES (5, 4, 4, '2026-11-02 17:45:00', 75000, 'OPEN');
INSERT INTO `showtimes` VALUES (6, 5, 2, '2026-11-03 22:00:00', 100000, 'OPEN');
INSERT INTO `showtimes` VALUES (7, 6, 3, '2026-11-03 18:00:00', 85000, 'OPEN');
INSERT INTO `showtimes` VALUES (8, 2, 1, '2026-11-04 20:00:00', 80000, 'OPEN');
INSERT INTO `showtimes` VALUES (9, 7, 1, '2026-11-05 09:30:00', 80000, 'OPEN');
INSERT INTO `showtimes` VALUES (10, 7, 2, '2026-11-05 13:45:00', 85000, 'OPEN');
INSERT INTO `showtimes` VALUES (11, 7, 1, '2026-11-05 18:30:00', 90000, 'OPEN');
INSERT INTO `showtimes` VALUES (12, 7, 3, '2026-11-06 20:00:00', 90000, 'OPEN');
INSERT INTO `showtimes` VALUES (13, 8, 2, '2026-11-05 10:15:00', 85000, 'OPEN');
INSERT INTO `showtimes` VALUES (14, 8, 3, '2026-11-05 15:30:00', 90000, 'OPEN');
INSERT INTO `showtimes` VALUES (15, 8, 1, '2026-11-05 19:45:00', 95000, 'OPEN');
INSERT INTO `showtimes` VALUES (16, 8, 2, '2026-11-06 21:15:00', 95000, 'OPEN');

-- ----------------------------
-- Table structure for users
-- ----------------------------
CREATE TABLE `users`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('CUSTOMER','ADMIN') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CUSTOMER',
  `status` enum('ACTIVE','LOCKED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'Admin', 'admin@gmail.com', '0900000000', '123456', 'ADMIN', 'ACTIVE', '2026-04-28 22:40:23');
INSERT INTO `users` VALUES (2, 'Nguyen Van A', 'user@gmail.com', '0911111111', '123456', 'CUSTOMER', 'ACTIVE', '2026-04-28 22:40:23');
INSERT INTO `users` VALUES (3, 'Nguyễn Đăng Hậu-11A4', 'gaankteam@gmail.com', '0389102430', 'pbkdf2$210000$SxhPHx4kHda6ENWqwgjDag==$M5Skr4RbGcsV1P9j4x1zL1M5+5/+OSw5N0hsXEppMPI=', 'CUSTOMER', 'ACTIVE', '2026-10-02 21:27:13');
INSERT INTO `users` VALUES (4, 'Hau nguyen', 'hausieucapvippro1@gmail.com', '0389102430', 'pbkdf2$210000$zMHrQHNCCXobwFtSe+gH0A==$9LT7tOclRF6EfgiQtM88F+STp2gqNMctddDe41elrRY=', 'ADMIN', 'ACTIVE', '2026-10-02 21:34:29');

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO users(full_name,email,phone,password_hash,role,status)
VALUES('Demo Customer B','demo.customer2@example.test','0900000002','Demo_2026!','CUSTOMER','ACTIVE');
-- Import original SQL into a SEPARATE cinebook_design* DB before this fixture.
-- No schema/business changes; only one locked test account and one inactive test room.
SET NAMES utf8mb4;
DELIMITER //
DROP PROCEDURE IF EXISTS seed_remaining_design//
CREATE PROCEDURE seed_remaining_design()
BEGIN
    IF DATABASE() NOT LIKE 'cinebook_design%' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Use a separate cinebook_design* test database';
    END IF;
    IF EXISTS(SELECT 1 FROM users WHERE email='design.locked@example.test')
       OR EXISTS(SELECT 1 FROM rooms WHERE id=90400) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Fixture already exists; use a fresh test DB or inspect it before rerunning';
    END IF;
    INSERT INTO users(full_name,email,phone,password_hash,role,status)
      VALUES('Design Locked','design.locked@example.test','0901234567','TestDesign_2026!','CUSTOMER','LOCKED');
    INSERT INTO rooms(id,name,total_seats,status) VALUES(90400,'Design INACTIVE',32,'INACTIVE');
END//
CALL seed_remaining_design()//
DROP PROCEDURE seed_remaining_design//
DELIMITER ;


SELECT 'READY: cinebook_design_demo (F01-F08)' AS selenium_demo;
