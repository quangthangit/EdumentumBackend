package com.EdumentumBackend.EdumentumBackend.dtos.todo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TodoRequestDto {
    @NotEmpty(message = "Task name is required")
    @Size(max = 100, message = "Task name must be at most 100 characters long")
    private String nameTask;
}
