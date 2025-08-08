package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.TaskRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.TaskResponseDto;
import com.EdumentumBackend.EdumentumBackend.exception.InvalidInputException;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.service.TaskService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/student/tasks")
@Slf4j
public class StudentTaskController {

    private final TaskService taskService;

    public StudentTaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequestDto taskRequestDto) {
        try {
            Long userId = getCurrentUserId();

            TaskResponseDto createdTask = taskService.createTask(taskRequestDto, userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Task created successfully",
                    "data", createdTask
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTasks() {
        try {
            Long userId = getCurrentUserId();

            List<TaskResponseDto> tasks = taskService.getAllTasks(userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Tasks retrieved successfully",
                    "data", tasks,
                    "total", tasks.size()
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            validateTaskId(id);

            Long userId = getCurrentUserId();

            TaskResponseDto task = taskService.getTaskById(id, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Task retrieved successfully",
                    "data", task
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDto taskRequestDto) {
        try {
            validateTaskId(id);

            Long userId = getCurrentUserId();

            TaskResponseDto updatedTask = taskService.updateTask(id, taskRequestDto, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Task updated successfully",
                    "data", updatedTask
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            validateTaskId(id);

            Long userId = getCurrentUserId();

            taskService.deleteTask(id, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Task deleted successfully"
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    // -------------------- PRIVATE HELPERS --------------------

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new RuntimeException("Unauthorized");
        }

        return userDetails.getId();
    }

    private void validateTaskId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidInputException("Task ID must be a positive number");
        }
    }

    private ResponseEntity<?> buildServerError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "error", "Internal server error: " + e.getMessage()
        ));
    }
}
