package com.EdumentumBackend.EdumentumBackend.dtos.course;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponseDto {
    
    private Long lessonId;
    private String title;
    private String content;
    private Integer orderIndex;
    private String videoUrl;
    private Integer durationMinutes;
    private LocalDateTime createdAt;
}
