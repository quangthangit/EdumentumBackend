package com.EdumentumBackend.EdumentumBackend.controller.user;

import com.EdumentumBackend.EdumentumBackend.dtos.todo.TodoRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.todo.TodoUpdateRequestDto;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.service.ToDoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/v1/user/todos")
@RequiredArgsConstructor
public class ToDoController {

    private final ToDoService toDoService;

    @PostMapping
    public ResponseEntity<?> createTodo(@Valid @RequestBody TodoRequestDto todoRequestDto) {
        try {
            Long userId = getCurrentUserId();
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Todo created successfully",
                    "data", toDoService.createTodo(todoRequestDto, userId)
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTodos() {
        try {
            Long userId = getCurrentUserId();
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Get all todos successfully",
                    "data", toDoService.getAllTodosByUser(userId)
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<?> updateTodo(@PathVariable Long todoId,
                                       @Valid @RequestBody TodoUpdateRequestDto todoUpdateRequestDto) {
        try {
            Long userId = getCurrentUserId();
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Todo updated successfully",
                    "data", toDoService.updateTodo(todoId, todoUpdateRequestDto, userId)
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long todoId) {
        try {
            Long userId = getCurrentUserId();
            toDoService.deleteTodo(todoId, userId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Todo deleted successfully"
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }

    private ResponseEntity<?> buildServerError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "message", "Internal server error: " + e.getMessage()
        ));
    }
}
