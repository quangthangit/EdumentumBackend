package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.QuizCategoriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizCategoriesRepository extends JpaRepository<QuizCategoriesEntity, Long> {
    List<QuizCategoriesEntity> findByIsActiveTrue();

    @Query("SELECT qc FROM QuizCategoriesEntity qc WHERE qc.name LIKE %:name%")
    List<QuizCategoriesEntity> findByNameContaining(String name);
}