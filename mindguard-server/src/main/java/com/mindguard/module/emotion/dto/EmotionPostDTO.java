package com.mindguard.module.emotion.dto;

import lombok.Data;

@Data
public class EmotionPostDTO {
    private String content;
    private Boolean isAnonymous;
    private String moodEmoji;
}
