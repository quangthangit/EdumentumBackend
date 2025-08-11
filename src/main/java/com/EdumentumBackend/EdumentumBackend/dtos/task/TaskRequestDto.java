package com.EdumentumBackend.EdumentumBackend.dtos.task;

import com.EdumentumBackend.EdumentumBackend.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskRequestDto {
    @NotBlank(message = "Title cannot be blank")
    private String title;
    private String description;
    @NotNull(message = "Status cannot be null")
    private TaskStatus status;
    @NotNull(message = "Due date cannot be null")
    private LocalDateTime dueDate;
}