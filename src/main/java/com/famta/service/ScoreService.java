package com.famta.service;

import com.famta.service.dto.ScoreClassOption;
import com.famta.service.dto.ScoreEntry;
import java.util.List;

/**
 * Service contract for querying and updating class scoreboards.
 */
public interface ScoreService {

    /**
     * Loads the list of classes that accept score input.
     *
     * @return list of class options ordered by display name
     */
    List<ScoreClassOption> findClassOptions();

    /**
     * Loads the list of classes for a specific semester.
     *
     * @param semesterId the semester ID to filter by
     * @return list of class options ordered by display name
     */
    List<ScoreClassOption> findClassOptions(String semesterId);

    List<ScoreClassOption> findClassOptionsForTeacher(String semesterId, String teacherId);
    List<ScoreClassOption> findClassOptionsForStudent(String semesterId, String studentId);
    List<ScoreClassOption> findClassOptionsForGuardian(String semesterId, String guardianId);

    /**
     * Fetches existing score entries for a class.
     *
     * @param classId the class identifier
     * @return list of score rows, possibly empty when the class has no students yet
     */
    List<ScoreEntry> findScoresByClass(String classId);

    List<ScoreEntry> findScoresByClassForGuardian(String classId, String guardianId);
    
    List<ScoreEntry> findScoresByClassForStudent(String classId, String studentId);

    /**
     * Persists the scores for a student within a class. Implementations
     * should either update an existing row or insert a new one when the pair
     * does not exist.
     *
     * @param classId   the targeted class identifier
     * @param studentId the student identifier
     * @param regularScore the regular score (0-10)
     * @param midtermScore the midterm score (0-10)
     * @param finalScore the final score (0-10)
     */
    void updateScore(String classId, String studentId, Double regularScore, Double midtermScore, Double finalScore);
}
