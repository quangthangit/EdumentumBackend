package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.course.TagCourseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagCourseRepository extends JpaRepository<TagCourseEntity, Long> {
    
    Optional<TagCourseEntity> findByName(String name);
    
    List<TagCourseEntity> findByNameIn(List<String> names);
    
    @Query("SELECT t FROM TagCourseEntity t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<TagCourseEntity> searchByName(@Param("keyword") String keyword);
    
    // Popular tags (used in most courses)
    @Query("SELECT t FROM TagCourseEntity t JOIN t.courses c WHERE c.status = 'PUBLISHED' GROUP BY t ORDER BY COUNT(c) DESC")
    List<TagCourseEntity> findPopularTags(Pageable pageable);
}
