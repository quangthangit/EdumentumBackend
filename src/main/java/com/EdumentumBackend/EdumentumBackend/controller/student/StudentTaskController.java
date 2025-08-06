package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.TaskRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.TaskResponseDto;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.service.TaskService;
import lombok.RequiredArgsConstructor;
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
public class StudentTaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskRequestDto taskRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = getUserIdFromAuthentication(authentication);
        return new ResponseEntity<>(taskService.createTask(taskRequestDto, userId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDto taskRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(taskService.updateTask(id, taskRequestDto, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = getUserIdFromAuthentication(authentication);
        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(taskService.getAllTasks(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(taskService.getTaskById(id, userId));
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getId();
        }
        throw new SecurityException("User not authenticated");
    }
}