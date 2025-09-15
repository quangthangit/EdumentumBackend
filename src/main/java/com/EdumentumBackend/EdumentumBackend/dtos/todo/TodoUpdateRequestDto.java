package com.EdumentumBackend.EdumentumBackend.dtos.todo;

import com.EdumentumBackend.EdumentumBackend.enums.ToDoStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TodoUpdateRequestDto {
    @NotEmpty(message = "Task name is required")
    @Size(max = 100, message = "Task name must be at most 100 characters long")
    private String nameTask;

    private ToDoStatus status;
}
