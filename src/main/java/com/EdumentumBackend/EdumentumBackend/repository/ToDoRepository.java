package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.ToDoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToDoRepository extends JpaRepository<ToDoEntity, Long> {
    List<ToDoEntity> findByUserUserIdOrderByCreationAtDesc(Long userId);
    Optional<ToDoEntity> findByIdAndUserUserId(Long id, Long userId);
}
