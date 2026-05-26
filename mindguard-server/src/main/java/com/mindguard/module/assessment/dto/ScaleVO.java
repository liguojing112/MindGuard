package com.mindguard.module.assessment.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ScaleVO {
    private Long id;
    private String name;
    private String description;
    private String type;
    private Integer totalQuestions;
    private Integer minScore;
    private Integer maxScore;
    private Integer isActive;
    private LocalDateTime createdAt;

    private List<QuestionVO> questions;

    @Data
    public static class QuestionVO {
        private Long id;
        private Integer questionNumber;
        private String questionText;
        private List<OptionVO> options;
    }

    @Data
    public static class OptionVO {
        private Long id;
        private String optionText;
        private Integer scoreValue;
        private Integer sortOrder;
    }
}
