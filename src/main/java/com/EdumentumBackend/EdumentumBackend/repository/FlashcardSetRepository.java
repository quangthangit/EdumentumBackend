package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.FlashcardSetEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlashcardSetRepository extends JpaRepository<FlashcardSetEntity, Long> {
    Page<FlashcardSetEntity> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);
    Page<FlashcardSetEntity> findByUserOrderByCreatedAtDesc(UserEntity user, Pageable pageable);
    Optional<FlashcardSetEntity> findByIdAndUser(Long id, UserEntity user);
}