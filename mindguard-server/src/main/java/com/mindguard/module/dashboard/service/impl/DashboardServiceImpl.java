package com.mindguard.module.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mindguard.module.appointment.entity.Appointment;
import com.mindguard.module.appointment.mapper.AppointmentMapper;
import com.mindguard.module.assessment.entity.AssessmentResult;
import com.mindguard.module.assessment.mapper.AssessmentResultMapper;
import com.mindguard.module.assessment.mapper.UserAssessmentMapper;
import com.mindguard.module.dashboard.service.DashboardService;
import com.mindguard.module.emotion.entity.Alert;
import com.mindguard.module.emotion.entity.EmotionAnalysisResult;
import com.mindguard.module.emotion.entity.EmotionPost;
import com.mindguard.module.emotion.mapper.AlertMapper;
import com.mindguard.module.emotion.mapper.EmotionAnalysisResultMapper;
import com.mindguard.module.emotion.mapper.EmotionPostMapper;
import com.mindguard.module.user.entity.User;
import com.mindguard.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserMapper userMapper;
    private final EmotionPostMapper postMapper;
    private final AlertMapper alertMapper;
    private final AppointmentMapper appointmentMapper;
    private final UserAssessmentMapper userAssessmentMapper;
    private final AssessmentResultMapper assessmentResultMapper;
    private final EmotionAnalysisResultMapper analysisResultMapper;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("totalStudents", userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getRole, "STUDENT")));
        overview.put("totalPosts", postMapper.selectCount(null));
        overview.put("totalAlerts", alertMapper.selectCount(null));
        overview.put("pendingAlerts", alertMapper.selectCount(
                new LambdaQueryWrapper<Alert>().eq(Alert::getStatus, "PENDING")));
        overview.put("totalAppointments", appointmentMapper.selectCount(null));
        overview.put("totalAssessments", userAssessmentMapper.selectCount(null));

        // 本月预约数
        LocalDate now = LocalDate.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).atStartOfDay();
        overview.put("todayAppointments", appointmentMapper.selectCount(
                new LambdaQueryWrapper<Appointment>()
                        .ge(Appointment::getCreatedAt, monthStart)));

        // 活跃学生数（过去30天内有任何活动的学生）
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Set<Long> activeStudentIds = new HashSet<>();
        List<EmotionPost> recentPosts = postMapper.selectList(
                new LambdaQueryWrapper<EmotionPost>()
                        .ge(EmotionPost::getCreatedAt, thirtyDaysAgo));
        activeStudentIds.addAll(recentPosts.stream().map(EmotionPost::getUserId).collect(Collectors.toSet()));
        List<Appointment> recentAppointments = appointmentMapper.selectList(
                new LambdaQueryWrapper<Appointment>()
                        .ge(Appointment::getCreatedAt, thirtyDaysAgo));
        activeStudentIds.addAll(recentAppointments.stream().map(Appointment::getStudentId).collect(Collectors.toSet()));
        overview.put("activeStudents", activeStudentIds.size());

        return overview;
    }

    @Override
    public List<Map<String, Object>> getEmotionTrend(int days) {
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        LocalDateTime start = startDate.atStartOfDay();

        List<EmotionPost> posts = postMapper.selectList(
                new LambdaQueryWrapper<EmotionPost>()
                        .ge(EmotionPost::getCreatedAt, start)
                        .orderByAsc(EmotionPost::getCreatedAt));

        Map<LocalDate, List<EmotionPost>> grouped = posts.stream()
                .collect(Collectors.groupingBy(p -> p.getCreatedAt().toLocalDate()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", date.toString());
            List<EmotionPost> dayPosts = grouped.getOrDefault(date, Collections.emptyList());
            item.put("count", dayPosts.size());
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getEmotionDistribution() {
        List<EmotionAnalysisResult> results = analysisResultMapper.selectList(null);
        Map<String, Long> distribution = results.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getEmotionLabel() != null ? r.getEmotionLabel() : "NEUTRAL",
                        Collectors.counting()));

        String[] labels = {"POSITIVE", "NEUTRAL", "MILDLY_NEGATIVE", "SEVERELY_NEGATIVE"};
        String[] names = {"积极", "中性", "一般负面", "高危负面"};
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("label", labels[i]);
            item.put("name", names[i]);
            item.put("value", distribution.getOrDefault(labels[i], 0L));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getAssessmentDistribution() {
        List<AssessmentResult> results = assessmentResultMapper.selectList(null);
        Map<String, Long> distribution = results.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getSeverityLevel() != null ? r.getSeverityLevel() : "UNKNOWN",
                        Collectors.counting()));

        String[] levels = {"NORMAL", "MILD", "MODERATE", "SEVERE"};
        String[] labels = {"正常", "轻度", "中度", "重度"};
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < levels.length; i++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("level", levels[i]);
            item.put("name", labels[i]);
            item.put("value", distribution.getOrDefault(levels[i], 0L));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getAppointmentStats() {
        List<Appointment> appointments = appointmentMapper.selectList(null);
        Map<String, Long> stats = appointments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getStatus() != null ? a.getStatus() : "UNKNOWN",
                        Collectors.counting()));

        String[] statuses = {"PENDING", "APPROVED", "IN_PROGRESS", "COMPLETED", "ARCHIVED", "REJECTED"};
        String[] labels = {"待审核", "已通过", "咨询中", "已完成", "已归档", "已拒绝"};
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < statuses.length; i++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("status", statuses[i]);
            item.put("name", labels[i]);
            item.put("value", stats.getOrDefault(statuses[i], 0L));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getCrisisTrend(int days) {
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        LocalDateTime start = startDate.atStartOfDay();

        List<Alert> alerts = alertMapper.selectList(
                new LambdaQueryWrapper<Alert>()
                        .ge(Alert::getCreatedAt, start)
                        .orderByAsc(Alert::getCreatedAt));

        Map<LocalDate, List<Alert>> grouped = alerts.stream()
                .collect(Collectors.groupingBy(a -> a.getCreatedAt().toLocalDate()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", date.toString());
            List<Alert> dayAlerts = grouped.getOrDefault(date, Collections.emptyList());
            item.put("count", dayAlerts.size());
            result.add(item);
        }
        return result;
    }
}
