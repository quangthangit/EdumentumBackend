package com.EdumentumBackend.EdumentumBackend.dtos.course;

import com.EdumentumBackend.EdumentumBackend.enums.ResourceType;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceCreateRequestDto {
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @NotNull(message = "Resource type is required")
    private ResourceType resourceType;
    
    @NotBlank(message = "URL is required")
    private String url;
    
    @Min(value = 0, message = "Order index must be non-negative")
    private Integer orderIndex;
}