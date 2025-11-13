package com.famta.service.dto;

import java.util.Objects;

/**
 * Lightweight descriptor for a class (lớp học) that supports score entry.
 */
public record ScoreClassOption(
    String classId,
    String className,
    String subjectId,
    String subjectName
) {

    public ScoreClassOption {
        Objects.requireNonNull(classId, "classId");
        Objects.requireNonNull(className, "className");
    }

    @Override
    public String toString() {
        return subjectName == null || subjectName.isBlank()
            ? className
            : className + " • " + subjectName;
    }
}
