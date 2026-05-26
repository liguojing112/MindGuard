package com.mindguard.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataFixRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        fixNullTimestamps("alert", "created_at", "updated_at");
        fixNullTimestamps("emotion_post", "created_at", "updated_at");
        fixNullTimestamps("user_assessment", "started_at", "completed_at");
        fixNullTimestamps("appointment", "created_at", "updated_at");
    }

    private void fixNullTimestamps(String table, String createCol, String fallbackCol) {
        int updated = jdbcTemplate.update(
                "UPDATE " + table + " SET " + createCol + " = COALESCE(" + fallbackCol + ", NOW()) WHERE " + createCol + " IS NULL");
        if (updated > 0) {
            log.info("修复 {}.{} 空值: {} 条记录", table, createCol, updated);
        }
    }
}
