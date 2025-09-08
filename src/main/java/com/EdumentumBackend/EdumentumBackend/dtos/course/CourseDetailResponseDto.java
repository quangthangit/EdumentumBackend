package com.EdumentumBackend.EdumentumBackend.dtos.course;

import com.EdumentumBackend.EdumentumBackend.enums.EnrollmentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailResponseDto {

    private CourseResponseDto course;
    private List<LessonResponseDto> lessons;
    private List<ExerciseResponseDto> exercises;
    private List<ResourceResponseDto> resources;
    private Boolean isEnrolled;
    private EnrollmentStatus enrollmentStatus;
    private BigDecimal progressPercentage;
    private RatingResponseDto userRating;
}
