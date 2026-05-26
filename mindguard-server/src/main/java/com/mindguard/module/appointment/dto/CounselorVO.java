package com.mindguard.module.appointment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CounselorVO {
    private Long id;
    private Long userId;
    private String realName;
    private String title;
    private String specialty;
    private String description;
    private String avatar;
    private Integer isAvailable;
    private LocalDateTime createdAt;

    private String username;
    private String email;
}
