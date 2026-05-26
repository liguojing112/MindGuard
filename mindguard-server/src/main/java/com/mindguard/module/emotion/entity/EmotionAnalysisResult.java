package com.mindguard.module.emotion.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("emotion_analysis_result")
public class EmotionAnalysisResult {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long postId;
    private Integer emotionScore;
    private String emotionLabel;
    private String keywords;
    private String analysisText;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
