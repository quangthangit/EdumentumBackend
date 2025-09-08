package com.EdumentumBackend.EdumentumBackend.entity.course;

import com.EdumentumBackend.EdumentumBackend.entity.BaseEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.enums.EnrollmentStatus;
import org.hibernate.annotations.Formula;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "course_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enrollmentId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity course;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;
    
    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;
    
    // Progress tracking
    @Column(nullable = false)
    @Builder.Default
    private Integer completedLessons = 0;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer completedExercises = 0;
    
    @Formula("CASE WHEN (SELECT COUNT(*) FROM lessons l WHERE l.course_id = course_id) > 0 " +
            "THEN (completed_lessons * 100.0) / (SELECT COUNT(*) FROM lessons l WHERE l.course_id = course_id) " +
            "ELSE 0 END")
    private Double progressPercentage;
}