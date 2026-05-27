package com.mindguard.ai;

import lombok.Data;

@Data
public class ChatResult {
    private String reply;
    private Integer emotionScore;
    private String emotionLabel;
}
