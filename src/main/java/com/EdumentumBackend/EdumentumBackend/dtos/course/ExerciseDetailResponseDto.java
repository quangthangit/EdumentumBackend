package com.EdumentumBackend.EdumentumBackend.dtos.course;

import com.EdumentumBackend.EdumentumBackend.enums.ExerciseType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExerciseDetailResponseDto {
    private Long exerciseId;
    private String title;
    private String description;
    private ExerciseType type;
}
