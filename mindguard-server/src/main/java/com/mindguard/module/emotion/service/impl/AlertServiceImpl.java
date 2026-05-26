package com.mindguard.module.emotion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindguard.common.BusinessException;
import com.mindguard.common.PageResult;
import com.mindguard.module.emotion.dto.AlertVO;
import com.mindguard.module.emotion.entity.Alert;
import com.mindguard.module.emotion.entity.EmotionPost;
import com.mindguard.module.emotion.entity.EmotionAnalysisResult;
import com.mindguard.module.emotion.mapper.AlertMapper;
import com.mindguard.module.emotion.mapper.EmotionAnalysisResultMapper;
import com.mindguard.module.emotion.mapper.EmotionPostMapper;
import com.mindguard.module.emotion.service.AlertService;
import com.mindguard.module.appointment.entity.Counselor;
import com.mindguard.module.appointment.mapper.CounselorMapper;
import com.mindguard.module.user.entity.User;
import com.mindguard.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final AlertMapper alertMapper;
    private final EmotionPostMapper postMapper;
    private final EmotionAnalysisResultMapper analysisResultMapper;
    private final UserMapper userMapper;
    private final CounselorMapper counselorMapper;

    private static final Set<String> VALID_STATES = Set.of("PENDING", "NOTICED", "COMMUNICATED", "RESOLVED");

    @Override
    public PageResult<AlertVO> listAlerts(String status, Integer page, Integer size) {
        Page<Alert> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Alert> wrapper = new LambdaQueryWrapper<Alert>()
                .orderByDesc(Alert::getCreatedAt);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Alert::getStatus, status);
        }
        Page<Alert> result = alertMapper.selectPage(pageObj, wrapper);

        List<AlertVO> voList = result.getRecords().stream()
                .map(this::buildAlertVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public Long getPendingCount() {
        return alertMapper.selectCount(
                new LambdaQueryWrapper<Alert>().eq(Alert::getStatus, "PENDING"));
    }

    @Override
    public AlertVO getAlertDetail(Long alertId) {
        Alert alert = alertMapper.selectById(alertId);
        if (alert == null) {
            throw new BusinessException("预警记录不存在");
        }
        return buildAlertVO(alert);
    }

    @Override
    @Transactional
    public void updateStatus(Long alertId, String newStatus, String remarks, Long counselorId) {
        if (!VALID_STATES.contains(newStatus)) {
            throw new BusinessException("无效的预警状态: " + newStatus);
        }

        Alert alert = alertMapper.selectById(alertId);
        if (alert == null) {
            throw new BusinessException("预警记录不存在");
        }

        String current = alert.getStatus();
        if (current.equals(newStatus)) {
            throw new BusinessException("预警已处于 " + newStatus + " 状态");
        }

        boolean validTransition = switch (current) {
            case "PENDING" -> "NOTICED".equals(newStatus);
            case "NOTICED" -> "COMMUNICATED".equals(newStatus);
            case "COMMUNICATED" -> "RESOLVED".equals(newStatus);
            default -> false;
        };

        if (!validTransition) {
            throw new BusinessException("不允许从 " + current + " 直接变更到 " + newStatus);
        }

        alert.setStatus(newStatus);
        if (remarks != null) {
            alert.setRemarks(remarks);
        }
        if (counselorId != null) {
            alert.setAssignedCounselorId(counselorId);
        }
        alertMapper.updateById(alert);
    }

    @Override
    public List<AlertVO> getMyAlerts(Long userId) {
        List<Alert> alerts = alertMapper.selectList(
                new LambdaQueryWrapper<Alert>()
                        .eq(Alert::getUserId, userId)
                        .orderByDesc(Alert::getCreatedAt));
        return alerts.stream().map(this::buildAlertVO).collect(Collectors.toList());
    }

    private AlertVO buildAlertVO(Alert alert) {
        AlertVO vo = new AlertVO();
        vo.setId(alert.getId());
        vo.setPostId(alert.getPostId());
        vo.setUserId(alert.getUserId());
        vo.setAlertLevel(alert.getAlertLevel());
        vo.setStatus(alert.getStatus());
        vo.setAssignedCounselorId(alert.getAssignedCounselorId());
        vo.setRemarks(alert.getRemarks());
        vo.setEmotionScore(alert.getEmotionScore());
        vo.setCreatedAt(alert.getCreatedAt());
        vo.setUpdatedAt(alert.getUpdatedAt());

        EmotionPost post = postMapper.selectById(alert.getPostId());
        if (post != null) {
            vo.setPostContent(post.getContent());
            vo.setPostCreatedAt(post.getCreatedAt() != null ? post.getCreatedAt() : post.getUpdatedAt());
        }

        EmotionAnalysisResult analysisResult = analysisResultMapper.selectOne(
                new LambdaQueryWrapper<EmotionAnalysisResult>().eq(EmotionAnalysisResult::getPostId, alert.getPostId()));
        if (analysisResult != null) {
            vo.setAnalysisLabel(analysisResult.getEmotionLabel());
            vo.setAnalysisReport(analysisResult.getAnalysisText());
        }

        User student = userMapper.selectById(alert.getUserId());
        if (student != null) {
            vo.setStudentName(student.getRealName());
        }

        if (alert.getAssignedCounselorId() != null) {
            Counselor counselor = counselorMapper.selectById(alert.getAssignedCounselorId());
            if (counselor != null) {
                vo.setCounselorName(counselor.getRealName());
            }
        }

        return vo;
    }
}
