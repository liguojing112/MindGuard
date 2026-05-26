package com.mindguard.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmotionResult {
    private Integer score;
    private String label;
    private List<String> keywords;
    private String analysis;
}
