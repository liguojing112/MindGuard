package com.mindguard.ai;

import java.util.Map;

public interface AIService {
    EmotionResult analyzeEmotion(String content);
    Map<String, String> generateSuggestion(String studentProfile);
    ChatResult chat(String userMessage);
}
