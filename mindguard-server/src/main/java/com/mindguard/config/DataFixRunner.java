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
        ensureTable("ai_chat_message",
                "CREATE TABLE ai_chat_message (" +
                " id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                " user_id BIGINT NOT NULL COMMENT '用户ID'," +
                " role VARCHAR(20) NOT NULL COMMENT 'user/assistant'," +
                " content TEXT NOT NULL COMMENT '消息内容'," +
                " emotion_score INT COMMENT 'AI分析分数'," +
                " emotion_label VARCHAR(30) COMMENT '情绪标签'," +
                " created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                " INDEX idx_chat_user (user_id, created_at)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI聊天消息'");
        addColumnIfNotExists("article", "summary", "VARCHAR(500) COMMENT '文章摘要'");
        addColumnIfNotExists("article", "video_url", "VARCHAR(500) COMMENT '视频链接'");
        makeColumnNullable("alert", "post_id", "BIGINT");
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

    private void ensureTable(String name, String ddl) {
        try {
            jdbcTemplate.execute("SELECT 1 FROM " + name + " LIMIT 1");
        } catch (Exception e) {
            jdbcTemplate.execute(ddl);
            log.info("创建表: {}", name);
        }
    }

    private void fixNullTimestamps(String table, String createCol, String fallbackCol) {
        int updated = jdbcTemplate.update(
                "UPDATE " + table + " SET " + createCol + " = COALESCE(" + fallbackCol + ", NOW()) WHERE " + createCol + " IS NULL");
        if (updated > 0) {
            log.info("修复 {}.{} 空值: {} 条记录", table, createCol, updated);
        }
    }

    private void makeColumnNullable(String table, String column, String type) {
        try {
            jdbcTemplate.execute("ALTER TABLE " + table + " MODIFY COLUMN " + column + " " + type + " DEFAULT NULL");
            log.info("修改列 {}.{} 为可空", table, column);
        } catch (Exception e) {
            // 已经是可空则忽略
        }
    }
}
