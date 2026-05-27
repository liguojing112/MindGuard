package com.mindguard.ai;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
public class DeepSeekAIService implements AIService {

    private final String apiKey;
    private final String apiUrl;
    private final MockAIService fallback;
    private final HttpClient httpClient;

    public DeepSeekAIService(String apiKey, String apiUrl, MockAIService fallback) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.fallback = fallback;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public EmotionResult analyzeEmotion(String content) {
        try {
            String systemPrompt = "你是一个专业的心理学情感分析助手。请分析以下文本的情绪状态，"
                    + "并返回严格的JSON格式：{\"score\": 0-100的整数, "
                    + "\"label\": \"POSITIVE\"/\"NEUTRAL\"/\"MILD_NEGATIVE\"/\"SEVERE_NEGATIVE\", "
                    + "\"keywords\": [\"关键词1\",\"关键词2\",\"关键词3\"], "
                    + "\"analysis\": \"详细分析文本，包含情绪判断依据和简短建议\"}";

            String requestBody = String.format(
                    "{\"model\":\"deepseek-chat\",\"messages\":["
                            + "{\"role\":\"system\",\"content\":\"%s\"},"
                            + "{\"role\":\"user\",\"content\":\"请分析以下文本的情绪：%s\"}"
                            + "],\"temperature\":0.3}",
                    escapeJson(systemPrompt), escapeJson(content));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject respJson = JSON.parseObject(response.body());
                JSONArray choices = respJson.getJSONArray("choices");
                String reply = choices.getJSONObject(0).getJSONObject("message").getString("content");
                reply = reply.replaceAll("```json|```", "").trim();
                JSONObject result = JSON.parseObject(reply);

                Integer score = result.getInteger("score");
                String label = result.getString("label");
                List<String> keywords = result.getJSONArray("keywords").toList(String.class);
                String analysis = result.getString("analysis");

                log.info("DeepSeek API分析成功: score={}, label={}", score, label);
                return new EmotionResult(score, label, keywords, analysis);
            } else {
                log.warn("DeepSeek API返回非200状态码: {}, 降级为Mock", response.statusCode());
                return fallback.analyzeEmotion(content);
            }
        } catch (Exception e) {
            log.warn("DeepSeek API调用失败: {}, 降级为Mock模式", e.getMessage());
            return fallback.analyzeEmotion(content);
        }
    }

    @Override
    public Map<String, String> generateSuggestion(String studentProfile) {
        try {
            String prompt = "作为专业心理咨询督导，请根据以下学生概况，生成个性化咨询建议。"
                    + "必须返回严格JSON格式，不要包含markdown代码块标记："
                    + "{\"contentSummary\": \"本次咨询内容摘要(100-200字)\", "
                    + "\"diagnosis\": \"初步诊断结论(100-200字)\", "
                    + "\"suggestions\": \"跟进建议(100-200字)\"}\n\n"
                    + "学生概况：\n" + studentProfile;

            String requestBody = String.format(
                    "{\"model\":\"deepseek-chat\",\"messages\":["
                            + "{\"role\":\"user\",\"content\":\"%s\"}"
                            + "],\"temperature\":0.5}",
                    escapeJson(prompt));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject respJson = JSON.parseObject(response.body());
                JSONArray choices = respJson.getJSONArray("choices");
                String replyText = choices.getJSONObject(0).getJSONObject("message").getString("content");
                replyText = replyText.replaceAll("```json|```", "").trim();
                JSONObject result = JSON.parseObject(replyText);
                return Map.of(
                    "contentSummary", result.getString("contentSummary"),
                    "diagnosis", result.getString("diagnosis"),
                    "suggestions", result.getString("suggestions")
                );
            }
        } catch (Exception e) {
            log.warn("DeepSeek生成建议失败: {}, 降级为Mock", e.getMessage());
        }
        return fallback.generateSuggestion(studentProfile);
    }

    @Override
    public ChatResult chat(String userMessage) {
        try {
            String systemPrompt = "你是一个温暖的校园心理陪伴助手。请用亲切、共情的语气回复学生，"
                    + "同时评估其情绪状态。返回严格JSON："
                    + "{\"reply\": \"你的回复(200字内)\", \"score\": 0-100的整数, \"label\": \"POSITIVE/NEUTRAL/MILD_NEGATIVE/SEVERE_NEGATIVE\"}";

            String requestBody = String.format(
                    "{\"model\":\"deepseek-chat\",\"messages\":["
                            + "{\"role\":\"system\",\"content\":\"%s\"},"
                            + "{\"role\":\"user\",\"content\":\"%s\"}"
                            + "],\"temperature\":0.7}",
                    escapeJson(systemPrompt), escapeJson(userMessage));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject respJson = JSON.parseObject(response.body());
                JSONArray choices = respJson.getJSONArray("choices");
                String replyText = choices.getJSONObject(0).getJSONObject("message").getString("content");
                replyText = replyText.replaceAll("```json|```", "").trim();
                JSONObject result = JSON.parseObject(replyText);

                ChatResult chatResult = new ChatResult();
                chatResult.setReply(result.getString("reply"));
                chatResult.setEmotionScore(result.getInteger("score"));
                chatResult.setEmotionLabel(mapLabel(result.getString("label")));
                return chatResult;
            }
        } catch (Exception e) {
            log.warn("DeepSeek聊天失败: {}, 降级为Mock", e.getMessage());
        }
        return fallback.chat(userMessage);
    }

    private String mapLabel(String label) {
        log.info("mapLabel 输入: {}", label);
        if (label == null) return "正常情绪";
        String mapped = switch (label.toUpperCase()) {
            case "POSITIVE", "NEUTRAL" -> "正常情绪";
            case "MILD_NEGATIVE" -> "一般负面";
            case "SEVERE_NEGATIVE" -> "高危负面";
            default -> label;
        };
        log.info("mapLabel 输出: {}", mapped);
        return mapped;
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
