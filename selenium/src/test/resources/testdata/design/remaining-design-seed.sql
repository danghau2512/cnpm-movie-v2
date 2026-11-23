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
