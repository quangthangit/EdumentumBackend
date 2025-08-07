package com.EdumentumBackend.EdumentumBackend.dtos;

import com.EdumentumBackend.EdumentumBackend.enums.TaskStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private Long userId;
}