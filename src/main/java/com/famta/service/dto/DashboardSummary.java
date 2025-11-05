package com.famta.service.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Aggregated metrics and helper lists used by the dashboard view.
 */
public record DashboardSummary(
    int totalStudents,
    int activeTeachers,
    int runningClasses,
    List<String> announcements,
    List<ScheduleItem> scheduleItems,
    LocalDateTime lastUpdated
) {
    public DashboardSummary {
        Objects.requireNonNull(announcements, "announcements");
        Objects.requireNonNull(scheduleItems, "scheduleItems");
        Objects.requireNonNull(lastUpdated, "lastUpdated");
    }
}
