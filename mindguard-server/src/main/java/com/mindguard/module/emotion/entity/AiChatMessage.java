package com.mindguard.module.emotion.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_chat_message")
public class AiChatMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String role;
    private String content;
    private Integer emotionScore;
    private String emotionLabel;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
