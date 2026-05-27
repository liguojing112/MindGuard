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
    public Map<String, String> generateSuggestion(String studentProfile) {
        return Map.of(
            "contentSummary", "学生主动前来咨询，主诉学业压力和情绪困扰。咨询过程中学生表达了近期面临的多重压力，包括课程负担、人际关系及未来规划等方面的焦虑。通过倾听和共情，初步建立了信任关系，学生能够较为开放地表达内心感受。",
            "diagnosis", "根据学生自述和情绪表现，初步评估为一般心理困扰，主要表现为学业压力引发的焦虑情绪和轻度睡眠问题。未发现自伤或自杀风险。建议进一步通过标准化量表评估焦虑和抑郁程度。",
            "suggestions", "1. 建议每周一次定期咨询，持续4-6周\n2. 教授基础放松训练和正念呼吸技巧\n3. 协助制定合理的学习计划和时间管理策略\n4. 鼓励参加校园社交活动，拓展支持网络\n5. 两周后使用SAS/SDS量表复测，评估干预效果"
        );
    }

    @Override
    public ChatResult chat(String userMessage) {
        int severeCount = countMatches(userMessage, SEVERE_NEGATIVE_WORDS);
        int mildCount = countMatches(userMessage, MILD_NEGATIVE_WORDS);
        int posCount = countMatches(userMessage, POSITIVE_WORDS);

        ChatResult result = new ChatResult();
        if (severeCount > 0) {
            int score = 15 + random.nextInt(25);
            result.setEmotionScore(score);
            result.setEmotionLabel("高危负面");
            result.setReply("我听到了你的声音，能感受到你现在的痛苦。请记住，你并不孤单，有很多人愿意帮助你。"
                    + "我强烈建议你立即联系学校心理咨询中心，或者拨打心理援助热线400-161-9995。"
                    + "你愿意和我聊聊具体发生了什么吗？我会一直在这里陪着你。");
        } else if (mildCount > posCount) {
            int score = 40 + random.nextInt(30);
            result.setEmotionScore(score);
            result.setEmotionLabel("一般负面");
            String[] replies = {
                "我理解你现在的感受，这种情绪是很正常的。能具体说说是什么事情让你有这样的感觉吗？也许我们可以一起想想应对的方法。",
                "谢谢你愿意和我分享这些。每个人都会有情绪低落的时候，这并不代表你不够坚强。你平时有没有一些能让自己放松的小习惯呢？",
                "听起来你最近压力确实不小。记住，适当表达情绪本身就是一种释放。如果你觉得需要，我可以帮你联系学校的心理辅导员聊一聊。"
            };
            result.setReply(replies[random.nextInt(replies.length)]);
        } else {
            int score = 70 + random.nextInt(26);
            result.setEmotionScore(score);
            result.setEmotionLabel("正常情绪");
            String[] replies = {
                "很高兴听到你分享这些！保持这样积极的心态真的很棒。有什么特别开心的事情想和我分享吗？",
                "你的状态听起来不错！积极的情绪是最好的免疫力。继续保持，也记得多关心身边的朋友哦。",
                "感谢你的信任和分享。生活中的小确幸总是值得珍惜的，希望你能一直保持这样阳光的心态！"
            };
            result.setReply(replies[random.nextInt(replies.length)]);
        }
        return result;
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
