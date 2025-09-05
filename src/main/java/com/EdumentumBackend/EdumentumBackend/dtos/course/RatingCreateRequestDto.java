package com.EdumentumBackend.EdumentumBackend.dtos.course;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingCreateRequestDto {

    
    @Min(value = 0, message = "Rating must be between 0 and 5")
    @Max(value = 5, message = "Rating must be between 0 and 5")
    @NotNull(message = "Rating is required")
    private Integer rating;
    
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;
}

