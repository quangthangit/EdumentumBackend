package com.EdumentumBackend.EdumentumBackend.dtos.course;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCourseDetailDto {
    private CourseResponseDto course;
    private List<LessonResponseDto> lessons;
    private List<ExerciseResponseDto> exercises;
    private List<ResourceResponseDto> resources;
    private Integer totalEnrollments;
    private Double averageRating;
    private List<RatingResponseDto> recentRatings;
}
