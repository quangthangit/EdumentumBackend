package com.EdumentumBackend.EdumentumBackend.dtos.course;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonCreateRequestDto {

    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    private String content;
    
    @Min(value = 0, message = "Order index must be non-negative")
    private Integer orderIndex;
    
    private String videoUrl;
    
    @Min(value = 0, message = "Duration must be non-negative")
    private Integer durationMinutes;
}
