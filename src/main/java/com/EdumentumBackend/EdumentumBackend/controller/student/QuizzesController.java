package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizResponseDto;
import com.EdumentumBackend.EdumentumBackend.service.QuizzesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/quizzes")
@CrossOrigin(origins = "*")
public class QuizzesController {

    @Autowired
    private QuizzesService quizzesService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.valueOf(authentication.getName());
    }

    @PostMapping
    public ResponseEntity<QuizResponseDto> createQuiz(@Valid @RequestBody QuizRequestDto quizRequestDto) {
        try {
            Long userId = getCurrentUserId();
            QuizResponseDto createdQuiz = quizzesService.createQuiz(quizRequestDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdQuiz);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<QuizResponseDto>> getAllQuizzes() {
        try {
            Long userId = getCurrentUserId();
            List<QuizResponseDto> quizzes = quizzesService.getAllQuizzes(userId);
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{quizId}/{slug}")
    public ResponseEntity<QuizResponseDto> getQuizByIdAndSlug(@PathVariable Long quizId, @PathVariable String slug) {
        try {
            Long userId = getCurrentUserId();
            QuizResponseDto quiz = quizzesService.getQuizById(quizId, userId);
            // Verify that the slug matches to ensure proper URL
            if (!quiz.getSlug().equals(slug)) {
                return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                    .header("Location", "/api/v1/user/quizzes/" + quizId + "/" + quiz.getSlug())
                    .build();
            }
            return ResponseEntity.ok(quiz);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Keep the old endpoint for backward compatibility
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponseDto> getQuizById(@PathVariable Long quizId) {
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

    @PutMapping("/{quizId}")
    public ResponseEntity<QuizResponseDto> updateQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizRequestDto quizRequestDto) {
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

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
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

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<QuizResponseDto>> getQuizzesByCategory(@PathVariable Long categoryId) {
        try {
            Long userId = getCurrentUserId();
            List<QuizResponseDto> quizzes = quizzesService.getQuizzesByCategory(categoryId, userId);
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<QuizResponseDto>> searchQuizzes(@RequestParam String title) {
        try {
            Long userId = getCurrentUserId();
            List<QuizResponseDto> quizzes = quizzesService.searchQuizzes(title, userId);
            return ResponseEntity.ok(quizzes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{quizId}/{slug}")
    public ResponseEntity<QuizResponseDto> updateQuizWithSlug(
            @PathVariable Long quizId,
            @PathVariable String slug,
            @Valid @RequestBody QuizRequestDto quizRequestDto) {
        try {
            Long userId = getCurrentUserId();
            // Verify quiz exists and slug matches
            QuizResponseDto existingQuiz = quizzesService.getQuizById(quizId, userId);
            if (!existingQuiz.getSlug().equals(slug)) {
                return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                    .header("Location", "/api/v1/user/quizzes/" + quizId + "/" + existingQuiz.getSlug())
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

    @DeleteMapping("/{quizId}/{slug}")
    public ResponseEntity<Void> deleteQuizWithSlug(@PathVariable Long quizId, @PathVariable String slug) {
        try {
            Long userId = getCurrentUserId();
            // Verify quiz exists and slug matches
            QuizResponseDto existingQuiz = quizzesService.getQuizById(quizId, userId);
            if (!existingQuiz.getSlug().equals(slug)) {
                return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                    .header("Location", "/api/v1/user/quizzes/" + quizId + "/" + existingQuiz.getSlug())
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
}
