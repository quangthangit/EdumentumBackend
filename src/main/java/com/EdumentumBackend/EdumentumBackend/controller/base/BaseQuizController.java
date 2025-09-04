package com.EdumentumBackend.EdumentumBackend.controller.base;

import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizResponseDto;
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
    protected ResponseEntity<QuizResponseDto> doCreateQuiz(@Valid @RequestBody QuizRequestDto quizRequestDto) {
        try {
            Long userId = getCurrentUserId();
            QuizResponseDto createdQuiz = quizzesService.createQuiz(quizRequestDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdQuiz);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get all quizzes for the current user
     */
    protected ResponseEntity<List<QuizResponseDto>> doGetAllQuizzes() {
        try {
            Long userId = getCurrentUserId();
            List<QuizResponseDto> quizzes = quizzesService.getAllQuizzes(userId);
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
    protected ResponseEntity<Page<QuizResponseDto>> doGetAllQuizzesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Long userId = getCurrentUserId();
            Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            Page<QuizResponseDto> quizzes = quizzesService.getAllQuizzesPaginated(userId, pageable);
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
    protected ResponseEntity<Page<QuizResponseDto>> doSearchQuizzesPaginated(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        try {
            Long userId = getCurrentUserId();
            Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            Page<QuizResponseDto> quizzes = quizzesService.searchQuizzesPaginated(title, userId, pageable);
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a quiz by ID
     */
    protected ResponseEntity<QuizResponseDto> doGetQuizById(Long quizId) {
        try {
            Long userId = getCurrentUserId();
            QuizResponseDto quiz = quizzesService.getQuizById(quizId, userId);
            return ResponseEntity.ok(quiz);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a quiz by ID and slug
     */
    protected ResponseEntity<QuizResponseDto> doGetQuizByIdAndSlug(Long quizId, String slug, String basePath) {
        try {
            Long userId = getCurrentUserId();
            QuizResponseDto quiz = quizzesService.getQuizById(quizId, userId);
            // Verify that the slug matches to ensure proper URL
            if (!quiz.getSlug().equals(slug)) {
                return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                    .header("Location", basePath + "/" + quizId + "/" + quiz.getSlug())
                    .build();
            }
            return ResponseEntity.ok(quiz);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update a quiz
     */
    protected ResponseEntity<QuizResponseDto> doUpdateQuiz(Long quizId, @Valid QuizRequestDto quizRequestDto) {
        try {
            Long userId = getCurrentUserId();
            QuizResponseDto updatedQuiz = quizzesService.updateQuiz(quizId, quizRequestDto, userId);
            return ResponseEntity.ok(updatedQuiz);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Update a quiz with slug validation
     */
    protected ResponseEntity<QuizResponseDto> doUpdateQuizWithSlug(Long quizId, String slug,
                                                                 @Valid QuizRequestDto quizRequestDto,
                                                                 String basePath) {
        try {
            Long userId = getCurrentUserId();
            // Verify quiz exists and slug matches
            QuizResponseDto existingQuiz = quizzesService.getQuizById(quizId, userId);
            if (!existingQuiz.getSlug().equals(slug)) {
                return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                    .header("Location", basePath + "/" + quizId + "/" + existingQuiz.getSlug())
                    .build();
            }

            QuizResponseDto updatedQuiz = quizzesService.updateQuiz(quizId, quizRequestDto, userId);
            return ResponseEntity.ok(updatedQuiz);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Patch update a quiz
     */
    protected ResponseEntity<QuizResponseDto> doPatchQuiz(Long quizId, Map<String, Object> updates) {
        try {
            Long userId = getCurrentUserId();
            QuizResponseDto updatedQuiz = quizzesService.patchQuiz(quizId, userId, updates);
            return ResponseEntity.ok(updatedQuiz);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a quiz
     */
    protected ResponseEntity<Void> doDeleteQuiz(Long quizId) {
        try {
            Long userId = getCurrentUserId();
            boolean deleted = quizzesService.deleteQuiz(quizId, userId);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a quiz with slug validation
     */
    protected ResponseEntity<Void> doDeleteQuizWithSlug(Long quizId, String slug, String basePath) {
        try {
            Long userId = getCurrentUserId();
            // Verify quiz exists and slug matches
            QuizResponseDto existingQuiz = quizzesService.getQuizById(quizId, userId);
            if (!existingQuiz.getSlug().equals(slug)) {
                return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                    .header("Location", basePath + "/" + quizId + "/" + existingQuiz.getSlug())
                    .build();
            }

            boolean deleted = quizzesService.deleteQuiz(quizId, userId);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Search quizzes by title
     */
    protected ResponseEntity<List<QuizResponseDto>> doSearchQuizzes(String title) {
        try {
            Long userId = getCurrentUserId();
            List<QuizResponseDto> quizzes = quizzesService.searchQuizzes(title, userId);
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
