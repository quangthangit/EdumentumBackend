package com.EdumentumBackend.EdumentumBackend.controller.base;

import com.EdumentumBackend.EdumentumBackend.dtos.common.ApiResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizSummaryDto;
import com.EdumentumBackend.EdumentumBackend.service.QuizzesService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public abstract class BaseQuizController {

    protected final QuizzesService quizzesService;

    protected BaseQuizController(QuizzesService quizzesService) {
        this.quizzesService = quizzesService;
    }

    /**
     * Extract user ID from the security context
     */
    protected abstract Long getCurrentUserId();

    /**
     * Create a new quiz
     */
    protected ResponseEntity<ApiResponse<QuizResponseDto>> doCreateQuiz(@Valid @RequestBody QuizRequestDto quizRequestDto) {
        try {
            Long userId = getCurrentUserId();
            QuizResponseDto createdQuiz = quizzesService.createQuiz(quizRequestDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created(createdQuiz, "Quiz created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create quiz: " + e.getMessage(), 400));
        }
    }

    /**
     * Get all quizzes for the current user
     */
    protected ResponseEntity<ApiResponse<List<QuizSummaryDto>>> doGetAllQuizzes() {
        try {
            Long userId = getCurrentUserId();
            List<QuizSummaryDto> quizzes = quizzesService.getAllQuizzes(userId);
            return ResponseEntity.ok(ApiResponse.success(quizzes, "Quizzes retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve quizzes: " + e.getMessage(), 500));
        }
    }

    /**
     * Get all quizzes for the current user with pagination
     * @param page Page number (0-based)
     * @param size Number of items per page
     * @param sortBy Field to sort by
     * @param direction Sort direction (ASC or DESC)
     * @return Paginated list of quizzes
     */
    protected ResponseEntity<ApiResponse<Page<QuizSummaryDto>>> doGetAllQuizzesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Long userId = getCurrentUserId();
            Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            Page<QuizSummaryDto> quizzes = quizzesService.getAllQuizzesPaginated(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success(quizzes, "Paginated quizzes retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve paginated quizzes: " + e.getMessage(), 500));
        }
    }

    /**
     * Search quizzes with pagination
     * @param title Search term
     * @param page Page number (0-based)
     * @param size Number of items per page
     * @param sortBy Field to sort by
     * @param direction Sort direction (ASC or DESC)
     * @return Paginated list of quizzes matching the search term
     */
    protected ResponseEntity<ApiResponse<Page<QuizSummaryDto>>> doSearchQuizzesPaginated(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Long userId = getCurrentUserId();
            Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            Page<QuizSummaryDto> quizzes = quizzesService.searchQuizzesPaginated(title, userId, pageable);
            return ResponseEntity.ok(ApiResponse.success(quizzes, "Search results retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to search quizzes: " + e.getMessage(), 500));
        }
    }

    /**
     * Get a quiz by ID
     */
    protected ResponseEntity<ApiResponse<QuizResponseDto>> doGetQuizById(Long quizId) {
        try {
            Long userId = getCurrentUserId();
            QuizResponseDto quiz = quizzesService.getQuizById(quizId, userId);
            return ResponseEntity.ok(ApiResponse.success(quiz, "Quiz retrieved successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Quiz not found: " + e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve quiz: " + e.getMessage(), 500));
        }
    }

    /**
     * Get a quiz by ID and slug
     */
    protected ResponseEntity<ApiResponse<QuizResponseDto>> doGetQuizByIdAndSlug(Long quizId, String slug, String basePath) {
        try {
            Long userId = getCurrentUserId();
            QuizResponseDto quiz = quizzesService.getQuizById(quizId, userId);
            // Verify that the slug matches to ensure proper URL
            if (!quiz.getSlug().equals(slug)) {
                return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                        .header("Location", basePath + "/" + quizId + "/" + quiz.getSlug())
                        .body(ApiResponse.error("Quiz moved to correct URL", 301));
            }
            return ResponseEntity.ok(ApiResponse.success(quiz, "Quiz retrieved successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Quiz not found: " + e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve quiz: " + e.getMessage(), 500));
        }
    }

    /**
     * Update a quiz
     */
    protected ResponseEntity<ApiResponse<QuizResponseDto>> doUpdateQuiz(Long quizId, @Valid QuizRequestDto quizRequestDto) {
        try {
            Long userId = getCurrentUserId();
            QuizResponseDto updatedQuiz = quizzesService.updateQuiz(quizId, quizRequestDto, userId);
            return ResponseEntity.ok(ApiResponse.success(updatedQuiz, "Quiz updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Quiz not found: " + e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update quiz: " + e.getMessage(), 400));
        }
    }

    /**
     * Update a quiz with slug validation
     */
    protected ResponseEntity<ApiResponse<QuizResponseDto>> doUpdateQuizWithSlug(Long quizId, String slug,
                                                                   @Valid QuizRequestDto quizRequestDto,
                                                                   String basePath) {
        try {
            Long userId = getCurrentUserId();
            // Verify quiz exists and slug matches
            QuizResponseDto existingQuiz = quizzesService.getQuizById(quizId, userId);
            if (!existingQuiz.getSlug().equals(slug)) {
                return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                        .header("Location", basePath + "/" + quizId + "/" + existingQuiz.getSlug())
                        .body(ApiResponse.error("Quiz moved to correct URL", 301));
            }

            QuizResponseDto updatedQuiz = quizzesService.updateQuiz(quizId, quizRequestDto, userId);
            return ResponseEntity.ok(ApiResponse.success(updatedQuiz, "Quiz updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Quiz not found: " + e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update quiz: " + e.getMessage(), 400));
        }
    }

    /**
     * Patch update a quiz
     */
    protected ResponseEntity<ApiResponse<QuizResponseDto>> doPatchQuiz(Long quizId, Map<String, Object> updates) {
        try {
            Long userId = getCurrentUserId();
            QuizResponseDto updatedQuiz = quizzesService.patchQuiz(quizId, userId, updates);
            return ResponseEntity.ok(ApiResponse.success(updatedQuiz, "Quiz patched successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Quiz not found: " + e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to patch quiz: " + e.getMessage(), 500));
        }
    }

    /**
     * Delete a quiz
     */
    protected ResponseEntity<ApiResponse<Void>> doDeleteQuiz(Long quizId) {
        try {
            Long userId = getCurrentUserId();
            boolean deleted = quizzesService.deleteQuiz(quizId, userId);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success(null, "Quiz deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Quiz not found", 404));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied: " + e.getMessage(), 403));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete quiz: " + e.getMessage(), 500));
        }
    }

    /**
     * Delete a quiz with slug validation
     */
    protected ResponseEntity<ApiResponse<Void>> doDeleteQuizWithSlug(Long quizId, String slug, String basePath) {
        try {
            Long userId = getCurrentUserId();
            // Verify quiz exists and slug matches
            QuizResponseDto existingQuiz = quizzesService.getQuizById(quizId, userId);
            if (!existingQuiz.getSlug().equals(slug)) {
                return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                        .header("Location", basePath + "/" + quizId + "/" + existingQuiz.getSlug())
                        .body(ApiResponse.error("Quiz moved to correct URL", 301));
            }

            boolean deleted = quizzesService.deleteQuiz(quizId, userId);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success(null, "Quiz deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Quiz not found", 404));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied: " + e.getMessage(), 403));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete quiz: " + e.getMessage(), 500));
        }
    }

    /**
     * Search quizzes by title
     */
    protected ResponseEntity<ApiResponse<List<QuizSummaryDto>>> doSearchQuizzes(String title) {
        try {
            Long userId = getCurrentUserId();
            List<QuizSummaryDto> quizzes = quizzesService.searchQuizzes(title, userId);
            return ResponseEntity.ok(ApiResponse.success(quizzes, "Search completed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to search quizzes: " + e.getMessage(), 500));
        }
    }
}