package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.QuizTagEntity;
import com.EdumentumBackend.EdumentumBackend.entity.QuizTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizTagRepository extends JpaRepository<QuizTagEntity, QuizTagId> {
    List<QuizTagEntity> findByQuizId(Long quizId);
    void deleteByQuizId(Long quizId);
}
