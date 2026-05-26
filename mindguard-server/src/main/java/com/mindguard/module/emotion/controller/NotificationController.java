package com.mindguard.module.emotion.controller;

import com.mindguard.common.PageResult;
import com.mindguard.common.Result;
import com.mindguard.module.emotion.entity.Notification;
import com.mindguard.module.emotion.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public Result<PageResult<Notification>> getMyNotifications(HttpServletRequest request,
                                                                @RequestParam(defaultValue = "1") Integer page,
                                                                @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(notificationService.getMyNotifications(userId, page, size));
    }

    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(notificationService.getUnreadCount(userId));
    }

    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.markAsRead(id, userId);
        return Result.ok();
    }
}
