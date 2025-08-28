package com.EdumentumBackend.EdumentumBackend.dtos.course;

import com.EdumentumBackend.EdumentumBackend.enums.CourseLevel;
import com.EdumentumBackend.EdumentumBackend.enums.CourseStatus;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseCreateRequestDto {
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @NotBlank(message = "Short description is required")
    @Size(max = 500, message = "Short description must not exceed 500 characters")
    private String shortDescription;
    
    private String fullDescription;
    
    @NotNull(message = "Course level is required")
    private CourseLevel courseLevel;
    
    private String thumbnailUrl;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid price format")
    private BigDecimal price;

    @NotNull(message = "Course status is required")
    private CourseStatus courseStatus;
    
    private Set<String> tagCourseNames;
}