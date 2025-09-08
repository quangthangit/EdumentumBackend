package com.EdumentumBackend.EdumentumBackend.dtos.course;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseCreateRequestDto {
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private String instructions;
    
    @Min(value = 0, message = "Order index must be non-negative")
    private Integer orderIndex;
}

