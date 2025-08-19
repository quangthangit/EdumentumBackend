package com.EdumentumBackend.EdumentumBackend.dtos.quiz;

import com.EdumentumBackend.EdumentumBackend.enums.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRequestDto {
    @NotNull(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String language = "AUTO";

    private VisibilityType visibility = VisibilityType.PRIVATE;

    private QuestionType questionType = QuestionType.MIXED;

    @Min(value = 1, message = "Number of questions must be at least 1")
    @Max(value = 100, message = "Number of questions cannot exceed 100")
    private Integer numberOfQuestions = 10;

    private QuizMode mode = QuizMode.QUIZ;

    private DifficultyLevel difficulty = DifficultyLevel.EASY;

    private ParsingMode parsingMode = ParsingMode.BALANCED;

    private SourceType sourceType;

    private String sourceContent;

    private Boolean isAiGenerated = false;

    private String aiModel;

    private GenerationMode generationMode;

    private FileProcessingMode fileProcessingMode;

    @NotNull(message = "Quiz data is required")
    private String quizData;

    private String[] tags;

    private Integer estimatedTime;

    @Min(value = 0, message = "Passing score must be at least 0")
    @Max(value = 100, message = "Passing score cannot exceed 100")
    private Integer passingScore = 70;
}
