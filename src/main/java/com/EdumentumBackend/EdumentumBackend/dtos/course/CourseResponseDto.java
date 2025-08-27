package com.EdumentumBackend.EdumentumBackend.dtos.course;

import com.EdumentumBackend.EdumentumBackend.enums.CourseLevel;
import com.EdumentumBackend.EdumentumBackend.enums.CourseStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDto {
    
    private Long courseId;
    private String title;
    private String shortDescription;
    private String fullDescription;
    private CourseLevel courseLevel;
    private CourseStatus status;
    private String thumbnailUrl;
    private BigDecimal price;
    private TeacherSummaryResponseDto teacher;
    private Set<TagCourseResponseDto> tags;
    private Integer totalEnrollments;
    private Double averageRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Summary version for lists
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private Long courseId;
        private String title;
        private String shortDescription;
        private CourseLevel courseLevel;
        private String thumbnailUrl;
        private BigDecimal price;
        private String teacherName;
        private Integer totalEnrollments;
        private Double averageRating;
        private LocalDateTime createdAt;
    }
}