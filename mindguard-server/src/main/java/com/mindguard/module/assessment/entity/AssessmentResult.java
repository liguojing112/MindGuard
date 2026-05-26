package com.mindguard.module.assessment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("assessment_result")
public class AssessmentResult {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long assessmentId;
    private Long userId;
    private Long scaleId;
    private Integer totalScore;
    private String severityLevel;
    private String reportText;
    private String recommendations;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
