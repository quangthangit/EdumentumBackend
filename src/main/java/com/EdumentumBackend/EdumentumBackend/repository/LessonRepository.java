package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.course.CourseEntity;
import com.EdumentumBackend.EdumentumBackend.entity.course.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, Long> {
    
    List<LessonEntity> findByCourseOrderByOrderIndex(CourseEntity course);
    
    List<LessonEntity> findByCourse_CourseIdOrderByOrderIndex(Long courseId);
    
    @Query("SELECT COUNT(l) FROM LessonEntity l WHERE l.course.courseId = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);
}