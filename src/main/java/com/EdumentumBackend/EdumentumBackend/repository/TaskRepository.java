package com.EdumentumBackend.EdumentumBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.EdumentumBackend.EdumentumBackend.entity.TaskEntity;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findByUserUserId(Long userId);

    Optional<TaskEntity> findByIdAndUserUserId(Long id, Long userId);

    boolean existsByIdAndUserUserId(Long id, Long userId);
}