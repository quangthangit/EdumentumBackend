package com.EdumentumBackend.EdumentumBackend.dtos.quiz;

import com.EdumentumBackend.EdumentumBackend.dtos.auth.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.enums.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResponseDto {
    private Long id;
    private String title;
    private String description;
    private QuizCategoriesResponseDto category;
    private String language;
    private VisibilityType visibility;
    private QuestionType questionType;
    private Integer numberOfQuestions;
    private QuizMode mode;
    private DifficultyLevel difficulty;
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
    private UserResponseDto user;
}
