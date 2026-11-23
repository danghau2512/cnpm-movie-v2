-- KAN-53 / F03: scoped fixtures for TC005,006,011,012,013.
-- Import the original project SQL into a SEPARATE cinebook_f03* DB first.
-- D = MySQL CURDATE() at seed time. Does not create users or touch application code.
SET NAMES utf8mb4;
SET SESSION time_zone = '+07:00';
DELIMITER //
DROP PROCEDURE IF EXISTS seed_cinebook_kan53_f03//
CREATE PROCEDURE seed_cinebook_kan53_f03()
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN ROLLBACK; RESIGNAL; END;
    IF DATABASE() NOT LIKE 'cinebook_f03%' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Use a separate cinebook_f03* test database';
    END IF;
    IF NOT EXISTS(SELECT 1 FROM rooms WHERE id=1 AND status='ACTIVE')
       OR NOT EXISTS(SELECT 1 FROM seats WHERE room_id=1)
       OR (SELECT COUNT(*) FROM genres WHERE name IN ('Horror','Drama','Action'))<>3 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Import original SQL first: active Room 1, seats and genres required';
    END IF;
    IF EXISTS(SELECT 1 FROM movies WHERE (id=90301 AND title<>'F03_TEST_Co_lich')
                OR (id=90302 AND title<>'F03_TEST_Khong_co_lich')
                OR (id=90303 AND title<>'F03_TEST_Chi_co_lich_khong_hop_le')) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Movie IDs collide with unrelated data; seed refused';
    END IF;
    IF EXISTS(SELECT 1 FROM showtimes WHERE id BETWEEN 90301 AND 90307
              AND (movie_id<>CASE WHEN id IN (90304,90305,90306) THEN 90303 ELSE 90301 END OR room_id<>1))
       OR EXISTS(SELECT 1 FROM showtimes WHERE movie_id IN (90301,90302,90303) AND id NOT BETWEEN 90301 AND 90307)
       OR EXISTS(SELECT 1 FROM bookings WHERE showtime_id BETWEEN 90301 AND 90307) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Fixture schedule IDs are occupied or booked; use a clean test DB';
    END IF;
    START TRANSACTION;
    INSERT INTO movies(id,title,duration_minutes,age_rating,status) VALUES
      (90301,'F03_TEST_Co_lich',120,'16+','NOW_SHOWING'),
      (90302,'F03_TEST_Khong_co_lich',120,'16+','NOW_SHOWING'),
      (90303,'F03_TEST_Chi_co_lich_khong_hop_le',120,'16+','NOW_SHOWING')
    ON DUPLICATE KEY UPDATE duration_minutes=120,age_rating='16+',status='NOW_SHOWING';
    DELETE FROM movie_genres WHERE movie_id IN (90301,90302,90303);
    INSERT INTO movie_genres(movie_id,genre_id)
      SELECT 90301,id FROM genres WHERE name IN ('Horror','Drama');
    INSERT INTO showtimes(id,movie_id,room_id,start_time,price,status) VALUES
      (90301,90301,1,TIMESTAMP(DATE_ADD(CURDATE(),INTERVAL 7 DAY),'09:00:00'),80000,'OPEN'),
      (90302,90301,1,TIMESTAMP(DATE_ADD(CURDATE(),INTERVAL 7 DAY),'14:00:00'),80000,'OPEN'),
      (90303,90301,1,TIMESTAMP(DATE_ADD(CURDATE(),INTERVAL 8 DAY),'09:00:00'),80000,'OPEN'),
      (90304,90303,1,TIMESTAMP(DATE_SUB(CURDATE(),INTERVAL 1 DAY),'09:00:00'),80000,'OPEN'),
      (90305,90303,1,TIMESTAMP(DATE_ADD(CURDATE(),INTERVAL 7 DAY),'18:00:00'),80000,'CLOSED'),
      (90306,90303,1,TIMESTAMP(DATE_ADD(CURDATE(),INTERVAL 7 DAY),'21:00:00'),80000,'CANCELLED'),
      (90307,90301,1,TIMESTAMP(DATE_ADD(CURDATE(),INTERVAL 9 DAY),'09:00:00'),80000,'OPEN')
    ON DUPLICATE KEY UPDATE start_time=VALUES(start_time),price=80000,status=VALUES(status);
    COMMIT;
END//
CALL seed_cinebook_kan53_f03()//
DROP PROCEDURE seed_cinebook_kan53_f03//
DELIMITER ;
SELECT CURDATE() AS seed_day_D;
SELECT id,movie_id,start_time,status FROM showtimes WHERE id BETWEEN 90301 AND 90307 ORDER BY id;
