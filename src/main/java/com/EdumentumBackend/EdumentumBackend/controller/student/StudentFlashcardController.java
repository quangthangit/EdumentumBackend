package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.flashcard.FlashcardSetRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.flashcard.FlashcardSetResponseDto;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.service.FlashcardService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/student/flashcards")
public class StudentFlashcardController {

    private final FlashcardService flashcardService;

    public StudentFlashcardController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    @PostMapping
    public ResponseEntity<?> createFlashcardSet(@Valid @RequestBody FlashcardSetRequestDto flashcardSetRequestDto) {
        try {
            Long userId = getCurrentUserId();

            FlashcardSetResponseDto createdSet = flashcardService.createFlashcardSet(flashcardSetRequestDto, userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Flashcard set created successfully",
                    "data", createdSet
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllFlashcardSets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        try {
            Long userId = getCurrentUserId();
            Pageable pageable = PageRequest.of(page, size);

            var result = flashcardService.getAllFlashcardSets(userId, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Flashcard sets retrieved successfully");
            response.put("data", result.getData());
            response.put("pagination", result.getPagination());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @GetMapping("/public")
    public ResponseEntity<?> getPublicFlashcardSets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            var result = flashcardService.getPublicFlashcardSets(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Public flashcard sets retrieved successfully");
            response.put("data", result.getData());
            response.put("pagination", result.getPagination());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFlashcardSetById(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();

            FlashcardSetResponseDto flashcardSet = flashcardService.getFlashcardSetById(id, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Flashcard set retrieved successfully",
                    "data", flashcardSet
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateFlashcardSet(
            @PathVariable Long id,
            @Valid @RequestBody FlashcardSetRequestDto flashcardSetRequestDto
    ) {
        try {
            Long userId = getCurrentUserId();

            FlashcardSetResponseDto updatedSet = flashcardService.updateFlashcardSet(id, flashcardSetRequestDto, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Flashcard set updated successfully",
                    "data", updatedSet
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFlashcardSet(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();

            flashcardService.deleteFlashcardSet(id, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Flashcard set deleted successfully"
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
