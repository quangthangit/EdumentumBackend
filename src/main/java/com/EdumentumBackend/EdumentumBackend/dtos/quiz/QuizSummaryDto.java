package com.EdumentumBackend.EdumentumBackend.dtos.quiz;

import com.EdumentumBackend.EdumentumBackend.dtos.auth.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.enums.DifficultyLevel;
import com.EdumentumBackend.EdumentumBackend.enums.SourceType;
import com.EdumentumBackend.EdumentumBackend.enums.VisibilityType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSummaryDto {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private String thumbnailUrl;

    private UserResponseDto user;
    private Long originalQuizId;

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

    private VisibilityType visibility;
    private String status;
    private Boolean isFeatured;
    private Boolean isTrending;
    private Boolean isPremium;

    // Tags
    private List<TagResponseDto> tags;

    private LocalDateTime publishedAt;
    private LocalDateTime archivedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public QuizSummaryDto(Long id, String title, String slug, String description, String thumbnailUrl,
                      VisibilityType visibility, DifficultyLevel difficulty,
                      Integer totalQuestions, Integer totalPoints,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.visibility = visibility;
        this.difficulty = difficulty;
        this.totalQuestions = totalQuestions;
        this.totalPoints = totalPoints;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
