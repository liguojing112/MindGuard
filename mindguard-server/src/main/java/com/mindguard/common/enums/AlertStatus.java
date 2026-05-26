package com.mindguard.common.enums;

public enum AlertStatus {
    PENDING("待处理"),
    NOTICED("已关注"),
    COMMUNICATED("已沟通"),
    RESOLVED("已解决");

    private final String description;

    AlertStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
