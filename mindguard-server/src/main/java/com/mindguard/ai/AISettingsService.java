package com.mindguard.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class AISettingsService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path configPath;
    private volatile AISettings cached;

    public AISettingsService() {
        String userDir = System.getProperty("user.dir");
        Path projectRoot = Paths.get(userDir).getParent() != null
                ? Paths.get(userDir).getParent()
                : Paths.get(userDir);
        this.configPath = projectRoot.resolve("ai-settings.json");
    }

    @PostConstruct
    public void init() {
        loadFromFile();
    }

    public AISettings getSettings() {
        if (cached == null) {
            synchronized (this) {
                if (cached == null) {
                    loadFromFile();
                }
            }
        }
        return cached;
    }

    public synchronized void saveSettings(AISettings settings) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(configPath.toFile(), settings);
            this.cached = settings;
            log.info("AI配置已保存: mock={}, apiKey={}", settings.isMock(),
                    settings.getApiKey() != null && !settings.getApiKey().isEmpty() ? "***" : "(空)");
        } catch (IOException e) {
            log.error("保存AI配置失败", e);
            throw new RuntimeException("保存AI配置失败", e);
        }
    }

    private void loadFromFile() {
        File file = configPath.toFile();
        if (file.exists()) {
            try {
                this.cached = objectMapper.readValue(file, AISettings.class);
                log.info("已加载AI配置文件: {}", configPath);
            } catch (IOException e) {
                log.warn("AI配置文件格式错误，使用默认配置");
                this.cached = new AISettings();
            }
        } else {
            this.cached = new AISettings();
            try {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, this.cached);
                log.info("已创建默认AI配置文件: {}", configPath);
            } catch (IOException e) {
                log.warn("无法创建AI配置文件");
            }
        }
    }

    public AIService getCurrentService() {
        AISettings settings = getSettings();
        if (!settings.isMock() && settings.getApiKey() != null && settings.getApiKey().length() > 10) {
            return new DeepSeekAIService(settings.getApiKey(), settings.getApiUrl(), new MockAIService());
        }
        return new MockAIService();
    }
}
