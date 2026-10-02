package Dao;

import Model.User;
import Util.JdbiConnector;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;

public class UserDAO {
    private final Jdbi jdbi;
    public UserDAO() { this(JdbiConnector.getJdbi()); }
    public UserDAO(Jdbi jdbi) { this.jdbi = jdbi; }

    public int create(User user) {
        return jdbi.withHandle(h -> h.createUpdate("""
                INSERT INTO users (full_name, email, phone, password_hash, role, status)
                VALUES (:fullName, :email, :phone, :passwordHash, 'CUSTOMER', 'ACTIVE')
                """).bindBean(user).executeAndReturnGeneratedKeys("id").mapTo(Integer.class).one());
    }

    public void upgradePassword(int id, String oldHash, String newHash) {
        jdbi.useHandle(h -> h.createUpdate("UPDATE users SET password_hash=:newHash WHERE id=:id AND password_hash=:oldHash")
                .bind("id", id).bind("oldHash", oldHash).bind("newHash", newHash).execute());
    }

    public User findByEmail(String email) {
        String sql = """
                SELECT 
                    id,
                    full_name AS fullName,
                    email,
                    phone,
                    password_hash AS passwordHash,
                    role,
                    status
                FROM users
                WHERE email = :email
                """;

        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .bind("email", email)
                        .registerRowMapper(BeanMapper.factory(User.class))
                        .mapTo(User.class)
                        .findOne()
                        .orElse(null)
        );
    }
}
