package com.EdumentumBackend.EdumentumBackend.dtos.quiz;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizStatsResponseDto {

    private Integer totalQuizzes;

    private Integer totalAttempts;

    private BigDecimal averageScore;

    private BigDecimal averageDuration;

    private Integer completedQuizzes;
    private Integer totalCorrectAnswers;
    private Integer totalQuestions;
    private BigDecimal accuracyRate;
}
