package com.EdumentumBackend.EdumentumBackend.dtos.attempt;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QuizAttemptDto {
    private Long attemptId;
    private Integer score;
    private Integer maxScore;
    private Double finalScorePercent;
    private Integer correct;
    private Integer wrong;
    private Integer skipped;
    private Integer timeSpentSec;
    private String performance;
    private LocalDateTime completedAt;
    private LocalDateTime startedAt;
}
