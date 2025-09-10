package com.EdumentumBackend.EdumentumBackend.controller.attempt;

import com.EdumentumBackend.EdumentumBackend.dtos.attempt.AttemptReviewDto;
import com.EdumentumBackend.EdumentumBackend.dtos.attempt.SubmitAttemptRequest;
import com.EdumentumBackend.EdumentumBackend.dtos.auth.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.common.ApiResponse;
import com.EdumentumBackend.EdumentumBackend.service.AttemptService;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class AttemptController {

    private final AttemptService attemptService;
    private final UserService userService;

    @PostMapping("/quizzes/{quizId}/attempts/submit")
    public ResponseEntity<ApiResponse<AttemptReviewDto>> submitAttempt(
            @PathVariable Long quizId,
            @RequestBody SubmitAttemptRequest req,
            Authentication auth) {
        try {
            Long userId = currentUserId(auth);
            AttemptReviewDto result = attemptService.submit(quizId, userId, req);
            return ResponseEntity.ok(ApiResponse.success(result, "Quiz attempt submitted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to submit attempt: " + e.getMessage(), 400));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Internal server error: " + e.getMessage(), 500));
        }
    }

    @GetMapping("/attempts/{attemptId}/review")
    public ResponseEntity<ApiResponse<AttemptReviewDto>> getAttemptReview(
            @PathVariable Long attemptId,
            Authentication auth) {
        try {
            Long userId = currentUserId(auth);
            AttemptReviewDto result = attemptService.getReview(attemptId, userId);
            return ResponseEntity.ok(ApiResponse.success(result, "Attempt review retrieved successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Attempt not found: " + e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve attempt review: " + e.getMessage(), 500));
        }
    }

    @GetMapping("/quizzes/{quizId}/attempts/latest")
    public ResponseEntity<ApiResponse<AttemptReviewDto>> getLatestAttempt(
            @PathVariable Long quizId,
            Authentication auth) {
        try {
            Long userId = currentUserId(auth);
            AttemptReviewDto result = attemptService.getLatest(quizId, userId);
            return ResponseEntity.ok(ApiResponse.success(result, "Latest attempt retrieved successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("No attempts found: " + e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve latest attempt: " + e.getMessage(), 500));
        }
    }

    private Long currentUserId(Authentication auth) {
        String email = auth.getName();
        UserResponseDto user = userService.getUserByEmail(email);
        return user.getUserId();
    }
}
