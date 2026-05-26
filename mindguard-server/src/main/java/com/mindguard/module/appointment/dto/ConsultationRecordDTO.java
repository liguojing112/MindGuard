package com.mindguard.module.appointment.dto;

import lombok.Data;

@Data
public class ConsultationRecordDTO {
    private String contentSummary;
    private String diagnosis;
    private String suggestions;
}
