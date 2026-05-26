package com.mindguard.common.enums;

public enum AppointmentStatus {
    PENDING("待审核"),
    APPROVED("已通过"),
    REJECTED("已拒绝"),
    IN_PROGRESS("咨询中"),
    COMPLETED("已完成"),
    ARCHIVED("已归档");

    private final String description;

    AppointmentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
