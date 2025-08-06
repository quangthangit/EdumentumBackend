package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.TaskRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.TaskResponseDto;
import java.util.List;

public interface TaskService {
    TaskResponseDto createTask(TaskRequestDto task, Long userId);

    TaskResponseDto updateTask(Long id, TaskRequestDto taskRequestDto, Long userId);

    void deleteTask(Long id, Long userId);

    List<TaskResponseDto> getAllTasks(Long userId);

    TaskResponseDto getTaskById(Long id, Long userId);
}