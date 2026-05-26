package com.mindguard.module.appointment.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EvaluationDTO {
    @NotNull(message = "评分不能为空")
    @Min(1)
    @Max(5)
    private Integer rating;
    private String comment;
}
