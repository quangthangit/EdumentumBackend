package com.EdumentumBackend.EdumentumBackend.dtos.course;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LessonPreviewResponseDto {
    private Long lessonId;
    private String title;
    private String description;
    private Integer orderIndex;
}
