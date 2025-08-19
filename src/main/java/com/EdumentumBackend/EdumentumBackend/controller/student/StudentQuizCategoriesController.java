package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizCategoriesRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.quiz.QuizCategoriesResponseDto;
import com.EdumentumBackend.EdumentumBackend.service.QuizCategoriesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student/quiz-categories")
@CrossOrigin(origins = "*")
public class StudentQuizCategoriesController {

    @Autowired
    private QuizCategoriesService quizCategoriesService;

    @GetMapping
    public ResponseEntity<List<QuizCategoriesResponseDto>> getAllCategories() {
        try {
            List<QuizCategoriesResponseDto> categories = quizCategoriesService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<QuizCategoriesResponseDto>> getAllActiveCategories() {
        try {
            List<QuizCategoriesResponseDto> categories = quizCategoriesService.getActiveCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizCategoriesResponseDto> getCategoryById(@PathVariable Long id) {
        try {
            QuizCategoriesResponseDto category = quizCategoriesService.getCategoryById(id);
            if (category != null) {
                return ResponseEntity.ok(category);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<QuizCategoriesResponseDto> createCategory(@Valid @RequestBody QuizCategoriesRequestDto requestDto) {
        try {
            QuizCategoriesResponseDto createdCategory = quizCategoriesService.createCategory(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizCategoriesResponseDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody QuizCategoriesRequestDto requestDto) {
        try {
            QuizCategoriesResponseDto updatedCategory = quizCategoriesService.updateCategory(id, requestDto);
            if (updatedCategory != null) {
                return ResponseEntity.ok(updatedCategory);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            boolean deleted = quizCategoriesService.deleteCategory(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
