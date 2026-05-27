package com.mindguard.module.emotion.controller;

import com.mindguard.ai.AIService;
import com.mindguard.ai.ChatResult;
import com.mindguard.common.Result;
import com.mindguard.module.emotion.entity.AiChatMessage;
import com.mindguard.module.emotion.entity.Alert;
import com.mindguard.module.emotion.entity.Notification;
import com.mindguard.module.emotion.mapper.AiChatMessageMapper;
import com.mindguard.module.emotion.mapper.AlertMapper;
import com.mindguard.module.emotion.mapper.NotificationMapper;
import com.mindguard.module.user.entity.User;
import com.mindguard.module.user.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final AIService aiService;
    private final AiChatMessageMapper chatMessageMapper;
    private final AlertMapper alertMapper;
    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;

    @PostMapping("/chat")
    public Result<Map<String, Object>> chat(HttpServletRequest request, @RequestBody Map<String, String> body) {
        Long userId = (Long) request.getAttribute("userId");
        String message = body.get("message");
        if (message == null || message.isBlank()) {
            return Result.ok(Map.of("reply", "请输入你想说的话"));
        }

        // 保存用户消息
        AiChatMessage userMsg = new AiChatMessage();
        userMsg.setUserId(userId);
        userMsg.setRole("user");
        userMsg.setContent(message);
        chatMessageMapper.insert(userMsg);

        // AI分析
        ChatResult result = aiService.chat(message);

        // 保存AI回复
        AiChatMessage aiMsg = new AiChatMessage();
        aiMsg.setUserId(userId);
        aiMsg.setRole("assistant");
        aiMsg.setContent(result.getReply());
        aiMsg.setEmotionScore(result.getEmotionScore());
        aiMsg.setEmotionLabel(result.getEmotionLabel());
        chatMessageMapper.insert(aiMsg);

        // 情绪分数<70，创建预警并通知辅导员
        if (result.getEmotionScore() != null && result.getEmotionScore() < 70) {
            boolean isSevere = result.getEmotionScore() < 40;
            String alertLevel = isSevere ? "SEVERE" : "MILD";
            String alertTitle = isSevere ? "AI聊一聊高危预警" : "AI聊一聊情绪异常";
            String alertRemarks = isSevere
                    ? "AI聊一聊自动检测到高危情绪，建议辅导员及时关注"
                    : "AI聊一聊自动检测到负面情绪，建议辅导员关注";
            String notifyContent = isSevere
                    ? "学生 " + (getStudentName(userId)) + " 在AI聊一聊中表现出高危情绪，请及时关注"
                    : "学生 " + (getStudentName(userId)) + " 在AI聊一聊中表现出负面情绪，请注意关注";

            // 检查最近1小时内是否已生成过同级别预警，避免重复
            Long recentAlertCount = alertMapper.selectCount(
                    new LambdaQueryWrapper<Alert>()
                            .eq(Alert::getUserId, userId)
                            .eq(Alert::getAlertLevel, alertLevel)
                            .gt(Alert::getCreatedAt, java.time.LocalDateTime.now().minusHours(1)));
            if (recentAlertCount == 0) {
                Alert alert = new Alert();
                alert.setUserId(userId);
                alert.setAlertLevel(alertLevel);
                alert.setStatus("PENDING");
                alert.setEmotionScore(result.getEmotionScore());
                alert.setRemarks(alertRemarks);
                alertMapper.insert(alert);

                // 通知所有辅导员
                List<User> counselors = userMapper.selectList(
                        new LambdaQueryWrapper<User>().in(User::getRole, "COUNSELOR", "ADMIN"));
                for (User c : counselors) {
                    Notification notif = new Notification();
                    notif.setUserId(c.getId());
                    notif.setType("ALERT");
                    notif.setTitle(alertTitle);
                    notif.setContent(notifyContent);
                    notif.setRelatedId(alert.getId());
                    notificationMapper.insert(notif);
                }

                // 通知学生本人
                Notification studentNotif = new Notification();
                studentNotif.setUserId(userId);
                studentNotif.setType("APPOINTMENT_REMINDER");
                studentNotif.setTitle(isSevere ? "你的情绪需要关注" : "情绪状态提醒");
                studentNotif.setContent(isSevere
                        ? "AI检测到你的情绪状态较为低落，建议尽快联系学校心理咨询中心，或拨打心理援助热线400-161-9995。辅导员已收到通知，会尽快与你联系。"
                        : "AI检测到你近期可能存在一些压力和困扰，建议你尝试预约一次心理咨询，和专业咨询师聊一聊。");
                studentNotif.setRelatedId(alert.getId());
                notificationMapper.insert(studentNotif);
            }
        }

        return Result.ok(Map.of(
                "reply", result.getReply(),
                "emotionScore", result.getEmotionScore() != null ? result.getEmotionScore() : 0,
                "emotionLabel", result.getEmotionLabel() != null ? result.getEmotionLabel() : "正常情绪"
        ));
    }

    private String getStudentName(Long userId) {
        User student = userMapper.selectById(userId);
        return student != null ? student.getRealName() : "未知";
    }

    @GetMapping("/chat/history")
    public Result<List<AiChatMessage>> getHistory(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<AiChatMessage> messages = chatMessageMapper.selectList(
                new LambdaQueryWrapper<AiChatMessage>()
                        .eq(AiChatMessage::getUserId, userId)
                        .orderByAsc(AiChatMessage::getCreatedAt));
        return Result.ok(messages);
    }
}
