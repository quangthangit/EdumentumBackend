package com.EdumentumBackend.EdumentumBackend.dtos.quiz;

import com.EdumentumBackend.EdumentumBackend.enums.DifficultyLevel;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizListDto {
    // Basic quiz info
    private Long id;
    private String title;
    private String slug;
    private String description;
    private DifficultyLevel difficulty;
    private Integer maxAttempts;
    private List<String> keywords;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;

    // User attempt statistics
    private LocalDateTime lastAttemptAt;
    private Integer totalAttempts;
    private Integer bestCorrectAnswers;
    private Integer totalQuestions;

    public QuizListDto(Long id, String title, String slug, String description,
                       DifficultyLevel difficulty, Integer maxAttempts, String[] keywords,
                       LocalDateTime createdAt, LocalDateTime publishedAt, Integer totalQuestions) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.difficulty = difficulty;
        this.maxAttempts = maxAttempts;
        this.keywords = keywords != null ? List.of(keywords) : List.of();
        this.createdAt = createdAt;
        this.publishedAt = publishedAt;
        this.totalQuestions = totalQuestions;

        // Initialize attempt stats to default values
        this.totalAttempts = 0;
        this.bestCorrectAnswers = 0;
    }
}
