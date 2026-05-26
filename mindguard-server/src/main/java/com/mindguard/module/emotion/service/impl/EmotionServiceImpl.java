package com.mindguard.module.emotion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindguard.ai.AIService;
import com.mindguard.ai.EmotionResult;
import com.mindguard.common.BusinessException;
import com.mindguard.common.PageResult;
import com.mindguard.module.emotion.dto.EmotionPostDTO;
import com.mindguard.module.emotion.dto.MoodCheckinDTO;
import com.mindguard.module.emotion.dto.PostVO;
import com.mindguard.module.emotion.entity.*;
import com.mindguard.module.emotion.mapper.*;
import com.mindguard.module.emotion.service.EmotionService;
import com.mindguard.module.emotion.service.NotificationService;
import com.mindguard.module.user.entity.User;
import com.mindguard.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmotionServiceImpl implements EmotionService {

    private final EmotionPostMapper postMapper;
    private final EmotionAnalysisResultMapper analysisResultMapper;
    private final AlertMapper alertMapper;
    private final UserMapper userMapper;
    private final AIService aiService;
    private final NotificationService notificationService;

    @Value("${mindguard.ai.crisis-threshold:40}")
    private int crisisThreshold;

    @Override
    @Transactional
    public PostVO createPost(Long userId, EmotionPostDTO dto) {
        EmotionPost post = new EmotionPost();
        post.setUserId(userId);
        post.setContent(dto.getContent());
        post.setIsAnonymous(dto.getIsAnonymous() != null && dto.getIsAnonymous() ? 1 : 0);
        post.setMoodEmoji(dto.getMoodEmoji());
        post.setStatus(1);
        postMapper.insert(post);

        EmotionResult aiResult = aiService.analyzeEmotion(dto.getContent());

        EmotionAnalysisResult analysis = new EmotionAnalysisResult();
        analysis.setPostId(post.getId());
        analysis.setEmotionScore(aiResult.getScore());
        analysis.setEmotionLabel(aiResult.getLabel());
        analysis.setKeywords(String.join(",", aiResult.getKeywords()));
        analysis.setAnalysisText(aiResult.getAnalysis());
        analysisResultMapper.insert(analysis);

        if (aiResult.getScore() < crisisThreshold) {
            Alert alert = new Alert();
            alert.setPostId(post.getId());
            alert.setUserId(userId);
            alert.setAlertLevel(aiResult.getScore() < 20 ? "SEVERE" : "MILD");
            alert.setStatus("PENDING");
            alert.setEmotionScore(aiResult.getScore());
            alertMapper.insert(alert);

            List<User> counselors = userMapper.selectList(
                    new LambdaQueryWrapper<User>().eq(User::getRole, "COUNSELOR"));
            for (User counselor : counselors) {
                notificationService.createNotification(counselor.getId(), "ALERT",
                        "高危情绪预警",
                        "学生" + userId + "发布了高危情绪内容，情感评分" + aiResult.getScore(),
                        alert.getId());
            }
        }

        return buildPostVO(post, analysis, null);
    }

    @Override
    public PageResult<PostVO> getMyPosts(Long userId, Integer page, Integer size) {
        Page<EmotionPost> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<EmotionPost> wrapper = new LambdaQueryWrapper<EmotionPost>()
                .eq(EmotionPost::getUserId, userId)
                .orderByDesc(EmotionPost::getCreatedAt);
        Page<EmotionPost> result = postMapper.selectPage(pageObj, wrapper);

        List<PostVO> voList = result.getRecords().stream()
                .map(p -> buildPostVOSimple(p))
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    public PostVO getPostDetail(Long postId) {
        EmotionPost post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        EmotionAnalysisResult analysis = analysisResultMapper.selectOne(
                new LambdaQueryWrapper<EmotionAnalysisResult>()
                        .eq(EmotionAnalysisResult::getPostId, postId));
        Alert alert = alertMapper.selectOne(
                new LambdaQueryWrapper<Alert>().eq(Alert::getPostId, postId));
        return buildPostVO(post, analysis, alert);
    }

    @Override
    public PageResult<PostVO> getPublicPosts(Integer page, Integer size) {
        Page<EmotionPost> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<EmotionPost> wrapper = new LambdaQueryWrapper<EmotionPost>()
                .eq(EmotionPost::getIsAnonymous, 0)
                .eq(EmotionPost::getStatus, 1)
                .orderByDesc(EmotionPost::getCreatedAt);
        Page<EmotionPost> result = postMapper.selectPage(pageObj, wrapper);

        List<PostVO> voList = result.getRecords().stream()
                .map(p -> buildPostVOSimple(p))
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional
    public void deletePost(Long userId, Long postId) {
        EmotionPost post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("无权删除他人帖子");
        }
        postMapper.deleteById(postId);
    }

    @Override
    public void createCheckin(Long userId, MoodCheckinDTO dto) {
        MoodCheckin checkin = new MoodCheckin();
        checkin.setUserId(userId);
        checkin.setMoodEmoji(dto.getMoodEmoji());
        checkin.setNote(dto.getNote());
        checkin.setCheckinDate(LocalDate.now());
        checkin.setCreatedAt(LocalDateTime.now());
        moodCheckinMapper.insert(checkin);
    }

    @Override
    public List<MoodCheckin> getMyCheckins(Long userId) {
        return moodCheckinMapper.selectList(
                new LambdaQueryWrapper<MoodCheckin>()
                        .eq(MoodCheckin::getUserId, userId)
                        .orderByDesc(MoodCheckin::getCreatedAt));
    }

    @Override
    public List<MoodCheckin> getCheckinCalendar(Long userId, String month) {
        YearMonth ym = YearMonth.parse(month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        return moodCheckinMapper.selectList(
                new LambdaQueryWrapper<MoodCheckin>()
                        .eq(MoodCheckin::getUserId, userId)
                        .ge(MoodCheckin::getCheckinDate, start)
                        .le(MoodCheckin::getCheckinDate, end)
                        .orderByAsc(MoodCheckin::getCheckinDate));
    }

    private PostVO buildPostVO(EmotionPost post, EmotionAnalysisResult analysis, Alert alert) {
        PostVO vo = new PostVO();
        BeanUtils.copyProperties(post, vo);

        User user = userMapper.selectById(post.getUserId());
        if (user != null) {
            if (post.getIsAnonymous() == 1) {
                vo.setUsername("匿名用户");
            } else {
                vo.setUsername(user.getRealName());
                vo.setRealName(user.getRealName());
            }
        }

        if (analysis != null) {
            vo.setScore(analysis.getEmotionScore());
            vo.setLabel(analysis.getEmotionLabel());
            if (analysis.getKeywords() != null && !analysis.getKeywords().isEmpty()) {
                vo.setKeywords(List.of(analysis.getKeywords().split(",")));
            }
            vo.setAnalysis(analysis.getAnalysisText());
        }

        if (alert != null) {
            vo.setAlertStatus(alert.getStatus());
        }

        return vo;
    }

    private PostVO buildPostVOSimple(EmotionPost post) {
        EmotionAnalysisResult analysis = analysisResultMapper.selectOne(
                new LambdaQueryWrapper<EmotionAnalysisResult>()
                        .eq(EmotionAnalysisResult::getPostId, post.getId()));
        Alert alert = alertMapper.selectOne(
                new LambdaQueryWrapper<Alert>().eq(Alert::getPostId, post.getId()));
        return buildPostVO(post, analysis, alert);
    }

    private final MoodCheckinMapper moodCheckinMapper;
}
