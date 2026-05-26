package com.mindguard.module.assessment.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindguard.common.BusinessException;
import com.mindguard.common.PageResult;
import com.mindguard.module.assessment.dto.AssessmentListItemVO;
import com.mindguard.module.assessment.dto.AssessmentResultVO;
import com.mindguard.module.assessment.dto.SubmitAnswerDTO;
import com.mindguard.module.assessment.entity.*;
import com.mindguard.module.assessment.mapper.*;
import com.mindguard.module.assessment.service.AssessmentService;
import com.mindguard.module.assessment.service.ArticleService;
import com.mindguard.module.user.entity.User;
import com.mindguard.module.user.mapper.UserMapper;
import com.mindguard.util.ScoreCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {

    private final UserAssessmentMapper userAssessmentMapper;
    private final AssessmentResultMapper assessmentResultMapper;
    private final ScaleMapper scaleMapper;
    private final ScaleOptionMapper optionMapper;
    private final ArticleService articleService;
    private final ScoreCalculator scoreCalculator;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Long startAssessment(Long userId, Long scaleId) {
        Scale scale = scaleMapper.selectById(scaleId);
        if (scale == null || scale.getIsActive() == 0) {
            throw new BusinessException("量表不存在或已停用");
        }

        UserAssessment assessment = new UserAssessment();
        assessment.setUserId(userId);
        assessment.setScaleId(scaleId);
        assessment.setStatus("IN_PROGRESS");
        assessment.setStartedAt(LocalDateTime.now());
        userAssessmentMapper.insert(assessment);

        return assessment.getId();
    }

    @Override
    @Transactional
    public AssessmentResultVO submitAnswers(Long assessmentId, Long userId, SubmitAnswerDTO dto) {
        UserAssessment assessment = userAssessmentMapper.selectById(assessmentId);
        if (assessment == null) {
            throw new BusinessException("测评记录不存在");
        }
        if (!assessment.getUserId().equals(userId)) {
            throw new BusinessException("无权提交他人测评");
        }
        if (!"IN_PROGRESS".equals(assessment.getStatus())) {
            throw new BusinessException("该测评已提交，不可重复提交");
        }

        Scale scale = scaleMapper.selectById(assessment.getScaleId());

        int totalScore = 0;
        for (SubmitAnswerDTO.AnswerItem item : dto.getAnswers()) {
            ScaleOption option = optionMapper.selectById(item.getOptionId());
            if (option != null) {
                totalScore += option.getScoreValue();
            }
        }

        ScoreCalculator.ScoreResult scoreResult = scoreCalculator.calculate(scale.getType(), totalScore);

        assessment.setAnswers(JSON.toJSONString(dto.getAnswers()));
        assessment.setStatus("COMPLETED");
        assessment.setCompletedAt(LocalDateTime.now());
        userAssessmentMapper.updateById(assessment);

        AssessmentResult result = new AssessmentResult();
        result.setAssessmentId(assessmentId);
        result.setUserId(userId);
        result.setScaleId(assessment.getScaleId());
        result.setTotalScore(totalScore);
        result.setSeverityLevel(scoreResult.getSeverity());
        result.setReportText(scoreResult.getReport());
        result.setRecommendations(buildRecommendations(scoreResult.getSeverity()));
        assessmentResultMapper.insert(result);

        return buildResultVO(result, scale);
    }

    @Override
    public PageResult<AssessmentListItemVO> getMyAssessments(Long userId, Integer page, Integer size) {
        Page<UserAssessment> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<UserAssessment> wrapper = new LambdaQueryWrapper<UserAssessment>()
                .eq(UserAssessment::getUserId, userId)
                .orderByDesc(UserAssessment::getStartedAt);
        Page<UserAssessment> result = userAssessmentMapper.selectPage(pageObj, wrapper);

        List<AssessmentListItemVO> voList = result.getRecords().stream().map(a -> {
            AssessmentListItemVO vo = new AssessmentListItemVO();
            vo.setId(a.getId());
            vo.setUserId(a.getUserId());
            vo.setScaleId(a.getScaleId());
            vo.setStatus(a.getStatus());
            vo.setStartedAt(a.getStartedAt());
            vo.setCompletedAt(a.getCompletedAt());
            vo.setCreatedAt(a.getCompletedAt() != null ? a.getCompletedAt() : a.getStartedAt());

            Scale scale = scaleMapper.selectById(a.getScaleId());
            if (scale != null) {
                vo.setScaleName(scale.getName());
            }

            AssessmentResult ar = assessmentResultMapper.selectOne(
                    new LambdaQueryWrapper<AssessmentResult>()
                            .eq(AssessmentResult::getAssessmentId, a.getId()));
            if (ar != null) {
                vo.setTotalScore(ar.getTotalScore());
                vo.setSeverityLevel(ar.getSeverityLevel());
            }
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public AssessmentResultVO getAssessmentResult(Long assessmentId, Long userId) {
        AssessmentResult result = assessmentResultMapper.selectOne(
                new LambdaQueryWrapper<AssessmentResult>()
                        .eq(AssessmentResult::getAssessmentId, assessmentId));

        if (result == null) {
            throw new BusinessException("测评结果不存在");
        }
        if (!result.getUserId().equals(userId)) {
            throw new BusinessException("无权查看他人测评结果");
        }

        Scale scale = scaleMapper.selectById(result.getScaleId());
        return buildResultVO(result, scale);
    }

    @Override
    public PageResult<AssessmentResultVO> listAllAssessments(Integer page, Integer size) {
        Page<AssessmentResult> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<AssessmentResult> wrapper = new LambdaQueryWrapper<AssessmentResult>()
                .orderByDesc(AssessmentResult::getCreatedAt);
        Page<AssessmentResult> result = assessmentResultMapper.selectPage(pageObj, wrapper);

        List<AssessmentResultVO> voList = result.getRecords().stream().map(r -> {
            AssessmentResultVO vo = new AssessmentResultVO();
            vo.setId(r.getId());
            vo.setUserId(r.getUserId());
            vo.setScaleId(r.getScaleId());
            vo.setTotalScore(r.getTotalScore());
            vo.setSeverityLevel(r.getSeverityLevel());
            vo.setReportText(r.getReportText());
            vo.setRecommendations(r.getRecommendations());
            vo.setCreatedAt(r.getCreatedAt());

            Scale scale = scaleMapper.selectById(r.getScaleId());
            if (scale != null) {
                vo.setScaleName(scale.getName());
                vo.setScaleType(scale.getType());
            }

            User user = userMapper.selectById(r.getUserId());
            if (user != null) {
                vo.setStudentName(user.getRealName());
            }

            if (vo.getCreatedAt() == null && r.getAssessmentId() != null) {
                UserAssessment ua = userAssessmentMapper.selectById(r.getAssessmentId());
                if (ua != null && ua.getCompletedAt() != null) {
                    vo.setCreatedAt(ua.getCompletedAt());
                }
            }
            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public List<Map<String, Object>> getAllAssessmentData() {
        List<AssessmentResult> results = assessmentResultMapper.selectList(
                new LambdaQueryWrapper<AssessmentResult>().orderByDesc(AssessmentResult::getCreatedAt));

        List<Map<String, Object>> data = new ArrayList<>();
        for (AssessmentResult r : results) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("学生姓名", getUserName(r.getUserId()));
            row.put("量表", getScaleName(r.getScaleId()));
            row.put("总分", r.getTotalScore());
            row.put("程度", mapSeverity(r.getSeverityLevel()));

            String timeStr = "";
            if (r.getCreatedAt() != null) {
                timeStr = r.getCreatedAt().toString();
            } else if (r.getAssessmentId() != null) {
                UserAssessment ua = userAssessmentMapper.selectById(r.getAssessmentId());
                if (ua != null && ua.getCompletedAt() != null) {
                    timeStr = ua.getCompletedAt().toString();
                }
            }
            row.put("时间", timeStr);
            data.add(row);
        }
        return data;
    }

    private String mapSeverity(String level) {
        if (level == null) return "";
        return switch (level) {
            case "NORMAL" -> "正常";
            case "MILD" -> "轻度";
            case "MODERATE" -> "中度";
            case "SEVERE" -> "重度";
            default -> level;
        };
    }

    private String getUserName(Long userId) {
        User user = userMapper.selectById(userId);
        return user != null ? user.getRealName() : "用户" + userId;
    }

    private String getScaleName(Long scaleId) {
        Scale scale = scaleMapper.selectById(scaleId);
        return scale != null ? scale.getName() : "量表" + scaleId;
    }

    private AssessmentResultVO buildResultVO(AssessmentResult result, Scale scale) {
        AssessmentResultVO vo = new AssessmentResultVO();
        vo.setId(result.getId());
        vo.setAssessmentId(result.getAssessmentId());
        vo.setUserId(result.getUserId());
        vo.setScaleId(result.getScaleId());
        vo.setTotalScore(result.getTotalScore());
        vo.setSeverityLevel(result.getSeverityLevel());
        vo.setReportText(result.getReportText());
        vo.setRecommendations(result.getRecommendations());
        vo.setCreatedAt(result.getCreatedAt());

        if (scale != null) {
            vo.setScaleName(scale.getName());
            vo.setScaleType(scale.getType());
        }

        String severityDesc = switch (result.getSeverityLevel()) {
            case "NORMAL" -> "正常，请继续保持良好的心理状态";
            case "MILD" -> "轻度异常，建议关注自身心理状态，适当进行自我调节";
            case "MODERATE" -> "中度异常，建议寻求专业心理咨询师的帮助";
            case "SEVERE" -> "重度异常，请务必尽快联系心理咨询师进行专业干预";
            default -> "";
        };
        vo.setSeverityDescription(severityDesc);

        List<Article> articles = articleService.getRecommendedArticles(result.getUserId());
        vo.setRecommendedArticles(articles);

        return vo;
    }

    private String buildRecommendations(String severity) {
        return switch (severity) {
            case "NORMAL" -> "保持良好的作息习惯，适当运动，定期进行心理自评";
            case "MILD" -> "学习放松技巧，增加社交活动，建议阅读相关心理健康文章";
            case "MODERATE" -> "预约心理咨询师进行专业评估，尝试正念冥想等减压方式";
            case "SEVERE" -> "立即联系心理危机干预热线，请勿独自承受压力";
            default -> "";
        };
    }
}
