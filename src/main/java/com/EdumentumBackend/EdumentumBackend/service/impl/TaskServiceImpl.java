package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.TaskRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.TaskResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.TaskEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.TaskRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    public TaskResponseDto createTask(TaskRequestDto taskRequestDto, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User id "+ userId + " not found"));
        TaskEntity taskEntity = TaskEntity.builder().title(taskRequestDto.getTitle())
                .description(taskRequestDto.getDescription()).status(taskRequestDto.getStatus())
                .dueDate(taskRequestDto.getDueDate()).user(user).build();
        TaskEntity savedTask = taskRepository.save(taskEntity);
        return convertToDto(savedTask);
    }

    @Override
    public TaskResponseDto updateTask(Long id, TaskRequestDto taskRequestDto, Long userId) {
        TaskEntity existingTask = taskRepository.findByIdAndUserUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Task" + id + " not found or you don't have permission"));
        existingTask.setTitle(taskRequestDto.getTitle());
        existingTask.setDescription(taskRequestDto.getDescription());
        existingTask.setStatus(taskRequestDto.getStatus());
        existingTask.setDueDate(taskRequestDto.getDueDate());
        TaskEntity updatedTask = taskRepository.save(existingTask);
        return convertToDto(updatedTask);
    }

    @Override
    public void deleteTask(Long id, Long userId) {
        TaskEntity task = taskRepository.findByIdAndUserUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Task not found or you don't have permission"));
        taskRepository.delete(task);
    }

    @Override
    public List<TaskResponseDto> getAllTasks(Long userId) {
        return taskRepository.findByUserUserId(userId).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public TaskResponseDto getTaskById(Long id, Long userId) {
        TaskEntity task = taskRepository.findByIdAndUserUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Task not found or you don't have permission"));
        return convertToDto(task);
    }

    private TaskResponseDto convertToDto(TaskEntity task) {
        return TaskResponseDto.builder().id(task.getId()).title(task.getTitle()).description(task.getDescription())
                .status(task.getStatus()).dueDate(task.getDueDate()).createdAt(task.getCreatedAt())
                .userId(task.getUser().getUserId()).build();
    }
}