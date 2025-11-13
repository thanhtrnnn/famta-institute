package com.famta.service.dto;

/**
 * Represents the persisted score for a student inside a class.
 */
public record ScoreEntry(
    String studentId,
    String fullName,
    Double regularScore,
    Double midtermScore,
    Double finalScore
) {
}
