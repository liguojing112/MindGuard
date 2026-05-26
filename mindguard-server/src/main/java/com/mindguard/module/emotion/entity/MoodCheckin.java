package com.mindguard.module.emotion.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("mood_checkin")
public class MoodCheckin {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String moodEmoji;
    private String note;
    private LocalDate checkinDate;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
