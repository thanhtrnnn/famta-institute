package com.famta.service.dto;

public record TimetableEntry(
    String id,
    int dayOfWeek, // 2-8
    String startPeriod,
    String endPeriod,
    String startTime,
    String endTime,
    String room,
    String className,
    String subjectName,
    String studentName
) {}
