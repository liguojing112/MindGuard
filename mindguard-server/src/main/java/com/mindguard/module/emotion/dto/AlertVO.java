package com.mindguard.module.emotion.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertVO {
    private Long id;
    private Long postId;
    private Long userId;
    private String alertLevel;
    private String status;
    private Long assignedCounselorId;
    private String remarks;
    private Integer emotionScore;
    private LocalDateTime createdAt;
    private LocalDateTime postCreatedAt;
    private LocalDateTime updatedAt;

    private String postContent;
    private String studentName;
    private String counselorName;

    private String analysisLabel;
    private String analysisReport;
}
