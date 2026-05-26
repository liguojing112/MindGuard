package com.mindguard.module.emotion.controller;

import com.mindguard.common.PageResult;
import com.mindguard.common.Result;
import com.mindguard.module.emotion.dto.EmotionPostDTO;
import com.mindguard.module.emotion.dto.MoodCheckinDTO;
import com.mindguard.module.emotion.dto.PostVO;
import com.mindguard.module.emotion.entity.MoodCheckin;
import com.mindguard.module.emotion.service.EmotionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emotion")
@RequiredArgsConstructor
public class EmotionController {

    private final EmotionService emotionService;

    @PostMapping("/posts")
    public Result<PostVO> createPost(HttpServletRequest request, @RequestBody EmotionPostDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(emotionService.createPost(userId, dto));
    }

    @GetMapping("/posts")
    public Result<PageResult<PostVO>> getMyPosts(HttpServletRequest request,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(emotionService.getMyPosts(userId, page, size));
    }

    @GetMapping("/posts/public")
    public Result<PageResult<PostVO>> getPublicPosts(@RequestParam(defaultValue = "1") Integer page,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(emotionService.getPublicPosts(page, size));
    }

    @GetMapping("/posts/{id}")
    public Result<PostVO> getPostDetail(@PathVariable Long id) {
        return Result.ok(emotionService.getPostDetail(id));
    }

    @DeleteMapping("/posts/{id}")
    public Result<Void> deletePost(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        emotionService.deletePost(userId, id);
        return Result.ok();
    }

    @PostMapping("/posts/{id}/concern")
    public Result<Void> concernPost(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        emotionService.concernPost(userId, id);
        return Result.ok();
    }

    @PostMapping("/checkin")
    public Result<Void> createCheckin(HttpServletRequest request, @RequestBody MoodCheckinDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        emotionService.createCheckin(userId, dto);
        return Result.ok();
    }

    @GetMapping("/checkin")
    public Result<List<MoodCheckin>> getMyCheckins(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(emotionService.getMyCheckins(userId));
    }

    @GetMapping("/checkin/calendar")
    public Result<List<MoodCheckin>> getCheckinCalendar(HttpServletRequest request,
                                                         @RequestParam String month) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(emotionService.getCheckinCalendar(userId, month));
    }
}
