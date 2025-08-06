package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.MindMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MindMapRepository extends JpaRepository<MindMapEntity, Long> {
    List<MindMapEntity> findByUserUserId(Long userId);

    List<MindMapEntity> findByUserUserIdOrderByCreatedAtDesc(Long userId);
}