package com.EdumentumBackend.EdumentumBackend.dtos.course;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseResponseDto {
    
    private Long exerciseId;
    private String title;
    private String description;
    private String instructions;
    private Integer orderIndex;
    private LocalDateTime createdAt;
}

