package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.controller.base.BaseQuizController;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizResponseDto;
import com.EdumentumBackend.EdumentumBackend.service.QuizzesService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/quizzes")
@CrossOrigin(origins = "*")
public class QuizzesController extends BaseQuizController {

    private static final String BASE_PATH = "/api/v1/user/quizzes";

    public QuizzesController(QuizzesService quizzesService) {
        super(quizzesService);
    }

    @Override
    protected Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.valueOf(authentication.getName());
    }

    @PostMapping
    public ResponseEntity<QuizResponseDto> createQuiz(@Valid @RequestBody QuizRequestDto quizRequestDto) {
        return doCreateQuiz(quizRequestDto);
    }

    @GetMapping
    public ResponseEntity<List<QuizResponseDto>> getAllQuizzes() {
        return doGetAllQuizzes();
    }

    @GetMapping("/{quizId}/{slug}")
    public ResponseEntity<QuizResponseDto> getQuizByIdAndSlug(@PathVariable Long quizId, @PathVariable String slug) {
        return doGetQuizByIdAndSlug(quizId, slug, BASE_PATH);
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponseDto> getQuizById(@PathVariable Long quizId) {
        return doGetQuizById(quizId);
    }

    @PutMapping("/{quizId}")
    public ResponseEntity<QuizResponseDto> updateQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizRequestDto quizRequestDto) {
        return doUpdateQuiz(quizId, quizRequestDto);
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        return doDeleteQuiz(quizId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<QuizResponseDto>> searchQuizzes(@RequestParam String title) {
        return doSearchQuizzes(title);
    }

    @PutMapping("/{quizId}/{slug}")
    public ResponseEntity<QuizResponseDto> updateQuizWithSlug(
            @PathVariable Long quizId,
            @PathVariable String slug,
            @Valid @RequestBody QuizRequestDto quizRequestDto) {
        return doUpdateQuizWithSlug(quizId, slug, quizRequestDto, BASE_PATH);
    }

    @DeleteMapping("/{quizId}/{slug}")
    public ResponseEntity<Void> deleteQuizWithSlug(@PathVariable Long quizId, @PathVariable String slug) {
        return doDeleteQuizWithSlug(quizId, slug, BASE_PATH);
    }
}
