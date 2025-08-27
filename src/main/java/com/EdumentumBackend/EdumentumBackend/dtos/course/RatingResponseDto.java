package com.EdumentumBackend.EdumentumBackend.dtos.course;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDto {
    private Long ratingId;
    private Integer rating;
    private String comment;
    private String studentName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
