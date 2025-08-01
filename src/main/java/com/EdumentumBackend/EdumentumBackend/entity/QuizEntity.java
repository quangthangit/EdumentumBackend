package com.EdumentumBackend.EdumentumBackend.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "quiz")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private QuizzCategoriesEntity category;

    // Quiz settings
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private QuizVisibility visibility = QuizVisibility.PRIVATE;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private QuizLanguageType language = QuizLanguageType.AUTO;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", length = 20)
    private QuestionType questionType = QuestionType.MIXED;

    @Min(value = 1, message = "Number of questions must be at least 1")
    @Column(name = "number_of_questions")
    private Integer numberOfQuestions = 10;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private QuizModeType mode = QuizModeType.QUIZ;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private QuizDifficultyType difficulty = QuizDifficultyType.EASY;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private QuizTask task = QuizTask.GENERATE_QUIZ;

    @Enumerated(EnumType.STRING)
    @Column(name = "parsing_mode", length = 20)
    private ParsingModeType parsingMode = ParsingModeType.FAST;

    // Source information
    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", length = 20)
    private SourceType sourceType;

    @Column(name = "source_content", columnDefinition = "TEXT")
    private String sourceContent;

    // AI info
    @Column(name = "is_ai_generated")
    private Boolean isAiGenerated = false;

    @Size(max = 50, message = "AI model must not exceed 50 characters")
    @Column(name = "ai_model", length = 50)
    private String aiModel;

    // Quiz data (JSONB)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "quiz_data", columnDefinition = "jsonb", nullable = false)
    private Object quizData;

    // Metadata
    @Column(columnDefinition = "TEXT[]")
    private List<String> tags;

    @Min(value = 1, message = "Estimated time must be at least 1 minute")
    @Column(name = "estimated_time")
    private Integer estimatedTime;

    @Min(value = 0, message = "Passing score must be non-negative")
    @Max(value = 100, message = "Passing score cannot exceed 100")
    @Column(name = "passing_score")
    private Integer passingScore = 70;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (visibility == null) {
            visibility = QuizVisibility.PRIVATE;
        }
        if (language == null) {
            language = QuizLanguageType.AUTO;
        }
        if (questionType == null) {
            questionType = QuestionType.MIXED;
        }
        if (numberOfQuestions == null) {
            numberOfQuestions = 10;
        }
        if (mode == null) {
            mode = QuizModeType.QUIZ;
        }
        if (difficulty == null) {
            difficulty = QuizDifficultyType.EASY;
        }
        if (task == null) {
            task = QuizTask.GENERATE_QUIZ;
        }
        if (parsingMode == null) {
            parsingMode = ParsingModeType.FAST;
        }
        if (isAiGenerated == null) {
            isAiGenerated = false;
        }
        if (passingScore == null) {
            passingScore = 70;
        }
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
