package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.TaskRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.TaskResponseDto;
import com.EdumentumBackend.EdumentumBackend.exception.AuthenticationFailedException;
import com.EdumentumBackend.EdumentumBackend.exception.InvalidInputException;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/student/tasks")
@RequiredArgsConstructor
@Slf4j
public class StudentTaskController {

    private final TaskService taskService;

    /**
     * Create a new task for the authenticated student
     */
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskRequestDto taskRequestDto) {
        log.info("Creating new task for student");
        
        // Validate request data
        validateTaskRequest(taskRequestDto);
        
        Long userId = getUserIdFromAuthentication();
        TaskResponseDto createdTask = taskService.createTask(taskRequestDto, userId);
        
        log.info("Task created successfully with ID: {} for user: {}", createdTask.getId(), userId);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    /**
     * Update an existing task by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDto taskRequestDto) {
        log.info("Updating task with ID: {} for student", id);
        
        // Validate path parameter
        if (id == null || id <= 0) {
            throw new InvalidInputException("Task ID must be a positive number");
        }
        
        // Validate request data
        validateTaskRequest(taskRequestDto);
        
        Long userId = getUserIdFromAuthentication();
        TaskResponseDto updatedTask = taskService.updateTask(id, taskRequestDto, userId);
        
        log.info("Task updated successfully with ID: {} for user: {}", id, userId);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Delete a task by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("Deleting task with ID: {} for student", id);
        
        // Validate path parameter
        if (id == null || id <= 0) {
            throw new InvalidInputException("Task ID must be a positive number");
        }
        
        Long userId = getUserIdFromAuthentication();
        taskService.deleteTask(id, userId);
        
        log.info("Task deleted successfully with ID: {} for user: {}", id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all tasks for the authenticated student
     */
    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        log.info("Fetching all tasks for student");
        
        Long userId = getUserIdFromAuthentication();
        List<TaskResponseDto> tasks = taskService.getAllTasks(userId);
        
        log.info("Retrieved {} tasks for user: {}", tasks.size(), userId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get a specific task by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        log.info("Fetching task with ID: {} for student", id);
        
        // Validate path parameter
        if (id == null || id <= 0) {
            throw new InvalidInputException("Task ID must be a positive number");
        }
        
        Long userId = getUserIdFromAuthentication();
        TaskResponseDto task = taskService.getTaskById(id, userId);
        
        log.info("Retrieved task with ID: {} for user: {}", id, userId);
        return ResponseEntity.ok(task);
    }

    /**
     * Extract user ID from Spring Security authentication context
     */
    private Long getUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null) {
            log.error("No authentication found in security context");
            throw new AuthenticationFailedException("User not authenticated - no security context");
        }
        
        if (authentication.getPrincipal() == null) {
            log.error("No principal found in authentication");
            throw new AuthenticationFailedException("User not authenticated - no principal");
        }
        
        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            log.error("Invalid principal type: {}", authentication.getPrincipal().getClass());
            throw new AuthenticationFailedException("Invalid authentication principal type");
        }
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        
        if (userId == null) {
            log.error("User ID is null in authentication details");
            throw new AuthenticationFailedException("User ID not found in authentication");
        }
        
        log.debug("Successfully extracted user ID: {}", userId);
        return userId;
    }

    /**
     * Validate task request data
     */
    private void validateTaskRequest(TaskRequestDto taskRequestDto) {
        if (taskRequestDto == null) {
            throw new InvalidInputException("Task request cannot be null");
        }
        
        if (taskRequestDto.getTitle() == null || taskRequestDto.getTitle().trim().isEmpty()) {
            throw new InvalidInputException("Task title cannot be empty");
        }
        
        if (taskRequestDto.getTitle().length() > 255) {
            throw new InvalidInputException("Task title cannot exceed 255 characters");
        }
        
        if (taskRequestDto.getDescription() != null && taskRequestDto.getDescription().length() > 1000) {
            throw new InvalidInputException("Task description cannot exceed 1000 characters");
        }
        
        if (taskRequestDto.getStatus() == null) {
            throw new InvalidInputException("Task status cannot be null");
        }
        
        if (taskRequestDto.getDueDate() == null) {
            throw new InvalidInputException("Task due date cannot be null");
        }
    }
}