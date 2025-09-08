package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.entity.course.CourseEntity;
import com.EdumentumBackend.EdumentumBackend.entity.course.EnrollmentEntity;
import com.EdumentumBackend.EdumentumBackend.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, Long> {
    
    // Check if student is enrolled in course
    Optional<EnrollmentEntity> findByStudentAndCourse(UserEntity student, CourseEntity course);
    
    Optional<EnrollmentEntity> findByStudent_UserIdAndCourse_CourseId(Long studentId, Long courseId);
    
    // Get student's enrollments
    Page<EnrollmentEntity> findByStudent(UserEntity student, Pageable pageable);
    
    List<EnrollmentEntity> findByStudent_UserIdAndStatus(Long studentId, EnrollmentStatus status);
    
    // Get course enrollments
    Page<EnrollmentEntity> findByCourse(CourseEntity course, Pageable pageable);
    
    List<EnrollmentEntity> findByCourse_CourseIdAndStatus(Long courseId, EnrollmentStatus status);
    
    // Count enrollments
    Long countByCourse(CourseEntity course);
    
    Long countByStudent_UserIdAndStatus(Long studentId, EnrollmentStatus status);
    
    // Check enrollment existence
    boolean existsByStudentAndCourse(UserEntity student, CourseEntity course);
    
    boolean existsByStudent_UserIdAndCourse_CourseId(Long studentId, Long courseId);
}