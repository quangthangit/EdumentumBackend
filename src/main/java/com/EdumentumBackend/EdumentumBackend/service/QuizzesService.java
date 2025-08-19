package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizResponseDto;
import java.util.List;

public interface QuizzesService {
    QuizResponseDto createQuiz(QuizRequestDto quizRequestDto, Long userId);

    List<QuizResponseDto> getAllQuizzes(Long userId);

    QuizResponseDto getQuizById(Long quizId, Long userId);

    QuizResponseDto updateQuiz(Long quizId, QuizRequestDto quizRequestDto, Long userId);

    boolean deleteQuiz(Long quizId, Long userId);

    List<QuizResponseDto> getQuizzesByCategory(Long categoryId, Long userId);

    List<QuizResponseDto> searchQuizzes(String title, Long userId);
}