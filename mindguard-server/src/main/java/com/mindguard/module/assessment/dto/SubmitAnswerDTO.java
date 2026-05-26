package com.mindguard.module.assessment.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubmitAnswerDTO {
    private List<AnswerItem> answers;

    @Data
    public static class AnswerItem {
        private Long questionId;
        private Long optionId;
    }
}
