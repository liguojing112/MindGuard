package com.mindguard.module.dashboard.controller;

import com.mindguard.common.Result;
import com.mindguard.module.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        return Result.ok(dashboardService.getOverview());
    }

    @GetMapping("/emotion-trend")
    public Result<List<Map<String, Object>>> getEmotionTrend(@RequestParam(defaultValue = "7") int days) {
        return Result.ok(dashboardService.getEmotionTrend(days));
    }

    @GetMapping("/emotion-distribution")
    public Result<List<Map<String, Object>>> getEmotionDistribution() {
        return Result.ok(dashboardService.getEmotionDistribution());
    }

    @GetMapping("/assessment-distribution")
    public Result<List<Map<String, Object>>> getAssessmentDistribution() {
        return Result.ok(dashboardService.getAssessmentDistribution());
    }

    @GetMapping("/appointment-stats")
    public Result<List<Map<String, Object>>> getAppointmentStats() {
        return Result.ok(dashboardService.getAppointmentStats());
    }

    @GetMapping("/crisis-trend")
    public Result<List<Map<String, Object>>> getCrisisTrend(@RequestParam(defaultValue = "7") int days) {
        return Result.ok(dashboardService.getCrisisTrend(days));
    }
}
