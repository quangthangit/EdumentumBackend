package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.task.TaskRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.task.TaskResponseDto;
import java.util.List;

public interface TaskService {
    TaskResponseDto createTask(TaskRequestDto task, Long userId);
    TaskResponseDto updateTask(Long id, TaskRequestDto taskRequestDto, Long userId);
    void deleteTask(Long id, Long userId);
    List<TaskResponseDto> getAllTasks(Long userId);
    TaskResponseDto getTaskById(Long id, Long userId);
}