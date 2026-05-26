package com.mindguard.common.enums;

public enum RoleEnum {
    STUDENT("学生"),
    COUNSELOR("辅导员"),
    ADMIN("管理员");

    private final String description;

    RoleEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
