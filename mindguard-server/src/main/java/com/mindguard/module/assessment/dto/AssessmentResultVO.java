package com.mindguard.module.assessment.dto;

import com.mindguard.module.assessment.entity.Article;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssessmentResultVO {
    private Long id;
    private Long assessmentId;
    private Long userId;
    private Long scaleId;
    private Integer totalScore;
    private String severityLevel;
    private String reportText;
    private String recommendations;
    private LocalDateTime createdAt;

    private String scaleName;
    private String scaleType;
    private String severityDescription;

    private List<Article> recommendedArticles;
}
