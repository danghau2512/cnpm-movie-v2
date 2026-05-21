package Util;

import org.jdbi.v3.core.Jdbi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JdbiConnector {
    private static Jdbi jdbi;

    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    static {
        try (InputStream input = JdbiConnector.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("KhÃ´ng tÃ¬m tháº¥y file db.properties trong src/main/resources");
            }

            Properties props = new Properties();
            props.load(input);

            URL = props.getProperty("db.url");
            USERNAME = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");

            if (URL == null || USERNAME == null || PASSWORD == null) {
                throw new RuntimeException("Thiáº¿u cáº¥u hÃ¬nh db.url, db.user hoáº·c db.password trong db.properties");
            }

        } catch (IOException e) {
            throw new RuntimeException("Lá»—i khi Ä‘á»c file db.properties", e);
        }
    }

    public static Jdbi getJdbi() {
        if (jdbi == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                jdbi = Jdbi.create(URL, USERNAME, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("KhÃ´ng tÃ¬m tháº¥y MySQL Driver", e);
            }
        }

        return jdbi;
    }
}
