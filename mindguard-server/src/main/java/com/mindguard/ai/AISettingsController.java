package com.mindguard.ai;

import com.mindguard.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class AISettingsController {

    private final AISettingsService settingsService;

    @GetMapping("/ai")
    public Result<AISettings> getAISettings() {
        AISettings settings = settingsService.getSettings();
        AISettings safe = new AISettings();
        safe.setMock(settings.isMock());
        safe.setApiUrl(settings.getApiUrl());
        safe.setApiKey(maskKey(settings.getApiKey()));
        return Result.ok(safe);
    }

    @PutMapping("/ai")
    public Result<Void> saveAISettings(@RequestBody AISettings input) {
        AISettings current = settingsService.getSettings();
        if (input.getApiKey() != null && !input.getApiKey().equals(maskKey(current.getApiKey()))) {
            current.setApiKey(input.getApiKey());
        }
        if (input.getApiUrl() != null) {
            current.setApiUrl(input.getApiUrl());
        }
        current.setMock(input.isMock());
        settingsService.saveSettings(current);
        return Result.ok();
    }

    private String maskKey(String key) {
        if (key == null || key.length() <= 8) return key != null ? key : "";
        return key.substring(0, 4) + "****" + key.substring(key.length() - 4);
    }
}
