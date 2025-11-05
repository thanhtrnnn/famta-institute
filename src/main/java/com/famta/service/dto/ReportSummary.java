package com.famta.service.dto;

import java.util.List;
import java.util.Objects;

/**
 * Aggregate metrics used by the reports dashboard.
 */
public record ReportSummary(
    int totalStudents,
    int totalTeachers,
    int totalClasses,
    double averageScore,
    List<String> highlights
) {
    public ReportSummary {
        Objects.requireNonNull(highlights, "highlights");
    }

    public String formattedAverageScore() {
        return averageScore > 0 ? String.format("%.2f", averageScore) : "--";
    }
}
