package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.controller.base.BaseQuizController;
import com.EdumentumBackend.EdumentumBackend.dtos.auth.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.common.ApiResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizListDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizSummaryDto;
import com.EdumentumBackend.EdumentumBackend.service.QuizzesService;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/student/quizzes")
@CrossOrigin(origins = "*")
public class StudentQuizzesController extends BaseQuizController {

    private static final String BASE_PATH = "/api/v1/student/quizzes";
    private final UserService userService;

    public StudentQuizzesController(QuizzesService quizzesService, UserService userService) {
        super(quizzesService);
        this.userService = userService;
    }

    @Override
    protected Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserResponseDto user = userService.getUserByEmail(email);
        return user.getUserId();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<QuizResponseDto>> createQuiz(@Valid @RequestBody QuizRequestDto quizRequestDto) {
        return doCreateQuiz(quizRequestDto);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QuizSummaryDto>>> getAllQuizzes() {
        return doGetAllQuizzes();
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<ApiResponse<QuizResponseDto>> getQuizById(@PathVariable Long quizId) {
        return doGetQuizById(quizId);
    }

    @GetMapping("/{quizId}/{slug}")
    public ResponseEntity<ApiResponse<QuizResponseDto>> getQuizByIdAndSlug(@PathVariable Long quizId, @PathVariable String slug) {
        return doGetQuizByIdAndSlug(quizId, slug, BASE_PATH);
    }

    @PutMapping("/{quizId}")
    public ResponseEntity<ApiResponse<QuizResponseDto>> updateQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizRequestDto quizRequestDto) {
        return doUpdateQuiz(quizId, quizRequestDto);
    }

    @PutMapping("/{quizId}/{slug}")
    public ResponseEntity<ApiResponse<QuizResponseDto>> updateQuizWithSlug(
            @PathVariable Long quizId,
            @PathVariable String slug,
            @Valid @RequestBody QuizRequestDto quizRequestDto) {
        return doUpdateQuizWithSlug(quizId, slug, quizRequestDto, BASE_PATH);
    }

    @PatchMapping("/{quizId}")
    public ResponseEntity<ApiResponse<QuizResponseDto>> patchQuiz(@PathVariable Long quizId, @RequestBody Map<String, Object> updates) {
        return doPatchQuiz(quizId, updates);
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz(@PathVariable Long quizId) {
        return doDeleteQuiz(quizId);
    }

    @DeleteMapping("/{quizId}/{slug}")
    public ResponseEntity<ApiResponse<Void>> deleteQuizWithSlug(@PathVariable Long quizId, @PathVariable String slug) {
        return doDeleteQuizWithSlug(quizId, slug, BASE_PATH);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<QuizSummaryDto>>> searchQuizzes(@RequestParam String title) {
        return doSearchQuizzes(title);
    }
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<QuizListDto>>> getAllQuizzes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        return doGetAllQuizzes(page, size, sortBy, direction);
    }

    @GetMapping("/search/list")
    public ResponseEntity<ApiResponse<Page<QuizListDto>>> searchQuizzes(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        return doSearchQuizzes(title, page, size, sortBy, direction);
    }
}