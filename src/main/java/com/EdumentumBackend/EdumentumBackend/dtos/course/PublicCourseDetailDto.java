package com.EdumentumBackend.EdumentumBackend.dtos.course;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicCourseDetailDto {
    private CourseResponseDto course;
    private String shortDescription;
    private List<RatingResponseDto> ratings;
    private Double averageRating;
    private Integer totalEnrollments;
    private Integer totalLessons;
    private Integer totalExercises;
}
