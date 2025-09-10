package com.EdumentumBackend.EdumentumBackend.controller.student;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.EdumentumBackend.EdumentumBackend.dtos.course.*;
import com.EdumentumBackend.EdumentumBackend.enums.CourseLevel;
import com.EdumentumBackend.EdumentumBackend.enums.EnrollmentStatus;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.service.CourseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/student/courses")
@CrossOrigin(origins = "*")
public class StudentCourseController {

    private final CourseService courseService;

    public StudentCourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // Course Detail

    @GetMapping("/{courseId}")
    public ResponseEntity<Map<String, Object>> getCourseDetail(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long studentId = ((CustomUserDetails) userDetails).getUserId();
        Object response = courseService.getCourseDetailByUser(courseId, studentId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response
        ));
    }

    // Get enrolled course detail
    @GetMapping("/{courseId}/enrolled-detail")
    public ResponseEntity<Map<String, Object>> getEnrolledCourseDetail(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long studentId = ((CustomUserDetails) userDetails).getUserId();
        EnrolledStudentCourseDetailDto response =
                courseService.getEnrolledStudentCourseDetail(courseId, studentId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response
        ));
    }

    //  Browsing Courses

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCourses(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
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
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CourseResponseDto> response =
                courseService.filterCourses(level, minPrice, maxPrice, pageable);

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
            @RequestParam(defaultValue = "6") int size) {

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
            @RequestParam(defaultValue = "6") int size) {

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
            @RequestParam(defaultValue = "6") int size) {

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
            @RequestParam(defaultValue = "6") int size) {

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

    // Enrollment Management

    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<Map<String, Object>> enrollInCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long studentId = ((CustomUserDetails) userDetails).getUserId();
        EnrollmentResponseDto response = courseService.enrollInCourse(courseId, studentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "success",
                "message", "Successfully enrolled in course",
                "data", response
        ));
    }

    @DeleteMapping("/{courseId}/enroll")
    public ResponseEntity<Map<String, Object>> unenrollFromCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long studentId = ((CustomUserDetails) userDetails).getUserId();
        courseService.unenrollFromCourse(courseId, studentId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Successfully unenrolled from course"
        ));
    }

    @GetMapping("/my-enrollments")
    public ResponseEntity<Map<String, Object>> getStudentEnrollments(
            @RequestParam(required = false) EnrollmentStatus enrollmentStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long studentId = ((CustomUserDetails) userDetails).getUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<EnrollmentResponseDto> response =
                courseService.getStudentEnrollments(studentId, enrollmentStatus, pageable);

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

        EnrollmentResponseDto response =
                courseService.updateEnrollmentProgress(enrollmentId, completedLessons, completedExercises);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Progress updated successfully",
                "data", response
        ));
    }

    // Ratings Management

    @PostMapping("/{courseId}/ratings")
    public ResponseEntity<Map<String, Object>> rateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody RatingCreateRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long studentId = ((CustomUserDetails) userDetails).getUserId();
        RatingResponseDto response = courseService.rateCourse(courseId, request, studentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "status", "success",
                "message", "Course rated successfully",
                "data", response
        ));
    }

    @PatchMapping("/{courseId}/ratings")
    public ResponseEntity<Map<String, Object>> updateCourseRating(
            @PathVariable Long courseId,
            @Valid @RequestBody RatingCreateRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long studentId = ((CustomUserDetails) userDetails).getUserId();
        RatingResponseDto response = courseService.updateCourseRating(courseId, request, studentId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Rating updated successfully",
                "data", response
        ));
    }

    @DeleteMapping("/{courseId}/ratings")
    public ResponseEntity<Map<String, Object>> deleteCourseRating(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long studentId = ((CustomUserDetails) userDetails).getUserId();
        courseService.deleteCourseRating(courseId, studentId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Rating deleted successfully"
        ));
    }

    @GetMapping("/{courseId}/ratings")
    public ResponseEntity<Map<String, Object>> getCourseRatings(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

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

    @GetMapping("/{courseId}/ratings/my-rating")
    public ResponseEntity<Map<String, Object>> getUserCourseRating(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long studentId = ((CustomUserDetails) userDetails).getUserId();
        RatingResponseDto response = courseService.getUserCourseRating(courseId, studentId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response
        ));
    }
}
