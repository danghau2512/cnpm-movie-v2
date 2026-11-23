package com.cinebook.base;

import com.cinebook.utils.FlowEvidence;
import org.junit.jupiter.api.BeforeEach;

public abstract class TechniqueTest extends BaseTest {
    protected FlowEvidence evidence;
    private int pauseMs;

    @BeforeEach
    void prepareTechnique() {
        String feature = getClass().getSimpleName().substring(0, 3).toLowerCase(java.util.Locale.ROOT);
        baseUrl = config.get(feature + ".baseUrl", baseUrl).replaceAll("/+$", "");
        pauseMs = Integer.parseInt(config.get("technique.pauseMs", "2500"));
        if (pauseMs < 0 || pauseMs > 10000) throw new IllegalArgumentException("PauseMs phải từ 0 đến 10000");
        evidence = new FlowEvidence(driver, getClass().getSimpleName(),
                Boolean.parseBoolean(config.get("flow.screenshots", "true")));
    }

    protected void demonstrate(String tc, String description, Runnable test) {
        evidence.step(tc, description, () -> {
            try {
                test.run();
            } finally {
                // Dừng ở kết quả để quan sát, không dùng sleep thay cho chờ phần tử.
                try { Thread.sleep(pauseMs); }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Đã dừng chạy test", e);
                }
            }
        });
    }
}
