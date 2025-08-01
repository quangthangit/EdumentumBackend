package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_attempts")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class QuizAttemptsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", referencedColumnName = "id")
    private QuizEntity quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private UserEntity user;

    // Attempt info
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private QuizAttemptStatus status = QuizAttemptStatus.IN_PROGRESS;

    // Results
    @Column
    private Integer score;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "correct_answers")
    private Integer correctAnswers;

    @Column(name = "time_taken")
    private Integer timeTaken; // seconds

    @Column(name = "passing_score")
    private Integer passingScore;

    @Column(name = "is_passed")
    private Boolean isPassed;

    // User answers (JSONB)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "user_answers", columnDefinition = "jsonb", nullable = false)
    private Object userAnswers;

    // Quiz snapshot (to track if quiz changes)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "quiz_snapshot", columnDefinition = "jsonb")
    private Object quizSnapshot;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public enum QuizAttemptStatus {
        IN_PROGRESS,
        COMPLETED,
        ABANDONED
    }
}
