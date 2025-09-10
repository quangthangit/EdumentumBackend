package com.EdumentumBackend.EdumentumBackend.dtos.course;

import com.EdumentumBackend.EdumentumBackend.enums.EnrollmentStatus;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponseDto {
    
    private Long enrollmentId;
    private CourseResponseDto.Summary course;
    private EnrollmentStatus status;
    private BigDecimal paidAmount;
    private Integer completedLessons;
    private Integer completedExercises;
    private Double progressPercentage;
    private LocalDateTime enrolledAt;
}
