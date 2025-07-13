package com.example.analytic_service.component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

@RequiredArgsConstructor
@Slf4j
@Component
public class ClickHouseSchemaInitializer {
    private final DataSource dataSource;

    @PostConstruct
    public void init() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = StreamUtils.copyToString(
                    new ClassPathResource("db/init-deal-cancelled-events.sql").getInputStream(),
                    StandardCharsets.UTF_8
            );
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            log.error("Ошибка инициализации схемы ClickHouse", e);
        }
    }
}
