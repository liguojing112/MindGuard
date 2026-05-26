package com.mindguard.module.assessment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String type;
    private String tags;
    private String severityLevel;
    private String sourceUrl;
    private String videoUrl;
    private Integer viewCount;
    private Integer isActive;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
