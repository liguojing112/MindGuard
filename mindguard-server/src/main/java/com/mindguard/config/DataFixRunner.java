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
        addColumnIfNotExists("article", "summary", "VARCHAR(500) COMMENT '文章摘要'");
        addColumnIfNotExists("article", "video_url", "VARCHAR(500) COMMENT '视频链接'");
        fixNullTimestamps("alert", "created_at", "updated_at");
        fixNullTimestamps("emotion_post", "created_at", "updated_at");
        fixNullTimestamps("user_assessment", "started_at", "completed_at");
        fixNullTimestamps("appointment", "created_at", "updated_at");
    }

    private void addColumnIfNotExists(String table, String column, String definition) {
        try {
            jdbcTemplate.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
            log.info("添加列 {}.{}", table, column);
        } catch (Exception e) {
            // 列已存在则忽略
        }
    }

    private void fixNullTimestamps(String table, String createCol, String fallbackCol) {
        int updated = jdbcTemplate.update(
                "UPDATE " + table + " SET " + createCol + " = COALESCE(" + fallbackCol + ", NOW()) WHERE " + createCol + " IS NULL");
        if (updated > 0) {
            log.info("修复 {}.{} 空值: {} 条记录", table, createCol, updated);
        }
    }
}
