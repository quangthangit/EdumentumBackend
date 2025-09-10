// src/main/java/com/EdumentumBackend/EdumentumBackend/entity/QuestionAttemptEntity.java
package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "question_attempts",
        uniqueConstraints = @UniqueConstraint(name="uq_attempt_question", columnNames={"quiz_attempt_id","question_id"}),
        indexes = {
                @Index(name="idx_qattempts_attempt", columnList="quiz_attempt_id"),
                @Index(name="idx_qattempts_question", columnList="question_id")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionAttemptEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="quiz_attempt_id", nullable = false)
    private Long quizAttemptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_attempt_id", insertable = false, updatable = false)
    private QuizAttemptEntity attempt;

    @Column(name="question_id", nullable = false, length = 100)
    private String questionId; // id trong quizData.questions

    // Meta
    @Column(name="question_type", length = 50)
    private String questionType; // MULTIPLE_CHOICE, TRUE_FALSE...

    @Column(name="difficulty", length = 20)
    private String difficulty;

    @Column(name="points_possible", nullable = false)
    private Integer pointsPossible;

    // User response
    @Column(name="selected_answers", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> selectedAnswers; // array id đáp án đã chọn

    @Column(name="user_answer_text")
    private String userAnswerText; // cho short answer

    @Column(name="is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name="is_partial", nullable = false)
    @Builder.Default
    private Boolean isPartial = false;

    @Column(name="points_earned", precision = 5, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal pointsEarned = BigDecimal.ZERO;

    // Timing & behavior
    @Column(name="time_spent", nullable = false)
    @Builder.Default
    private Integer timeSpent = 0; // giây

    @Column(name="view_count", nullable = false)
    @Builder.Default
    private Integer viewCount = 1;

    @Column(name="skip_count", nullable = false)
    @Builder.Default
    private Integer skipCount = 0;

    @Column(name="answered_at")
    private LocalDateTime answeredAt;

    // A.I. assistance flags
    @Column(name="hint_used", nullable = false)
    @Builder.Default
    private Boolean hintUsed = false;

    @Column(name="ai_help_used", nullable = false)
    @Builder.Default
    private Boolean aiHelpUsed = false;

    @Version
    private Long version;
}
