package com.EdumentumBackend.EdumentumBackend.entity;

import com.EdumentumBackend.EdumentumBackend.enums.AttemptStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "quiz_attempts",
        uniqueConstraints = @UniqueConstraint(name = "uq_attempt_number", columnNames = {"quiz_id", "user_id", "attempt_number"}),
        indexes = {
                @Index(name = "idx_attempts_user_quiz", columnList = "user_id,quiz_id"),
                @Index(name = "idx_attempts_status", columnList = "status"),
                @Index(name = "idx_attempts_completed_at", columnList = "completed_at")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class QuizAttemptEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="quiz_id", nullable = false)
    private Long quizId;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private AttemptStatus status = AttemptStatus.COMPLETED; // vì chỉ lưu khi nộp

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber; // 1,2,3...

    // Scoring
    @Column(name = "score", nullable = false)
    @Builder.Default
    private Integer score = 0;

    @Column(name = "max_score", nullable = false)
    private Integer maxScore;

    @Column(name = "percentage_score", precision = 5, scale = 2)
    private BigDecimal percentageScore; // set từ service (score/maxScore*100)

    @Column(name = "grade", length = 5)
    private String grade;

    // Counters
    @Column(name = "correct_answers", nullable = false)
    @Builder.Default
    private Integer correctAnswers = 0;

    @Column(name = "wrong_answers", nullable = false)
    @Builder.Default
    private Integer wrongAnswers = 0;

    @Column(name = "skipped_answers", nullable = false)
    @Builder.Default
    private Integer skippedAnswers = 0;

    @Column(name = "partial_answers", nullable = false)
    @Builder.Default
    private Integer partialAnswers = 0;

    // Timing
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @Column(name = "total_time_spent", nullable = false)
    @Builder.Default
    private Integer totalTimeSpent = 0; // giây

    @Column(name = "active_time_spent", nullable = false)
    @Builder.Default
    private Integer activeTimeSpent = 0; // nếu không dùng, giữ = total

    // Snapshots/Breakdowns
    @Column(name = "answers_data", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> answersData;

    @Column(name = "difficulty_breakdown", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> difficultyBreakdown;

    @Column(name = "topic_breakdown", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> topicBreakdown;

    // Context
    @Column(name = "device_type", length = 20)
    private String deviceType; // DESKTOP/MOBILE/TABLET

    @Column(name = "browser", length = 50)
    private String browser;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    @Column(name = "location_data", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> locationData;

    // Pass/Certificate
    @Column(name = "is_passed", nullable = false)
    @Builder.Default
    private Boolean isPassed = false;

    @Column(name = "certificate_issued", nullable = false)
    @Builder.Default
    private Boolean certificateIssued = false;

    @Column(name = "certificate_url", length = 500)
    private String certificateUrl;

    // (Tuỳ chọn)
    @Column(name = "study_session_id")
    private java.util.UUID studySessionId;

    @Column(name = "pomodoro_session_id")
    private java.util.UUID pomodoroSessionId;

    // (Gợi ý) liên kết ngược — chỉ đọc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    private QuizzesEntity quiz;

    @Version
    private Long version; // tránh lost-update nếu có sửa
}