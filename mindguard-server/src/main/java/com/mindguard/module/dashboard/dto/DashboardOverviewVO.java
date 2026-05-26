package com.mindguard.module.dashboard.dto;

import lombok.Data;

@Data
public class DashboardOverviewVO {
    private Long totalStudents;
    private Long totalPosts;
    private Long totalAlerts;
    private Long pendingAlerts;
    private Long totalAppointments;
    private Long totalAssessments;
}
