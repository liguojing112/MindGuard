package com.mindguard.ai;

import lombok.Data;

@Data
public class AISettings {
    private String apiKey = "";
    private String apiUrl = "https://api.deepseek.com/v1/chat/completions";
    private boolean mock = true;
}
