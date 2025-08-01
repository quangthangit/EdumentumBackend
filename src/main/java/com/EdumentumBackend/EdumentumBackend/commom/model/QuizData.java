package com.EdumentumBackend.EdumentumBackend.commom.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizData {
    private List<QuestionData> questions;
    private QuizMetadata metadata;
    private QuizSettings settings;
} 