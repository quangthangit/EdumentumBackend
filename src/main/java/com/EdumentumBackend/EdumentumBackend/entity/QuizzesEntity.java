package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import lombok.*;
import com.EdumentumBackend.EdumentumBackend.enums.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

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

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private QuizCategoriesEntity category;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private VisibilityType visibility = VisibilityType.PRIVATE;

    @Column(name = "language", length = 10)
    private String language = "AUTO";

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type")
    private QuestionType questionType = QuestionType.MIXED;

    @Column(name = "number_of_questions")
    private Integer numberOfQuestions = 10;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode")
    private QuizMode mode = QuizMode.QUIZ;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    private DifficultyLevel difficulty = DifficultyLevel.EASY;

    @Column(name = "task", length = 50)
    private String task = "GENERATE_QUIZ";

    @Enumerated(EnumType.STRING)
    @Column(name = "parsing_mode")
    private ParsingMode parsingMode = ParsingMode.BALANCED;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type")
    private SourceType sourceType;

    @Column(name = "source_content", columnDefinition = "TEXT")
    private String sourceContent;

    @Column(name = "is_ai_generated")
    private Boolean isAiGenerated = false;

    @Column(name = "ai_model", length = 50)
    private String aiModel;

    @Enumerated(EnumType.STRING)
    @Column(name = "generation_mode")
    private GenerationMode generationMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_processing_mode")
    private FileProcessingMode fileProcessingMode;

    @Column(name = "quiz_data", columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String quizData;

    @Column(name = "tags", columnDefinition = "text[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private String[] tags;

    @Column(name = "estimated_time")
    private Integer estimatedTime;

    @Column(name = "passing_score")
    private Integer passingScore = 70;

    @Column(name = "total_questions", insertable = false, updatable = false)
    private Integer totalQuestions;

    @Column(name = "total_points", insertable = false, updatable = false)
    private Integer totalPoints;
}