package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.flashcard.FlashcardCategoryRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.flashcard.FlashcardCategoryResponseDto;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.service.FlashcardCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/student/flashcard-categories")
@RequiredArgsConstructor
public class StudentFlashcardCategoryController {

    private final FlashcardCategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody FlashcardCategoryRequestDto requestDto) {
        try {
            Long userId = getCurrentUserId();
            FlashcardCategoryResponseDto created = categoryService.createCategory(requestDto, userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Category created successfully",
                    "data", created
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            Long userId = getCurrentUserId();
            List<FlashcardCategoryResponseDto> result = categoryService.getAllActiveCategoriesByUser(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Categories retrieved successfully");
            response.put("data", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            FlashcardCategoryResponseDto dto = categoryService.getCategoryById(id, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Category retrieved successfully",
                    "data", dto
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id,
                                            @Valid @RequestBody FlashcardCategoryRequestDto requestDto) {
        try {
            Long userId = getCurrentUserId();
            FlashcardCategoryResponseDto updated = categoryService.updateCategory(id, requestDto, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Category updated successfully",
                    "data", updated
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            categoryService.deleteCategory(id, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Category deleted successfully"
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

    private ResponseEntity<?> buildServerError(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "error", "Internal server error: " + e.getMessage()
        ));
    }
}
