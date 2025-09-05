package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.course.CourseTagEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseTagRepository extends JpaRepository<CourseTagEntity, Long> {
    
    Optional<CourseTagEntity> findByName(String name);
    
    List<CourseTagEntity> findByNameIn(List<String> names);
    
    @Query("SELECT t FROM CourseTagEntity t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<CourseTagEntity> searchByName(@Param("keyword") String keyword);
    
    // Popular tags (used in most courses)
    @Query("SELECT t FROM CourseTagEntity t JOIN t.courses c WHERE c.status = 'PUBLISHED' GROUP BY t ORDER BY COUNT(c) DESC")
    List<CourseTagEntity> findPopularTags(Pageable pageable);
}
