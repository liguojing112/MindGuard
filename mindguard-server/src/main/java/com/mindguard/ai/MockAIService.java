package com.mindguard.ai;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public class MockAIService implements AIService {

    private static final List<String> POSITIVE_WORDS = Arrays.asList(
            "开心", "快乐", "幸福", "满足", "期待", "兴奋", "感激", "温暖", "充满希望",
            "放松", "自信", "乐观", "平静", "美好", "阳光", "热爱", "积极", "喜悦", "满意"
    );
    private static final List<String> MILD_NEGATIVE_WORDS = Arrays.asList(
            "焦虑", "压力", "紧张", "担心", "疲惫", "烦躁", "迷茫", "孤独", "失落",
            "不安", "无聊", "困惑", "难过", "郁闷", "沮丧", "失眠", "分心", "拖延"
    );
    private static final List<String> SEVERE_NEGATIVE_WORDS = Arrays.asList(
            "绝望", "崩溃", "无助", "恐惧", "自伤", "痛苦", "窒息", "黑暗",
            "活不下去", "没有意义", "想死", "放弃", "撕裂", "空洞"
    );
    private static final List<String> POSITIVE_KEYWORDS = Arrays.asList(
            "积极心态", "情绪稳定", "生活满意", "社交活跃"
    );
    private static final List<String> MILD_NEGATIVE_KEYWORDS = Arrays.asList(
            "学业压力", "人际困扰", "未来迷茫", "情绪波动"
    );
    private static final List<String> SEVERE_NEGATIVE_KEYWORDS = Arrays.asList(
            "严重抑郁", "自杀意念", "重度焦虑", "社会退缩"
    );

    private final Random random = new Random();

    @Override
    public EmotionResult analyzeEmotion(String content) {
        if (content == null || content.trim().isEmpty()) {
            return new EmotionResult(70, "NEUTRAL",
                    Arrays.asList("无明显情绪"), "未检测到明显情绪内容，请描述您的感受。");
        }

        int severeCount = countMatches(content, SEVERE_NEGATIVE_WORDS);
        int mildNegCount = countMatches(content, MILD_NEGATIVE_WORDS);
        int posCount = countMatches(content, POSITIVE_WORDS);

        int score;
        String label;
        List<String> keywords = new ArrayList<>();
        String analysis;

        if (severeCount > 0) {
            score = 15 + random.nextInt(25);
            label = "SEVERE_NEGATIVE";
            keywords = pickRandom(SEVERE_NEGATIVE_KEYWORDS, 3);
            analysis = String.format(
                    "【高危预警】检测到%d处高危负面词汇，情绪分数为%d分（严重负面）。"
                            + "该学生可能存在较严重的心理困扰，建议辅导员及时介入关注。"
                            + "关键词：%s。请注意：情绪分析结果仅供参考，不能替代专业心理评估。",
                    severeCount, score, String.join("、", keywords));
        } else if (mildNegCount > posCount + 2) {
            score = 40 + random.nextInt(29);
            label = "MILD_NEGATIVE";
            keywords = pickRandom(MILD_NEGATIVE_KEYWORDS, 3);
            analysis = String.format(
                    "检测到%d处一般负面词汇，情绪分数为%d分（一般负面）。"
                            + "学生可能正在经历一些压力或困扰，建议适当关注。"
                            + "关键词：%s。",
                    mildNegCount, score, String.join("、", keywords));
        } else if (posCount > mildNegCount) {
            score = 70 + random.nextInt(26);
            label = "POSITIVE";
            keywords = pickRandom(POSITIVE_KEYWORDS, 2);
            analysis = String.format(
                    "检测到%d处积极词汇，情绪分数为%d分（积极）。"
                            + "学生当前情绪状态良好，请继续保持。关键词：%s。",
                    posCount, score, String.join("、", keywords));
        } else {
            score = 55 + random.nextInt(25);
            label = "NEUTRAL";
            keywords = Arrays.asList("情绪平稳", "无明显波动");
            analysis = String.format(
                    "情绪分数为%d分（中性）。学生情绪总体平稳，未检测到明显的负面或积极倾向。",
                    score);
        }

        log.info("Mock AI分析完成: score={}, label={}, keywords={}", score, label, keywords);
        return new EmotionResult(score, label, keywords, analysis);
    }

    @Override
    public String generateSuggestion(String studentProfile) {
        return "【个性化咨询建议】（Mock生成）\n\n"
                + "基于学生情绪数据和测评记录的综合分析：\n\n"
                + "1. 建立信任关系：初次咨询时注重营造安全、非评判的环境，让学生感到被理解和接纳。\n"
                + "2. 探索核心问题：通过开放式提问，引导学生表达当前最困扰的问题。\n"
                + "3. 评估风险因素：关注学生是否有自伤、自杀等风险行为，必要时启动危机干预流程。\n"
                + "4. 制定辅导计划：根据评估结果，与学生共同制定可行的短期和长期辅导目标。\n"
                + "5. 定期跟进：安排后续咨询，监测学生情绪变化和应对策略的有效性。\n\n"
                + "学生概况：" + studentProfile;
    }

    private int countMatches(String content, List<String> words) {
        int count = 0;
        for (String word : words) {
            if (content.contains(word)) count++;
        }
        return count;
    }

    private List<String> pickRandom(List<String> pool, int n) {
        List<String> shuffled = new ArrayList<>(pool);
        Collections.shuffle(shuffled, random);
        return shuffled.subList(0, Math.min(n, shuffled.size()));
    }
}
