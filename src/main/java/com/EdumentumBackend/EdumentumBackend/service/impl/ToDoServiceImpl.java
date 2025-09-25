package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.todo.TodoRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.todo.TodoResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.todo.TodoUpdateRequestDto;
import com.EdumentumBackend.EdumentumBackend.entity.ToDoEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.enums.ToDoStatus;
import com.EdumentumBackend.EdumentumBackend.repository.ToDoRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.ToDoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ToDoServiceImpl implements ToDoService {

    private final ToDoRepository toDoRepository;
    private final UserRepository userRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public TodoResponseDto createTodo(TodoRequestDto todoRequestDto, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ToDoEntity todoEntity = ToDoEntity.builder()
                .nameTask(todoRequestDto.getNameTask())
                .status(ToDoStatus.PENDING)
                .user(user)
                .build();

        ToDoEntity savedTodo = toDoRepository.save(todoEntity);
        return convertToResponseDto(savedTodo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoResponseDto> getAllTodosByUser(Long userId) {
        List<ToDoEntity> todos = toDoRepository.findByUserUserIdOrderByCreationAtDesc(userId);
        return todos.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public TodoResponseDto updateTodo(Long todoId, TodoUpdateRequestDto todoUpdateRequestDto, Long userId) {
        ToDoEntity todo = toDoRepository.findByIdAndUserUserId(todoId, userId)
                .orElseThrow(() -> new RuntimeException("Todo not found or access denied"));

        todo.setNameTask(todoUpdateRequestDto.getNameTask());

        // Update status if provided
        if (todoUpdateRequestDto.getStatus() != null) {
            todo.setStatus(todoUpdateRequestDto.getStatus());
        }

        ToDoEntity updatedTodo = toDoRepository.save(todo);
        return convertToResponseDto(updatedTodo);
    }

    @Override
    public void deleteTodo(Long todoId, Long userId) {
        ToDoEntity todo = toDoRepository.findByIdAndUserUserId(todoId, userId)
                .orElseThrow(() -> new RuntimeException("Todo not found or access denied"));

        toDoRepository.delete(todo);
    }

    private TodoResponseDto convertToResponseDto(ToDoEntity todoEntity) {
        return TodoResponseDto.builder()
                .id(todoEntity.getId())
                .nameTask(todoEntity.getNameTask())
                .status(todoEntity.getStatus().name())
                .creationAt(todoEntity.getCreationAt() != null ?
                          todoEntity.getCreationAt().format(dateFormatter) : null)
                .build();
    }
}
