package Dao;

import Model.User;
import Util.JdbiConnector;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;

public class UserDAO {
    private final Jdbi jdbi = JdbiConnector.getJdbi();

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