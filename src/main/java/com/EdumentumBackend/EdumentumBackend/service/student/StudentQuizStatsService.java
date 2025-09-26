package com.EdumentumBackend.EdumentumBackend.service.student;

import com.EdumentumBackend.EdumentumBackend.dto.response.QuizStatsResponseDto;
import com.EdumentumBackend.EdumentumBackend.repository.QuizAttemptRepository;
import com.EdumentumBackend.EdumentumBackend.repository.QuizzesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class StudentQuizStatsService {

    private final QuizzesRepository quizzesRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    public QuizStatsResponseDto getQuizStats(Long userId) {

        Integer totalQuizzes = quizzesRepository.countQuizzesByUserId(userId);


        Integer totalAttempts = quizAttemptRepository.countAttemptsByUserId(userId);


        BigDecimal averageScore = quizAttemptRepository.getAverageScoreByUserId(userId);
        if (averageScore == null) {
            averageScore = BigDecimal.ZERO;
        } else {
            averageScore = averageScore.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal averageDuration = quizAttemptRepository.getAverageDurationByUserId(userId);
        if (averageDuration == null) {
            averageDuration = BigDecimal.ZERO;
        } else {
            averageDuration = averageDuration.divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        }

        Integer completedQuizzes = quizAttemptRepository.countDistinctCompletedQuizzesByUserId(userId);
        Integer totalCorrectAnswers = quizAttemptRepository.sumCorrectAnswersByUserId(userId);
        Integer totalQuestions = quizAttemptRepository.sumTotalQuestionsByUserId(userId);


        BigDecimal accuracyRate = BigDecimal.ZERO;
        if (totalQuestions != null && totalQuestions > 0 && totalCorrectAnswers != null) {
            accuracyRate = BigDecimal.valueOf(totalCorrectAnswers)
                    .divide(BigDecimal.valueOf(totalQuestions), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return QuizStatsResponseDto.builder()
                .totalQuizzes(totalQuizzes != null ? totalQuizzes : 0)
                .totalAttempts(totalAttempts != null ? totalAttempts : 0)
                .averageScore(averageScore)
                .averageDuration(averageDuration)
                .completedQuizzes(completedQuizzes != null ? completedQuizzes : 0)
                .totalCorrectAnswers(totalCorrectAnswers != null ? totalCorrectAnswers : 0)
                .totalQuestions(totalQuestions != null ? totalQuestions : 0)
                .accuracyRate(accuracyRate)
                .build();
    }


    public QuizStatsResponseDto getSystemStats() {
        Integer totalQuizzes = quizzesRepository.countAllQuizzes();
        Integer totalAttempts = quizAttemptRepository.countAllAttempts();

        BigDecimal averageScore = quizAttemptRepository.getOverallAverageScore();
        if (averageScore == null) {
            averageScore = BigDecimal.ZERO;
        } else {
            averageScore = averageScore.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal averageDuration = quizAttemptRepository.getOverallAverageDuration();
        if (averageDuration == null) {
            averageDuration = BigDecimal.ZERO;
        } else {
            averageDuration = averageDuration.divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        }

        Integer totalCorrectAnswers = quizAttemptRepository.sumAllCorrectAnswers();
        Integer totalQuestions = quizAttemptRepository.sumAllTotalQuestions();

        BigDecimal accuracyRate = BigDecimal.ZERO;
        if (totalQuestions != null && totalQuestions > 0 && totalCorrectAnswers != null) {
            accuracyRate = BigDecimal.valueOf(totalCorrectAnswers)
                    .divide(BigDecimal.valueOf(totalQuestions), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return QuizStatsResponseDto.builder()
                .totalQuizzes(totalQuizzes != null ? totalQuizzes : 0)
                .totalAttempts(totalAttempts != null ? totalAttempts : 0)
                .averageScore(averageScore)
                .averageDuration(averageDuration)
                .completedQuizzes(0) // Không áp dụng cho system stats
                .totalCorrectAnswers(totalCorrectAnswers != null ? totalCorrectAnswers : 0)
                .totalQuestions(totalQuestions != null ? totalQuestions : 0)
                .accuracyRate(accuracyRate)
                .build();
    }
}
