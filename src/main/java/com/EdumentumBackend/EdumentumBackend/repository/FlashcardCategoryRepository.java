package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.FlashcardCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashcardCategoryRepository extends JpaRepository<FlashcardCategoryEntity, Long> {
    List<FlashcardCategoryEntity> findByIsActiveTrueOrderByNameAsc();
}
