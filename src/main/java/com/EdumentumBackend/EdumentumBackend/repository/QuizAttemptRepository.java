package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.QuizAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository extends JpaRepository<QuizAttemptEntity, Long> {
    Optional<QuizAttemptEntity> findTopByUserIdAndQuizIdOrderByCompletedAtDesc(Long userId, Long quizId);

    Integer countByUserIdAndQuizId(Long userId, Long quizId);

    Optional<QuizAttemptEntity> findByIdAndUserId(Long id, Long userId);

    @Query("select coalesce(max(a.attemptNumber),0) from QuizAttemptEntity a where a.quizId = :quizId and a.userId = :userId")
    int findMaxAttemptNumber(Long quizId, Long userId);

    // New queries for quiz attempt statistics
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
}
