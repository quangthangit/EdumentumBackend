package com.EdumentumBackend.EdumentumBackend.commom.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSettings {
    private boolean shuffle_questions;
    private boolean shuffle_answers;
    private boolean show_explanations;
    private boolean allow_retry;
    private Integer time_limit_per_question;
} 