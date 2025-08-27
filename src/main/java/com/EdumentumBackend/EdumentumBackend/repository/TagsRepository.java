package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.TagsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagsRepository extends JpaRepository<TagsEntity, Long> {
    Optional<TagsEntity> findByNameIgnoreCase(String name);
    List<TagsEntity> findByNameContainingIgnoreCase(String name);
    boolean existsBySlug(String slug);
    boolean existsByNameIgnoreCase(String name);
}
