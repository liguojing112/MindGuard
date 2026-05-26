package com.mindguard.module.emotion.controller;

import com.mindguard.common.PageResult;
import com.mindguard.common.Result;
import com.mindguard.module.emotion.dto.AlertVO;
import com.mindguard.module.emotion.service.AlertService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public Result<PageResult<AlertVO>> listAlerts(@RequestParam(required = false) String status,
                                                   @RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(alertService.listAlerts(status, page, size));
    }

    @GetMapping("/pending-count")
    public Result<Long> getPendingCount() {
        return Result.ok(alertService.getPendingCount());
    }

    @GetMapping("/{id}")
    public Result<AlertVO> getAlertDetail(@PathVariable Long id) {
        return Result.ok(alertService.getAlertDetail(id));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body,
                                      HttpServletRequest request) {
        String newStatus = body.get("status");
        String remarks = body.get("remarks");
        Long counselorId = (Long) request.getAttribute("userId");
        alertService.updateStatus(id, newStatus, remarks, counselorId);
        return Result.ok();
    }

    @GetMapping("/my")
    public Result<List<AlertVO>> getMyAlerts(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(alertService.getMyAlerts(userId));
    }
}
