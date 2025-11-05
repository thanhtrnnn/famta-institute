package com.famta.service.dto;

import java.util.Objects;

/**
 * Lightweight projection for the day's teaching schedule.
 */
public record ScheduleItem(
    String className,
    String teacherName,
    String timeRange,
    String room
) {
    public ScheduleItem {
        Objects.requireNonNull(className, "className");
        Objects.requireNonNull(teacherName, "teacherName");
        Objects.requireNonNull(timeRange, "timeRange");
        Objects.requireNonNull(room, "room");
    }
}
