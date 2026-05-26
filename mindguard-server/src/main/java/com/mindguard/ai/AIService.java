package com.mindguard.ai;

public interface AIService {
    EmotionResult analyzeEmotion(String content);
    String generateSuggestion(String studentProfile);
}
