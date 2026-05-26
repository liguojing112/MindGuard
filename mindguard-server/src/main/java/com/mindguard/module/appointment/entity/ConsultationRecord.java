package com.mindguard.module.appointment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("consultation_record")
public class ConsultationRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long appointmentId;
    private Long counselorId;
    private String contentSummary;
    private String diagnosis;
    private String suggestions;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
