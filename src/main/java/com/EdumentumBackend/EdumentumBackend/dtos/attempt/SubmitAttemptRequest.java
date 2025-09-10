package com.EdumentumBackend.EdumentumBackend.dtos.attempt;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SubmitAttemptRequest {

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Integer timeSpentSec;

    private List<AnswerItem> answers;

    @Data
    public static class AnswerItem {
        private String questionId;
        private List<String> selectedOptionIds;
        private String userAnswerText;
    }
}