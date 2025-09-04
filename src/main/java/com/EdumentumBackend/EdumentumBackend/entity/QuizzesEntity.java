package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import lombok.*;
import com.EdumentumBackend.EdumentumBackend.enums.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizzesEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic Information
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    // Ownership & Access
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @Column(name = "original_quiz_id")
    private Long originalQuizId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_quiz_id", insertable = false, updatable = false)
    private QuizzesEntity originalQuiz;

    // Content Structure
    @Column(name = "quiz_data", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> quizData;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", length = 20)
    @Builder.Default
    private DifficultyLevel difficulty = DifficultyLevel.EASY;

    @Column(name = "estimated_time")
    private Integer estimatedTime; // in minutes

    // Scoring Configuration
    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints;

    @Column(name = "passing_score")
    @Builder.Default
    private Integer passingScore = 70; // percentage

    @Column(name = "max_attempts")
    @Builder.Default
    private Integer maxAttempts = 0; // 0 = unlimited

    // AI Information (Simplified)
    @Column(name = "is_ai_generated")
    @Builder.Default
    private Boolean isAiGenerated = false;

    @Column(name = "ai_model", length = 50)
    private String aiModel;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", length = 20)
    private SourceType sourceType;

//    @Column(name = "generation_prompt", columnDefinition = "TEXT")
//    private String generationPrompt;

    // SEO & Discovery
    @Column(name = "meta_title", length = 1000)
    private String metaTitle;

    @Column(name = "meta_description", length = 1000)
    private String metaDescription;

    @Column(name = "canonical_url", length = 500)
    private String canonicalUrl;

    @Column(name = "keywords", columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private String[] keywords;

    // Analytics & Performance
    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "attempt_count")
    @Builder.Default
    private Integer attemptCount = 0;

    @Column(name = "completion_count")
    @Builder.Default
    private Integer completionCount = 0;

    @Column(name = "avg_score", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal avgScore = BigDecimal.ZERO;

    @Column(name = "avg_completion_time")
    @Builder.Default
    private Integer avgCompletionTime = 0; // in seconds

    @Column(name = "bookmark_count")
    @Builder.Default
    private Integer bookmarkCount = 0;

    @Column(name = "share_count")
    @Builder.Default
    private Integer shareCount = 0;

    // Status & Visibility
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", length = 20)
    @Builder.Default
    private VisibilityType visibility = VisibilityType.PRIVATE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private QuizStatus status = QuizStatus.DRAFT;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_trending")
    @Builder.Default
    private Boolean isTrending = false;

    @Column(name = "is_premium")
    @Builder.Default
    private Boolean isPremium = false;

    // Timestamps
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuizTagEntity> quizTags = new HashSet<>();
}