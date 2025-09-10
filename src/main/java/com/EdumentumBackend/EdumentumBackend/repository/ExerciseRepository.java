package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.course.CourseEntity;
import com.EdumentumBackend.EdumentumBackend.entity.course.ExerciseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<ExerciseEntity, Long> {
    
    List<ExerciseEntity> findByCourseOrderByOrderIndex(CourseEntity course);
    
    List<ExerciseEntity> findByCourse_CourseIdOrderByOrderIndex(Long courseId);
    
    List<ExerciseEntity> findByLesson_LessonIdOrderByOrderIndex(Long lessonId);
    
    @Query("SELECT COUNT(e) FROM ExerciseEntity e WHERE e.course.courseId = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);
}
