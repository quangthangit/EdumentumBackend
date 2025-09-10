package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.course.CourseCreateRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.CourseResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.CourseUpdateRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.EnrollmentResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.ExerciseCreateRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.ExerciseResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.LessonCreateRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.LessonResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.RatingCreateRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.RatingResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.ResourceCreateRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.ResourceResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.TeacherCourseDetailDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.EnrolledStudentCourseDetailDto;
import com.EdumentumBackend.EdumentumBackend.dtos.course.PublicCourseDetailDto;
import com.EdumentumBackend.EdumentumBackend.enums.CourseLevel;
import com.EdumentumBackend.EdumentumBackend.enums.CourseStatus;
import com.EdumentumBackend.EdumentumBackend.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface CourseService {
    
    // Course Management
    CourseResponseDto createCourse(CourseCreateRequestDto request, Long teacherId);
    
    CourseResponseDto updateCourse(Long courseId, CourseUpdateRequestDto request, Long teacherId);
    
    // Course Detail Methods - Updated to use specific DTOs
    TeacherCourseDetailDto getTeacherCourseDetail(Long courseId, Long teacherId);
    
    EnrolledStudentCourseDetailDto getEnrolledStudentCourseDetail(Long courseId, Long studentId);
    
    PublicCourseDetailDto getPublicCourseDetail(Long courseId);
    
    // Generic method that determines which DTO to return based on user context
    Object getCourseDetailByUser(Long courseId, Long userId);
    
    void deleteCourse(Long courseId, Long teacherId);
    
    CourseResponseDto publishCourse(Long courseId, Long teacherId);
    
    CourseResponseDto archiveCourse(Long courseId, Long teacherId);
    
    // Course Queries
    Page<CourseResponseDto> getTeacherCourses(Long teacherId, CourseStatus status, Pageable pageable);
    
    Page<CourseResponseDto> getPublishedCourses(Pageable pageable);
    
    Page<CourseResponseDto> searchCourses(String keyword, Pageable pageable);
    
    Page<CourseResponseDto> filterCourses(CourseLevel level, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    Page<CourseResponseDto> getCoursesByTags(List<String> tagCourseNames, Pageable pageable);
    
    Page<CourseResponseDto> getPopularCourses(Pageable pageable);
    
    Page<CourseResponseDto> getHighlyRatedCourses(Pageable pageable);
    
    Page<CourseResponseDto> getFreeCourses(Pageable pageable);
    
    // Lesson Management
    LessonResponseDto createLesson(Long courseId, LessonCreateRequestDto request, Long teacherId);
    
    LessonResponseDto updateLesson(Long lessonId, LessonCreateRequestDto request, Long teacherId);
    
    void deleteLesson(Long lessonId, Long teacherId);
    
    List<LessonResponseDto> getCourseLessons(Long courseId, Long userId);
    
    // Exercise Management
    ExerciseResponseDto createExercise(Long courseId, ExerciseCreateRequestDto request, Long teacherId);
    
    ExerciseResponseDto updateExercise(Long exerciseId, ExerciseCreateRequestDto request, Long teacherId);
    
    void deleteExercise(Long exerciseId, Long teacherId);
    
    List<ExerciseResponseDto> getCourseExercises(Long courseId, Long userId);
    
    // Resource Management
    ResourceResponseDto createResource(Long courseId, ResourceCreateRequestDto request, Long teacherId);
    
    ResourceResponseDto updateResource(Long resourceId, ResourceCreateRequestDto request, Long teacherId);
    
    void deleteResource(Long resourceId, Long teacherId);
    
    List<ResourceResponseDto> getCourseResources(Long courseId, Long userId);
    
    // Enrollment Management
    EnrollmentResponseDto enrollInCourse(Long courseId, Long studentId);
    
    void unenrollFromCourse(Long courseId, Long studentId);
    
    Page<EnrollmentResponseDto> getStudentEnrollments(Long studentId, EnrollmentStatus status, Pageable pageable);
    
    Page<EnrollmentResponseDto> getCourseEnrollments(Long courseId, Long teacherId, Pageable pageable);
    
    EnrollmentResponseDto updateEnrollmentProgress(Long enrollmentId, Integer completedLessons, Integer completedExercises);
    
    // Rating Management
    RatingResponseDto rateCourse(Long courseId, RatingCreateRequestDto request, Long studentId);
    
    RatingResponseDto updateCourseRating(Long courseId, RatingCreateRequestDto request, Long studentId);
    
    void deleteCourseRating(Long courseId, Long studentId);
    
    Page<RatingResponseDto> getCourseRatings(Long courseId, Pageable pageable);
    
    RatingResponseDto getUserCourseRating(Long courseId, Long studentId);
}