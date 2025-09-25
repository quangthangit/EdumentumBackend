package com.EdumentumBackend.EdumentumBackend.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizStatsResponseDto {

    // Tổng số bài kiểm tra
    private Integer totalQuizzes;

    // Tổng lượt làm
    private Integer totalAttempts;

    // Điểm trung bình
    private BigDecimal averageScore;

    // Thời gian trung bình (tính bằng phút)
    private BigDecimal averageDuration;

    // Thống kê bổ sung
    private Integer completedQuizzes;
    private Integer totalCorrectAnswers;
    private Integer totalQuestions;
    private BigDecimal accuracyRate;
}
