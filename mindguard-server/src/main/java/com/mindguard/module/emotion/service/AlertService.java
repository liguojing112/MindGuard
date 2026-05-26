package com.mindguard.module.emotion.service;

import com.mindguard.common.PageResult;
import com.mindguard.module.emotion.dto.AlertVO;

import java.util.List;

public interface AlertService {
    PageResult<AlertVO> listAlerts(String status, Integer page, Integer size);
    Long getPendingCount();
    AlertVO getAlertDetail(Long alertId);
    void updateStatus(Long alertId, String newStatus, String remarks, Long counselorId);
    List<AlertVO> getMyAlerts(Long userId);
}
