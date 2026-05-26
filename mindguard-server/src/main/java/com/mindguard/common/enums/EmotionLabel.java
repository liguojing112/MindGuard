package com.mindguard.common.enums;

public enum EmotionLabel {
    POSITIVE("积极"),
    NEUTRAL("中性"),
    MILD_NEGATIVE("一般负面"),
    SEVERE_NEGATIVE("高危负面");

    private final String description;

    EmotionLabel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
