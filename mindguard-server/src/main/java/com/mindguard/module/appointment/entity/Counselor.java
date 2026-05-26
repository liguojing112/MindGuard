package com.mindguard.module.appointment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("counselor")
public class Counselor {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String realName;
    private String title;
    private String specialty;
    private String description;
    private String avatar;
    private Integer isAvailable;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
