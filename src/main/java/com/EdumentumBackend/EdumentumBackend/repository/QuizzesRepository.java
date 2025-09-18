package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizListDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizSummaryDto;
import com.EdumentumBackend.EdumentumBackend.entity.QuizzesEntity;
import com.EdumentumBackend.EdumentumBackend.enums.VisibilityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizzesRepository extends JpaRepository<QuizzesEntity, Long> {
    List<QuizzesEntity> findByUserId(Long userId);

    Page<QuizzesEntity> findByUserId(Long userId, Pageable pageable);

    Page<QuizzesEntity> findByVisibility(VisibilityType visibility, Pageable pageable);

    @Query("SELECT q FROM QuizzesEntity q WHERE q.title LIKE %:title%")
    List<QuizzesEntity> findByTitleContaining(String title);

    @Query("SELECT q FROM QuizzesEntity q WHERE q.title LIKE %:title%")
    Page<QuizzesEntity> findByTitleContaining(String title, Pageable pageable);

    @Query("SELECT DISTINCT q FROM QuizzesEntity q JOIN q.quizTags qt WHERE qt.tag.id IN :tagIds AND q.visibility = :visibility")
    List<QuizzesEntity> findByTagIdsAndVisibility(List<Long> tagIds, VisibilityType visibility);

    @Query("SELECT DISTINCT q FROM QuizzesEntity q JOIN q.quizTags qt WHERE qt.tag.id IN :tagIds AND q.visibility = :visibility")
    Page<QuizzesEntity> findByTagIdsAndVisibility(List<Long> tagIds, VisibilityType visibility, Pageable pageable);

    @EntityGraph(attributePaths = {"quizTags", "quizTags.tag", "user"})
    @Query("""
        SELECT q FROM QuizzesEntity q
        WHERE q.id = :id
    """)
    Optional<QuizzesEntity> findDetailForStudent(@Param("id") Long id);

    @EntityGraph(attributePaths = {"quizTags", "quizTags.tag", "user"})
    @Query("""
        SELECT q FROM QuizzesEntity q
        WHERE q.id = :id AND q.userId = :userId
    """)
    Optional<QuizzesEntity> findDetailByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @EntityGraph(attributePaths = {"quizTags", "quizTags.tag", "user"})
    @Query("""
        SELECT q FROM QuizzesEntity q
        WHERE q.id = :id 
        AND (q.userId = :userId OR q.visibility = com.EdumentumBackend.EdumentumBackend.enums.VisibilityType.PUBLIC)
    """)
    Optional<QuizzesEntity> findDetailByIdWithAccess(@Param("id") Long id, @Param("userId") Long userId);


    @Deprecated
    @Query("""
        SELECT q FROM QuizzesEntity q
        LEFT JOIN FETCH q.quizTags qt
        LEFT JOIN FETCH qt.tag t
        WHERE q.id = :id
    """)
    QuizzesEntity findByIdWithTags(@Param("id") Long id);

    @Query("SELECT DISTINCT q FROM QuizzesEntity q LEFT JOIN FETCH q.quizTags qt LEFT JOIN FETCH qt.tag WHERE q.userId = :userId")
    List<QuizzesEntity> findByUserIdWithTags(Long userId);

    @Query(value = "SELECT DISTINCT q FROM QuizzesEntity q WHERE q.userId = :userId",
            countQuery = "SELECT COUNT(DISTINCT q) FROM QuizzesEntity q WHERE q.userId = :userId")
    Page<QuizzesEntity> findByUserIdPageable(Long userId, Pageable pageable);

    boolean existsBySlug(String slug);

    @Query("SELECT DISTINCT q FROM QuizzesEntity q LEFT JOIN FETCH q.quizTags qt LEFT JOIN FETCH qt.tag WHERE q.slug = :slug")

    QuizzesEntity findBySlugWithTags(String slug);

    @Query("""
      SELECT new  com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizSummaryDto(
        q.id, q.title, q.slug, q.description, q.thumbnailUrl,
        q.visibility, q.difficulty, q.totalQuestions, q.totalPoints,
        q.keywords,
        q.createdAt, q.updatedAt
      )
      FROM QuizzesEntity q
      WHERE q.userId = :userId
      ORDER BY q.createdAt DESC
    """)
    List<QuizSummaryDto> findSummariesByUserId(Long userId);

    @Query("""
      SELECT new  com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizSummaryDto(
        q.id, q.title, q.slug, q.description, q.thumbnailUrl,
        q.visibility, q.difficulty, q.totalQuestions, q.totalPoints,
        q.keywords,
        q.createdAt, q.updatedAt
      )
      FROM QuizzesEntity q
      WHERE q.userId = :userId
    """)
    Page<QuizSummaryDto> findSummariesByUserId(Long userId, Pageable pageable);

    @Query("""
      SELECT new  com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizSummaryDto(
        q.id, q.title, q.slug, q.description, q.thumbnailUrl,
        q.visibility, q.difficulty, q.totalQuestions, q.totalPoints,
        q.keywords,
        q.createdAt, q.updatedAt
      )
      FROM QuizzesEntity q
      WHERE LOWER(q.title) LIKE LOWER(CONCAT('%', :title, '%'))
        AND (q.userId = :userId OR q.visibility = com.EdumentumBackend.EdumentumBackend.enums.VisibilityType.PUBLIC)
    """)
    Page<QuizSummaryDto> findSummariesByTitleAndUserOrPublic(String title, Long userId, Pageable pageable);

    // New optimized queries for QuizListDto
    @Query("""
      SELECT new com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizListDto(
        q.id, q.title, q.slug, q.description, q.difficulty, q.maxAttempts, q.keywords,
        q.createdAt, q.publishedAt, q.totalQuestions
      )
      FROM QuizzesEntity q
      WHERE q.userId = :userId
    """)
    Page<QuizListDto> findQuizListByUserId(Long userId, Pageable pageable);

    @Query("""
      SELECT new com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizListDto(
        q.id, q.title, q.slug, q.description, q.difficulty, q.maxAttempts, q.keywords,
        q.createdAt, q.publishedAt, q.totalQuestions
      )
      FROM QuizzesEntity q
      WHERE LOWER(q.title) LIKE LOWER(CONCAT('%', :title, '%'))
        AND (q.userId = :userId OR q.visibility = com.EdumentumBackend.EdumentumBackend.enums.VisibilityType.PUBLIC)
    """)
}