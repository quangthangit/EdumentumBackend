package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.dtos.attempt.DailyQuizDtos;
import com.EdumentumBackend.EdumentumBackend.entity.QuizAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository extends JpaRepository<QuizAttemptEntity, Long> {
    Optional<QuizAttemptEntity> findTopByUserIdAndQuizIdOrderByCompletedAtDesc(Long userId, Long quizId);

    List<QuizAttemptEntity> findByUserIdAndQuizIdOrderByCompletedAtDesc(Long userId, Long quizId);

    Integer countByUserIdAndQuizId(Long userId, Long quizId);

    Optional<QuizAttemptEntity> findByIdAndUserId(Long id, Long userId);

    @Query("select coalesce(max(a.attemptNumber),0) from QuizAttemptEntity a where a.quizId = :quizId and a.userId = :userId")
    int findMaxAttemptNumber(Long quizId, Long userId);

    @Query("""
        SELECT new map(
            a.quizId as quizId,
            max(a.completedAt) as lastAttemptAt,
            count(a) as totalAttempts,
            max(a.correctAnswers) as bestCorrectAnswers
        )
        FROM QuizAttemptEntity a
        WHERE a.userId = :userId AND a.quizId IN :quizIds
        GROUP BY a.quizId
    """)
    List<java.util.Map<String, Object>> findAttemptStatsByUserAndQuizIds(Long userId, List<Long> quizIds);

    // Statistics methods for user
    @Query("SELECT COUNT(a) FROM QuizAttemptEntity a WHERE a.userId = :userId")
    Integer countAttemptsByUserId(@Param("userId") Long userId);

    @Query("SELECT AVG(a.percentageScore) FROM QuizAttemptEntity a WHERE a.userId = :userId")
    BigDecimal getAverageScoreByUserId(@Param("userId") Long userId);

    @Query("SELECT AVG(a.totalTimeSpent) FROM QuizAttemptEntity a WHERE a.userId = :userId")
    BigDecimal getAverageDurationByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(DISTINCT a.quizId) FROM QuizAttemptEntity a WHERE a.userId = :userId")
    Integer countDistinctCompletedQuizzesByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(a.correctAnswers) FROM QuizAttemptEntity a WHERE a.userId = :userId")
    Integer sumCorrectAnswersByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(a.correctAnswers + a.wrongAnswers + a.skippedAnswers + a.partialAnswers) FROM QuizAttemptEntity a WHERE a.userId = :userId")
    Integer sumTotalQuestionsByUserId(@Param("userId") Long userId);

    // Statistics methods for system (overall)
    @Query("SELECT COUNT(a) FROM QuizAttemptEntity a")
    Integer countAllAttempts();

    @Query("SELECT AVG(a.percentageScore) FROM QuizAttemptEntity a")
    BigDecimal getOverallAverageScore();

    @Query("SELECT AVG(a.totalTimeSpent) FROM QuizAttemptEntity a")
    BigDecimal getOverallAverageDuration();

    @Query("SELECT SUM(a.correctAnswers) FROM QuizAttemptEntity a")
    Integer sumAllCorrectAnswers();

    @Query("SELECT SUM(a.correctAnswers + a.wrongAnswers + a.skippedAnswers + a.partialAnswers) FROM QuizAttemptEntity a")
    Integer sumAllTotalQuestions();

    @Query(value = """
    WITH days AS (
        SELECT generate_series(CURRENT_DATE - INTERVAL '6 days', CURRENT_DATE, INTERVAL '1 day')::date AS day
    )
    SELECT d.day AS day,
           COALESCE(COUNT(q.id), 0) AS attempts,
           COALESCE(AVG(q.percentage_score), 0) AS avgScore
    FROM days d
    LEFT JOIN quiz_attempts q
           ON DATE(q.completed_at) = d.day
          AND q.user_id = :userId
          AND q.status = 'COMPLETED'
    GROUP BY d.day
    ORDER BY d.day
    """, nativeQuery = true)
    List<DailyQuizDtos> findDailyStatsLast7Days(@Param("userId") Long userId);

}
