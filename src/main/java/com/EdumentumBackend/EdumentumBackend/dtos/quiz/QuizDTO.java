package com.EdumentumBackend.EdumentumBackend.dtos.quiz;

import com.EdumentumBackend.EdumentumBackend.enums.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuizDTO {
    private Long id;
    private String title;
    private String description;
    private Long userId;
    private QuizCategoryDTO category;
    private VisibilityType visibility;
    private String language;
    private QuestionType questionType;
    private Integer numberOfQuestions;
    private QuizMode mode;
    private DifficultyLevel difficulty;
    private String task;
    private ParsingMode parsingMode;
    private SourceType sourceType;
    private String sourceContent;
    private Boolean isAiGenerated;
    private String aiModel;
    private GenerationMode generationMode;
    private FileProcessingMode fileProcessingMode;
    private String quizData;
    private String[] tags;
    private Integer estimatedTime;
    private Integer passingScore;
    private Integer totalQuestions;
    private Integer totalPoints;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}