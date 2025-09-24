package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dto.response.QuizStatsResponseDto;
import com.EdumentumBackend.EdumentumBackend.service.student.StudentQuizStatsService;
import com.EdumentumBackend.EdumentumBackend.dtos.common.ApiResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.auth.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/student/quiz-stats")
@CrossOrigin(origins = "*")
public class StudentQuizStatsController {

    private final StudentQuizStatsService studentQuizStatsService;
    private final UserService userService;

    public StudentQuizStatsController(StudentQuizStatsService studentQuizStatsService, UserService userService) {
        this.studentQuizStatsService = studentQuizStatsService;
        this.userService = userService;
    }

    protected Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserResponseDto user = userService.getUserByEmail(email);
        return user.getUserId();
    }

    @GetMapping("/my-stats")
//    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<QuizStatsResponseDto>> getMyQuizStats() {
        try {
            log.info("Getting quiz stats for current user");

            Long currentUserId = getCurrentUserId();
            QuizStatsResponseDto stats = studentQuizStatsService.getQuizStats(currentUserId);

            return ResponseEntity.ok(ApiResponse.success( stats));
        } catch (Exception e) {
            log.error("Error getting quiz stats for current user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<QuizStatsResponseDto>> getUserQuizStats(@PathVariable Long userId) {
        try {
            log.info("Getting quiz stats for user: {}", userId);

            QuizStatsResponseDto stats = studentQuizStatsService.getQuizStats(userId);

            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            log.error("Error getting quiz stats for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }

    @GetMapping("/system-stats")
    public ResponseEntity<ApiResponse<QuizStatsResponseDto>> getSystemQuizStats() {
        try {
            log.info("Getting system quiz stats");

            QuizStatsResponseDto stats = studentQuizStatsService.getSystemStats();

            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            log.error("Error getting system quiz stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }
}
