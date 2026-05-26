package com.mindguard.module.appointment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("appointment")
public class Appointment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private Long counselorId;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String issueType;
    private Integer isAnonymous;
    private String status;
    private String rejectReason;
    private Integer priority;
    private String roomNumber;
    private String location;
    @TableField(exist = false)
    private String counselorName;
    @TableField(exist = false)
    private Integer rating;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updatedAt;
}
