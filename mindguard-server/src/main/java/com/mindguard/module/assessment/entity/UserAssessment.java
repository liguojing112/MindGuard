package com.mindguard.module.assessment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_assessment")
public class UserAssessment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long scaleId;
    private String status;
    private String answers;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
