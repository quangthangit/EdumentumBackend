package com.EdumentumBackend.EdumentumBackend.dtos.quiz;

import com.EdumentumBackend.EdumentumBackend.enums.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    private String thumbnailUrl;

    private DifficultyLevel difficulty = DifficultyLevel.EASY;

    private Integer estimatedTime;

    @Min(value = 0, message = "Passing score must be at least 0")
    @Max(value = 100, message = "Passing score cannot exceed 100")
    private Integer passingScore = 70;

    private Integer maxAttempts = 0;

    // AI Information
    private Boolean isAiGenerated = false;
    private String aiModel;
    private SourceType sourceType;

    // SEO & Discovery
    private String metaTitle;
    private String metaDescription;
    private String canonicalUrl;
    private List<String> keywords;

    // Status & Visibility
    private VisibilityType visibility = VisibilityType.PRIVATE;
//    private String status = "DRAFT";
    private Boolean isPremium = false;

    // Content
    @NotNull(message = "Quiz data is required")
    private Map<String, Object> quizData;

//    private List<Long> tagIds;
//    private List<String> tagsNames;

    private  List<TagRequestDto> tags;
}
