package com.EdumentumBackend.EdumentumBackend.dtos.course;

import com.EdumentumBackend.EdumentumBackend.enums.EnrollmentStatus;
import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrolledStudentCourseDetailDto {
    private CourseResponseDto course;
    private List<LessonResponseDto> lessons;
    private List<ExerciseResponseDto> exercises;
    private List<ResourceResponseDto> resources;
    private EnrollmentStatus enrollmentStatus;
    private Double progressPercentage;
    private Integer completedLessons;
    private Integer completedExercises;
    private RatingResponseDto userRating;
}
