package com.mindguard.module.emotion.service;

import com.mindguard.common.PageResult;
import com.mindguard.module.emotion.entity.Notification;

public interface NotificationService {
    PageResult<Notification> getMyNotifications(Long userId, Integer page, Integer size);
    Long getUnreadCount(Long userId);
    void markAsRead(Long notificationId, Long userId);
    void createNotification(Long userId, String type, String title, String content, Long relatedId);
}
