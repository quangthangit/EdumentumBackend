package com.EdumentumBackend.EdumentumBackend.controller.teacher;

import com.EdumentumBackend.EdumentumBackend.dtos.course.*;
import com.EdumentumBackend.EdumentumBackend.enums.CourseLevel;
import com.EdumentumBackend.EdumentumBackend.enums.CourseStatus;
import com.EdumentumBackend.EdumentumBackend.service.CourseService;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/teacher/courses")
@CrossOrigin(origins = "*")
public class TeacherCourseController {

    private final CourseService courseService;

    public TeacherCourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // Course Management Endpoints

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCourse(
            @Valid @RequestBody CourseCreateRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        CourseResponseDto response = courseService.createCourse(request, teacherId);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "success",
                "message", "Course created successfully",
                "data", response
        ));
    }

    @PatchMapping("/{courseId}")
    public ResponseEntity<Map<String, Object>> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseUpdateRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        CourseResponseDto response = courseService.updateCourse(courseId, request, teacherId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Course updated successfully",
                "data", response
        ));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Map<String, Object>> getCourseDetail(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        TeacherCourseDetailDto response = courseService.getTeacherCourseDetail(courseId, teacherId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response
        ));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Map<String, Object>> deleteCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        courseService.deleteCourse(courseId, teacherId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Course deleted successfully"
        ));
    }

    @PatchMapping("/{courseId}/publish")
    public ResponseEntity<Map<String, Object>> publishCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        CourseResponseDto response = courseService.publishCourse(courseId, teacherId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Course published successfully",
                "data", response
        ));
    }

    @PatchMapping("/{courseId}/archive")
    public ResponseEntity<Map<String, Object>> archiveCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        CourseResponseDto response = courseService.archiveCourse(courseId, teacherId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Course archived successfully",
                "data", response
        ));
    }

    // Course Query Endpoints

    @GetMapping("/my-courses")
    public ResponseEntity<Map<String, Object>> getTeacherCourses(
            @RequestParam(defaultValue = "PUBLISHED") CourseStatus courseStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        Page<CourseResponseDto> response = courseService.getTeacherCourses(teacherId, courseStatus, pageable);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response.getContent(),
                "pagination", Map.of(
                        "currentPage", response.getNumber(),
                        "totalPages", response.getTotalPages(),
                        "totalElements", response.getTotalElements()
                )
        ));
    }

    @GetMapping("/published")
    public ResponseEntity<Map<String, Object>> getPublishedCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CourseResponseDto> response = courseService.getPublishedCourses(pageable);
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response.getContent(),
                "pagination", Map.of(
                        "currentPage", response.getNumber(),
                        "totalPages", response.getTotalPages(),
                        "totalElements", response.getTotalElements(),
                        "hasNext", response.hasNext(),
                        "hasPrevious", response.hasPrevious()
                )
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCourses(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CourseResponseDto> response = courseService.searchCourses(keyword, pageable);
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response.getContent(),
                "pagination", Map.of(
                        "currentPage", response.getNumber(),
                        "totalPages", response.getTotalPages(),
                        "totalElements", response.getTotalElements(),
                        "hasNext", response.hasNext(),
                        "hasPrevious", response.hasPrevious()
                )
        ));
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterCourses(
            @RequestParam(required = false) CourseLevel level,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CourseResponseDto> response = courseService.filterCourses(level, minPrice, maxPrice, pageable);
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response.getContent(),
                "pagination", Map.of(
                        "currentPage", response.getNumber(),
                        "totalPages", response.getTotalPages(),
                        "totalElements", response.getTotalElements(),
                        "hasNext", response.hasNext(),
                        "hasPrevious", response.hasPrevious()
                )
        ));
    }

    @GetMapping("/tags")
    public ResponseEntity<Map<String, Object>> getCoursesByTags(
            @RequestParam List<String> tagCourseNames,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CourseResponseDto> response = courseService.getCoursesByTags(tagCourseNames, pageable);
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response.getContent(),
                "pagination", Map.of(
                        "currentPage", response.getNumber(),
                        "totalPages", response.getTotalPages(),
                        "totalElements", response.getTotalElements()
                )
        ));
    }

    @GetMapping("/popular")
    public ResponseEntity<Map<String, Object>> getPopularCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseResponseDto> response = courseService.getPopularCourses(pageable);
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response.getContent(),
                "pagination", Map.of(
                        "currentPage", response.getNumber(),
                        "totalPages", response.getTotalPages(),
                        "totalElements", response.getTotalElements()
                )
        ));
    }

    @GetMapping("/highly-rated")
    public ResponseEntity<Map<String, Object>> getHighlyRatedCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseResponseDto> response = courseService.getHighlyRatedCourses(pageable);
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response.getContent(),
                "pagination", Map.of(
                        "currentPage", response.getNumber(),
                        "totalPages", response.getTotalPages(),
                        "totalElements", response.getTotalElements()
                )
        ));
    }

    @GetMapping("/free")
    public ResponseEntity<Map<String, Object>> getFreeCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CourseResponseDto> response = courseService.getFreeCourses(pageable);
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response.getContent(),
                "pagination", Map.of(
                        "currentPage", response.getNumber(),
                        "totalPages", response.getTotalPages(),
                        "totalElements", response.getTotalElements()
                )
        ));
    }

    // Lesson Management Endpoints

   @PostMapping("/{courseId}/lessons")
    public ResponseEntity<Map<String, Object>> createLesson(
            @PathVariable Long courseId,
            @Valid @RequestBody LessonCreateRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        LessonResponseDto response = courseService.createLesson(courseId, request, teacherId);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "success",
                "message", "Lesson created successfully",
                "data", response
        ));
    }

    @PatchMapping("/lessons/{lessonId}")
    public ResponseEntity<Map<String, Object>> updateLesson(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonCreateRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        LessonResponseDto response = courseService.updateLesson(lessonId, request, teacherId);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Lesson updated successfully",
                "data", response
        ));
    }

    @DeleteMapping("/lessons/{lessonId}")
    public ResponseEntity<Map<String, Object>> deleteLesson(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        courseService.deleteLesson(lessonId, teacherId);
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Lesson deleted successfully"
        ));
    }

    @GetMapping("/{courseId}/lessons")
    public ResponseEntity<Map<String, Object>> getCourseLessons(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        List<LessonResponseDto> response = courseService.getCourseLessons(courseId, teacherId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response
        ));
    }

    // Exercise Management Endpoints

    @PostMapping("/{courseId}/exercises")
    public ResponseEntity<Map<String, Object>> createExercise(
            @PathVariable Long courseId,
            @Valid @RequestBody ExerciseCreateRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        ExerciseResponseDto response = courseService.createExercise(courseId, request, teacherId);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "success",
                "message", "Exercise created successfully",
                "data", response
        ));
    }

    @PatchMapping("/exercises/{exerciseId}")
    public ResponseEntity<Map<String, Object>> updateExercise(
            @PathVariable Long exerciseId,
            @Valid @RequestBody ExerciseCreateRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        ExerciseResponseDto response = courseService.updateExercise(exerciseId, request, teacherId);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Exercise updated successfully",
                "data", response
        ));
    }

    @DeleteMapping("/exercises/{exerciseId}")
    public ResponseEntity<Map<String, Object>> deleteExercise(
            @PathVariable Long exerciseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        courseService.deleteExercise(exerciseId, teacherId);
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Exercise deleted successfully"
        ));
    }

    @GetMapping("/{courseId}/exercises")
    public ResponseEntity<Map<String, Object>> getCourseExercises(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = ((CustomUserDetails) userDetails).getUserId();
        List<ExerciseResponseDto> response = courseService.getCourseExercises(courseId, userId);
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response
        ));
    }

    // Resource Management Endpoints

    @PostMapping("/{courseId}/resources")
    public ResponseEntity<Map<String, Object>> createResource(
            @PathVariable Long courseId,
            @Valid @RequestBody ResourceCreateRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        ResourceResponseDto response = courseService.createResource(courseId, request, teacherId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "success",
                "message", "Resource created successfully",
                "data", response
        ));
    }

    @PatchMapping("/resources/{resourceId}")
    public ResponseEntity<Map<String, Object>> updateResource(
            @PathVariable Long resourceId,
            @Valid @RequestBody ResourceCreateRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        ResourceResponseDto response = courseService.updateResource(resourceId, request, teacherId);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Resource updated successfully",
                "data", response
        ));
    }

    @DeleteMapping("/resources/{resourceId}")
    public ResponseEntity<Map<String, Object>> deleteResource(
            @PathVariable Long resourceId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        courseService.deleteResource(resourceId, teacherId);
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Resource deleted successfully"
        ));
    }

    @GetMapping("/{courseId}/resources")
    public ResponseEntity<Map<String, Object>> getCourseResources(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = ((CustomUserDetails) userDetails).getUserId();
        List<ResourceResponseDto> response = courseService.getCourseResources(courseId, userId);
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response
        ));
    }

    // Enrollment Management Endpoints

    @GetMapping("/{courseId}/enrollments")
    public ResponseEntity<Map<String, Object>> getCourseEnrollments(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long teacherId = ((CustomUserDetails) userDetails).getUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        Page<EnrollmentResponseDto> response = courseService.getCourseEnrollments(courseId, teacherId, pageable);
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response.getContent(),
                "pagination", Map.of(
                        "currentPage", response.getNumber(),
                        "totalPages", response.getTotalPages(),
                        "totalElements", response.getTotalElements()
                )
        ));
    }

    @PatchMapping("/enrollments/{enrollmentId}/progress")
    public ResponseEntity<Map<String, Object>> updateEnrollmentProgress(
            @PathVariable Long enrollmentId,
            @RequestParam(required = false) Integer completedLessons,
            @RequestParam(required = false) Integer completedExercises) {
        
        EnrollmentResponseDto response = courseService.updateEnrollmentProgress(
                enrollmentId, completedLessons, completedExercises);
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Progress updated successfully",
                "data", response
        ));
    }

    // Rating Management Endpoints

     @GetMapping("/{courseId}/ratings")
    public ResponseEntity<Map<String, Object>> getCourseRatings(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<RatingResponseDto> response = courseService.getCourseRatings(courseId, pageable);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response.getContent(),
                "pagination", Map.of(
                        "currentPage", response.getNumber(),
                        "totalPages", response.getTotalPages(),
                        "totalElements", response.getTotalElements()
                )
        ));
    }
}