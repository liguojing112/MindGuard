package com.mindguard.module.emotion.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostVO {
    private Long id;
    private Long userId;
    private String content;
    private Integer isAnonymous;
    private String moodEmoji;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String username;
    private String realName;

    private Integer score;
    private String label;
    private List<String> keywords;
    private String analysis;

    private String alertStatus;
}
