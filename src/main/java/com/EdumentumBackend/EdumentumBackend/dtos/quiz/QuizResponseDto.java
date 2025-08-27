package com.EdumentumBackend.EdumentumBackend.dtos.quiz;

import com.EdumentumBackend.EdumentumBackend.dtos.auth.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.enums.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResponseDto {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private String thumbnailUrl;

    // Ownership & Access
    private UserResponseDto user;
    private Long originalQuizId;

    // Content Structure
    private Map<String, Object> quizData;
    private DifficultyLevel difficulty;
    private Integer estimatedTime;

    // Scoring Configuration
    private Integer totalQuestions;
    private Integer totalPoints;
    private Integer passingScore;
    private Integer maxAttempts;

    // AI Information
    private Boolean isAiGenerated;
    private String aiModel;
    private SourceType sourceType;
    private String generationPrompt;

    // SEO & Discovery
    private String metaTitle;
    private String metaDescription;
    private String canonicalUrl;
    private List<String> keywords;

    // Analytics & Performance
    private Integer viewCount;
    private Integer attemptCount;
    private Integer completionCount;
    private BigDecimal avgScore;
    private Integer avgCompletionTime;
    private Integer bookmarkCount;
    private Integer shareCount;

    // Status & Visibility
    private VisibilityType visibility;
    private String status;
    private Boolean isFeatured;
    private Boolean isTrending;
    private Boolean isPremium;

    // Tags
    private List<TagResponseDto> tags;

    // Timestamps
    private LocalDateTime publishedAt;
    private LocalDateTime archivedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
