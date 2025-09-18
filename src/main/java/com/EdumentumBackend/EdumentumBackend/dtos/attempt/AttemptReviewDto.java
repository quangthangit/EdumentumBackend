package com.EdumentumBackend.EdumentumBackend.dtos.attempt;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AttemptReviewDto {
    private Long attemptId;
    private Long quizId;
    private Integer score;
    private Integer maxScore;
    private Double finalScorePercent;
    private Integer correct;
    private Integer wrong;
    private Integer skipped;
    private Integer timeSpentSec;
    private String performance;
    private LocalDateTime completedAt;

    private List<QuestionReview> questions;

    @Data @Builder
    public static class QuestionReview {
        private String questionId;
        private Integer order;
        private String questionText;
        private boolean isCorrect;
        private List<String> selectedOptionIds;
        private List<String> correctOptionIds;
        private Integer pointsPossible;
        private Integer pointsEarned;
        private String timeSpent; // Add this field
        private String explanation;

        private List<Option> options;
    }

    @Data @Builder
    public static class Option {
        private String id;
        private String text;
    }
}