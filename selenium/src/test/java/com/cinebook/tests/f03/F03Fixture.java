package com.cinebook.tests.f03;

import com.cinebook.utils.TestConfig;
import java.sql.*;

/** Giữ các ID của bảng quyết định, chỉ trong thời gian chạy lớp F03. */
final class F03Fixture implements AutoCloseable {
    private final Connection db;

    private F03Fixture(Connection db) { this.db = db; }

    static F03Fixture prepare(TestConfig config) throws SQLException {
        Connection db = DriverManager.getConnection(config.required("technique.dbUrl"),
                config.get("technique.dbUser", "root"), config.get("technique.dbPassword", ""));
        F03Fixture fixture = new F03Fixture(db);
        try (Statement sql = db.createStatement()) {
            if (!"cinebook_design_demo".equals(db.getCatalog()))
                throw new SQLException("F03 cần chạy trên DB thử cinebook_design_demo.");
            try (ResultSet lock = sql.executeQuery("SELECT GET_LOCK('cinebook_design_demo.f03',0)")) {
                if (!lock.next() || lock.getInt(1) != 1) throw new SQLException("Một phiên F03 khác đang chạy.");
            }
            db.setAutoCommit(false);
            sql.execute("SET SESSION time_zone = '+07:00'");
            fixture.removeOwnedRows(sql); // Khôi phục cả lần chạy trước nếu Chrome/Maven bị tắt ngang.
            sql.executeUpdate("""
                    INSERT INTO movies(id,title,duration_minutes,age_rating,status,description) VALUES
                    (90301,'F03_TEST_Co_lich',120,'16+','NOW_SHOWING','KAN53:shared-fixture'),
                    (90302,'F03_TEST_Khong_co_lich',120,'16+','NOW_SHOWING','KAN53:shared-fixture'),
                    (90303,'F03_TEST_Chi_co_lich_khong_hop_le',120,'16+','NOW_SHOWING','KAN53:shared-fixture')
                    """);
            if (sql.executeUpdate("INSERT INTO movie_genres(movie_id,genre_id) SELECT 90301,id FROM genres WHERE name IN ('Horror','Drama')") != 2)
                throw new SQLException("Thiếu dữ liệu thể loại Horror hoặc Drama trong DB demo.");
            sql.executeUpdate("""
                    INSERT INTO showtimes(id,movie_id,room_id,start_time,price,status) VALUES
                    (90301,90301,1,TIMESTAMP(CURDATE()+INTERVAL 7 DAY,'09:00'),80000,'OPEN'),
                    (90302,90301,1,TIMESTAMP(CURDATE()+INTERVAL 7 DAY,'14:00'),80000,'OPEN'),
                    (90303,90301,1,TIMESTAMP(CURDATE()+INTERVAL 8 DAY,'09:00'),80000,'OPEN'),
                    (90304,90303,1,TIMESTAMP(CURDATE()-INTERVAL 1 DAY,'09:00'),80000,'OPEN'),
                    (90305,90303,1,TIMESTAMP(CURDATE()+INTERVAL 7 DAY,'18:00'),80000,'CLOSED'),
                    (90306,90303,1,TIMESTAMP(CURDATE()+INTERVAL 7 DAY,'21:00'),80000,'CANCELLED'),
                    (90307,90301,1,TIMESTAMP(CURDATE()+INTERVAL 9 DAY,'09:00'),80000,'OPEN')
                    """);
            db.commit();
            return fixture;
        } catch (SQLException e) {
            try { if (!db.getAutoCommit()) db.rollback(); } finally { db.close(); }
            throw e;
        }
    }

    private void removeOwnedRows(Statement sql) throws SQLException {
        // Chỉ xóa lịch của phim do fixture tạo. FK RESTRICT giữ lại lịch nếu đã có đơn.
        sql.executeUpdate("""
                DELETE s FROM showtimes s JOIN movies m ON m.id=s.movie_id
                WHERE m.description='KAN53:shared-fixture' AND m.id BETWEEN 90301 AND 90303
                AND s.id BETWEEN 90301 AND 90307
                """);
        sql.executeUpdate("""
                DELETE FROM movies WHERE id BETWEEN 90301 AND 90303 AND description='KAN53:shared-fixture'
                AND NOT EXISTS (SELECT 1 FROM showtimes s WHERE s.movie_id=movies.id)
                """);
    }

    @Override public void close() throws SQLException {
        try (Statement sql = db.createStatement()) {
            removeOwnedRows(sql);
            db.commit();
        } catch (SQLException e) {
            db.rollback();
            throw e;
        } finally {
            db.close();
        }
    }
}
