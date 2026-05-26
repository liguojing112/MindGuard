package com.mindguard.module.emotion.service;

import com.mindguard.common.PageResult;
import com.mindguard.module.emotion.dto.EmotionPostDTO;
import com.mindguard.module.emotion.dto.MoodCheckinDTO;
import com.mindguard.module.emotion.dto.PostVO;
import com.mindguard.module.emotion.entity.MoodCheckin;

import java.util.List;

public interface EmotionService {
    PostVO createPost(Long userId, EmotionPostDTO dto);
    PageResult<PostVO> getMyPosts(Long userId, Integer page, Integer size);
    PostVO getPostDetail(Long postId);
    PageResult<PostVO> getPublicPosts(Integer page, Integer size);
    void deletePost(Long userId, Long postId);
    void concernPost(Long userId, Long postId);
    void createCheckin(Long userId, MoodCheckinDTO dto);
    List<MoodCheckin> getMyCheckins(Long userId);
    List<MoodCheckin> getCheckinCalendar(Long userId, String month);
}
