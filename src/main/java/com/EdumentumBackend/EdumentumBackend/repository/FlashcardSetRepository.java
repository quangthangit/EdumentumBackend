package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.FlashcardSetEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlashcardSetRepository extends JpaRepository<FlashcardSetEntity, Long> {
    List<FlashcardSetEntity> findByIsPublicTrue();
    List<FlashcardSetEntity> findByUserOrderByCreatedAtDesc(UserEntity user);
    Optional<FlashcardSetEntity> findByIdAndUser(Long id, UserEntity user);
} 