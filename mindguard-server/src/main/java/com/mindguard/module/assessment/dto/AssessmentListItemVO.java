package com.mindguard.module.assessment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssessmentListItemVO {
    private Long id;
    private Long userId;
    private Long scaleId;
    private String scaleName;
    private String status;
    private Integer totalScore;
    private String severityLevel;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
}
