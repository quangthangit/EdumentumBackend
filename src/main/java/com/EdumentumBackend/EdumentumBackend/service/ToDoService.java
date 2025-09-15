package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.todo.TodoRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.todo.TodoResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.todo.TodoUpdateRequestDto;

import java.util.List;

public interface ToDoService {
    TodoResponseDto createTodo(TodoRequestDto todoRequestDto, Long userId);
    List<TodoResponseDto> getAllTodosByUser(Long userId);
    TodoResponseDto updateTodo(Long todoId, TodoUpdateRequestDto todoUpdateRequestDto, Long userId);
    void deleteTodo(Long todoId, Long userId);
}
