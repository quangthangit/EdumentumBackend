package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.entity.course.CourseEntity;
import com.EdumentumBackend.EdumentumBackend.entity.course.RatingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Long> {
    
    // Find user's rating for a course
    Optional<RatingEntity> findByStudentAndCourse(UserEntity student, CourseEntity course);
    
    Optional<RatingEntity> findByStudent_UserIdAndCourse_CourseId(Long studentId, Long courseId);
    
    // Get all ratings for a course
    Page<RatingEntity> findByCourse(CourseEntity course, Pageable pageable);
    
    List<RatingEntity> findByCourse_CourseIdOrderByCreatedAtDesc(Long courseId);
    
    // Calculate average rating
    @Query("SELECT AVG(r.rating) FROM RatingEntity r WHERE r.course.courseId = :courseId")
    Optional<Double> findAverageRatingByCourseId(@Param("courseId") Long courseId);
    
    // Count ratings by rating value
    @Query("SELECT r.rating, COUNT(r) FROM RatingEntity r WHERE r.course.courseId = :courseId GROUP BY r.rating")
    List<Object[]> countRatingsByCourseIdGroupByRating(@Param("courseId") Long courseId);
    
    // Check if student can rate (must be enrolled)
    @Query("SELECT COUNT(r) > 0 FROM RatingEntity r JOIN EnrollmentEntity e ON r.student = e.student " +
           "WHERE r.student.userId = :studentId AND r.course.courseId = :courseId AND e.course.courseId = :courseId")
    boolean canStudentRateCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}