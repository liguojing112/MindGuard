package com.mindguard.util;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public class ScoreCalculator {

    @Data
    public static class ScoreResult {
        private int standardScore;
        private String severity;
        private String report;
    }

    public ScoreResult calculate(String scaleType, int rawScore) {
        return switch (scaleType) {
            case "SAS" -> calculateSAS(rawScore);
            case "SDS" -> calculateSDS(rawScore);
            default -> calculateGeneric(rawScore);
        };
    }

    public ScoreResult calculateSAS(int rawScore) {
        int standardScore = (int) (rawScore * 1.25);
        String severity;
        if (standardScore < 50) {
            severity = "NORMAL";
        } else if (standardScore < 60) {
            severity = "MILD";
        } else if (standardScore < 70) {
            severity = "MODERATE";
        } else {
            severity = "SEVERE";
        }
        String report = generateReport("SAS焦虑自评量表", standardScore, severity);
        ScoreResult result = new ScoreResult();
        result.setStandardScore(standardScore);
        result.setSeverity(severity);
        result.setReport(report);
        return result;
    }

    public ScoreResult calculateSDS(int rawScore) {
        int standardScore = (int) (rawScore * 1.25);
        String severity;
        if (standardScore < 53) {
            severity = "NORMAL";
        } else if (standardScore < 63) {
            severity = "MILD";
        } else if (standardScore < 73) {
            severity = "MODERATE";
        } else {
            severity = "SEVERE";
        }
        String report = generateReport("SDS抑郁自评量表", standardScore, severity);
        ScoreResult result = new ScoreResult();
        result.setStandardScore(standardScore);
        result.setSeverity(severity);
        result.setReport(report);
        return result;
    }

    public ScoreResult calculateGeneric(int rawScore) {
        ScoreResult result = new ScoreResult();
        result.setStandardScore(rawScore);
        result.setSeverity("NORMAL");
        result.setReport("测评完成，原始得分: " + rawScore);
        return result;
    }

    public String generateReport(String scaleName, int score, String severity) {
        String severityText = switch (severity) {
            case "NORMAL" -> "正常范围";
            case "MILD" -> "轻度异常";
            case "MODERATE" -> "中度异常";
            case "SEVERE" -> "重度异常";
            default -> "未判定";
        };

        String suggestion = switch (severity) {
            case "NORMAL" -> "您的心理状态良好，请继续保持健康的生活方式。";
            case "MILD" -> "建议您关注自身心理状态，适当进行自我调节，可以阅读相关心理健康资料。";
            case "MODERATE" -> "建议您寻求专业心理咨询师的帮助，进行更深入的评估和干预。";
            case "SEVERE" -> "请务必尽快联系心理咨询师或心理危机干预热线，不要独自承受。";
            default -> "";
        };

        return String.format("""
                【%s测评报告】
                标准分: %d 分
                程度判定: %s

                %s""", scaleName, score, severityText, suggestion);
    }
}
