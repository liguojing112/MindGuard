package com.mindguard.config;

import com.mindguard.ai.AIService;
import com.mindguard.ai.DeepSeekAIService;
import com.mindguard.ai.MockAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
public class AIConfig {

    @Value("${deepseek.mock:true}")
    private boolean mock;

    @Value("${deepseek.api.key:}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    @Bean
    public AIService aiService() {
        if (!mock && StringUtils.hasText(apiKey) && apiKey.length() > 10) {
            log.info("AI服务: 使用 DeepSeek API 模式");
            return new DeepSeekAIService(apiKey, apiUrl, new MockAIService());
        }
        log.info("AI服务: 使用 Mock 模拟模式 (deepseek.mock=true 或 API Key 未配置)");
        return new MockAIService();
    }
}
