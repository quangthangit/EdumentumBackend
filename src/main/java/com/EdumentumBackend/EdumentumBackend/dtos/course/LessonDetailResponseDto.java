package com.EdumentumBackend.EdumentumBackend.dtos.course;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LessonDetailResponseDto {
    private Long lessonId;
    private String title;
    private String description;
    private Integer orderIndex;
    private List<ExerciseDetailResponseDto> exercises;
}
