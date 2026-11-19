package com.cinebook.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** JVM properties > environment variables > ignored local file > shared defaults. */
public final class TestConfig {
    private final Properties values = new Properties();

    public TestConfig() throws IOException {
        try (InputStream in = TestConfig.class.getResourceAsStream("/selenium.properties")) {
            if (in != null) values.load(in);
        }
        Path local = Path.of(System.getProperty("cinebook.configFile", "mainflows.local.properties"));
        if (Files.exists(local)) {
            try (Reader in = Files.newBufferedReader(local, StandardCharsets.UTF_8)) { values.load(in); }
        } else if (System.getProperty("cinebook.configFile") != null) {
            throw new IOException("Configuration file not found: " + local.toAbsolutePath());
        }
    }

    public String get(String key, String fallback) {
        String value = System.getProperty("cinebook." + key);
        if (value == null) value = System.getenv("CINEBOOK_" + key.replace('.', '_').toUpperCase(java.util.Locale.ROOT));
        return value == null ? values.getProperty(key, fallback) : value;
    }

    public String required(String key) {
        String value = get(key, "");
        if (value.isBlank() || value.startsWith("YOUR_"))
            throw new IllegalStateException("Missing " + key
                    + ". Copy selenium/mainflows.local.properties.example to selenium/mainflows.local.properties and fill test accounts.");
        return value;
    }

    public record Account(String email, String password) {}

    public Account account(String role) {
        return new Account(required("flow." + role + ".email"), required("flow." + role + ".password"));
    }
}
