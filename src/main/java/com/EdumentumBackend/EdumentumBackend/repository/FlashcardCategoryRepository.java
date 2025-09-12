package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.FlashcardCategoryEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlashcardCategoryRepository extends JpaRepository<FlashcardCategoryEntity, Long> {
    List<FlashcardCategoryEntity> findByIsActiveTrueOrderByNameAsc();
    List<FlashcardCategoryEntity> findByUserUserIdAndIsActiveTrueOrderByNameAsc(Long userId);
    Optional<FlashcardCategoryEntity> findByIdAndUserUserId(Long id, Long userId);
    boolean existsByNameAndUserUserId(String name, Long userId);
}
