package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.QuestionAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionAttemptRepository extends JpaRepository<QuestionAttemptEntity, Long> {
    List<QuestionAttemptEntity> findByQuizAttemptId(Long quizAttemptId);
}