package com.mindguard.common.enums;

public enum SeverityLevel {
    NORMAL("正常"),
    MILD("轻度"),
    MODERATE("中度"),
    SEVERE("重度");

    private final String description;

    SeverityLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
