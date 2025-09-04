package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface QuizzesService {
    QuizResponseDto createQuiz(QuizRequestDto quizRequestDto, Long userId);

    List<QuizResponseDto> getAllQuizzes(Long userId);

    Page<QuizResponseDto> getAllQuizzesPaginated(Long userId, Pageable pageable);

    Page<QuizResponseDto> searchQuizzesPaginated(String title, Long userId, Pageable pageable);

    QuizResponseDto getQuizById(Long quizId, Long userId);

    QuizResponseDto updateQuiz(Long quizId, QuizRequestDto quizRequestDto, Long userId);

    QuizResponseDto patchQuiz(Long quizId, Long userId, Map<String, Object> updates);

    boolean deleteQuiz(Long quizId, Long userId);

//    List<QuizResponseDto> getQuizzesByCategory(Long categoryId, Long userId);

    List<QuizResponseDto> searchQuizzes(String title, Long userId);
}