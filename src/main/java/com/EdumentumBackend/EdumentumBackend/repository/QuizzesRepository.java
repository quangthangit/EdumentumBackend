package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.QuizzesEntity;
import com.EdumentumBackend.EdumentumBackend.enums.VisibilityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizzesRepository extends JpaRepository<QuizzesEntity, Long> {
    List<QuizzesEntity> findByUserId(Long userId);

    Page<QuizzesEntity> findByVisibility(VisibilityType visibility, Pageable pageable);

    @Query("SELECT q FROM QuizzesEntity q WHERE q.title LIKE %:title%")
    List<QuizzesEntity> findByTitleContaining(String title);

    @Query("SELECT DISTINCT q FROM QuizzesEntity q JOIN q.quizTags qt WHERE qt.tag.id IN :tagIds AND q.visibility = :visibility")
    List<QuizzesEntity> findByTagIdsAndVisibility(List<Long> tagIds, VisibilityType visibility);

}