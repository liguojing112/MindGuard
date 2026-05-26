package com.mindguard.module.dashboard.service;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    Map<String, Object> getOverview();
    List<Map<String, Object>> getEmotionTrend(int days);
    List<Map<String, Object>> getEmotionDistribution();
    List<Map<String, Object>> getAssessmentDistribution();
    List<Map<String, Object>> getAppointmentStats();
    List<Map<String, Object>> getCrisisTrend(int days);
}
