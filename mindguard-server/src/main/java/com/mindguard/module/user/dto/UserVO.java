package com.mindguard.module.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String role;
    private String avatar;
    private Integer status;
    private String token;
    private LocalDateTime createdAt;
}
