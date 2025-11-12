package com.famta.service.dto;

import java.util.Objects;

/**
 * Lightweight card projection for the course catalog.
 */
public record CourseSummary(
    String code,
    String className,
    String subject,
    String department,
    String level,
    String teacher,
    String timeRange,
    String room,
    int enrolled,
    int durationMinutes,
    String status
) {

    public CourseSummary {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(className, "className");
        Objects.requireNonNull(subject, "subject");
        Objects.requireNonNull(department, "department");
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(teacher, "teacher");
        Objects.requireNonNull(timeRange, "timeRange");
        Objects.requireNonNull(room, "room");
        Objects.requireNonNull(status, "status");
    }

    public String durationLabel() {
        if (durationMinutes <= 0) {
            return "Thời lượng: 1 tiết";
        }
        int periods = Math.max(1, Math.round(durationMinutes / 45f));
        return "Thời lượng: " + periods + " tiết (~" + durationMinutes + " phút)";
    }

    public String detailSummary() {
        return "Giáo viên: " + teacher + " • " + timeRange + " • Phòng " + room;
    }
}
