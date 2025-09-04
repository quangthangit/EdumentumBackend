package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.QuizTagEntity;
import com.EdumentumBackend.EdumentumBackend.entity.QuizTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface QuizTagRepository extends JpaRepository<QuizTagEntity, QuizTagId> {
    List<QuizTagEntity> findByQuizId(Long quizId);
    void deleteByQuizId(Long quizId);

    @Modifying
    @Query("DELETE FROM QuizTagEntity qt WHERE qt.quiz.id = :quizId AND qt.tag.id IN :tagIds")
    void deleteByQuizIdAndTagIdIn(Long quizId, Set<Long> tagIds);
}
