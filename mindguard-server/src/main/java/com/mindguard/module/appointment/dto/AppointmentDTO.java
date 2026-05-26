package com.mindguard.module.appointment.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentDTO {
    private Long counselorId;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String issueType;
    private Boolean isAnonymous;
}
