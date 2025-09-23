package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizSummaryDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface QuizzesService {
    QuizResponseDto createQuiz(QuizRequestDto quizRequestDto, Long userId);

    List<QuizSummaryDto> getAllQuizzes(Long userId);

    Page<QuizSummaryDto> getAllQuizzesPaginated(Long userId, Pageable pageable);

    Page<QuizSummaryDto> searchQuizzesPaginated(String title, Long userId, Pageable pageable);

    // New methods for optimized quiz listing with attempt statistics
    Page<QuizListDto> getAllQuizzes(Long userId, Pageable pageable);

    Page<QuizListDto> searchQuizzes(String title, Long userId, Pageable pageable);

    // New methods for public quizzes
    Page<QuizListDto> getPublicQuizzes(Pageable pageable);

    Page<QuizListDto> searchPublicQuizzes(String title, Pageable pageable);

    Page<QuizListDto> getPublicQuizzesByTags(List<Long> tagIds, Pageable pageable);
    
    // New method for popular quizzes
    Page<QuizListDto> getPopularPublicQuizzes(String popularityCriteria, Pageable pageable);

    QuizResponseDto getPublicQuizById(Long quizId);

    QuizResponseDto getQuizById(Long quizId, Long userId);

    QuizResponseDto updateQuiz(Long quizId, QuizRequestDto quizRequestDto, Long userId);

    QuizResponseDto patchQuiz(Long quizId, Long userId, Map<String, Object> updates);

    boolean deleteQuiz(Long quizId, Long userId);

    List<QuizSummaryDto> searchQuizzes(String title, Long userId);
}