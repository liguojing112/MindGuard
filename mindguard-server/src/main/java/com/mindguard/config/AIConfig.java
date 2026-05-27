package com.mindguard.config;

import com.mindguard.ai.AIService;
import com.mindguard.ai.AISettingsService;
import com.mindguard.ai.ChatResult;
import com.mindguard.ai.EmotionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Slf4j
@Configuration
public class AIConfig {

    @Bean
    public AIService aiService(AISettingsService settingsService) {
        return new AIService() {
            @Override
            public EmotionResult analyzeEmotion(String content) {
                return settingsService.getCurrentService().analyzeEmotion(content);
            }

            @Override
            public Map<String, String> generateSuggestion(String studentProfile) {
                return settingsService.getCurrentService().generateSuggestion(studentProfile);
            }

            @Override
            public ChatResult chat(String userMessage) {
                return settingsService.getCurrentService().chat(userMessage);
            }
        };
    }
}
