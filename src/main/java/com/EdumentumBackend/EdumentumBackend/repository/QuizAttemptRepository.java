package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.QuizAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface QuizAttemptRepository extends JpaRepository<QuizAttemptEntity, Long> {
    Optional<QuizAttemptEntity> findTopByUserIdAndQuizIdOrderByCompletedAtDesc(Long userId, Long quizId);

    Integer countByUserIdAndQuizId(Long userId, Long quizId);

    Optional<QuizAttemptEntity> findByIdAndUserId(Long id, Long userId);

    @Query("select coalesce(max(a.attemptNumber),0) from QuizAttemptEntity a where a.quizId = :quizId and a.userId = :userId")
    int findMaxAttemptNumber(Long quizId, Long userId);
}
