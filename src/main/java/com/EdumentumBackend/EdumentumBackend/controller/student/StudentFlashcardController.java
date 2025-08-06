package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.FlashcardSetRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.FlashcardSetResponseDto;
import com.EdumentumBackend.EdumentumBackend.jwt.JwtService;
import com.EdumentumBackend.EdumentumBackend.service.FlashcardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/student/flashcards")
public class StudentFlashcardController {

    private final FlashcardService flashcardService;
    private final JwtService jwtService;

    public StudentFlashcardController(FlashcardService flashcardService, JwtService jwtService) {
        this.flashcardService = flashcardService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<?> createFlashcardSet(
            @Valid @RequestBody FlashcardSetRequestDto flashcardSetRequestDto,
            @RequestHeader("Authorization") String authHeader) throws JsonProcessingException {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of(
                        "status", "error",
                        "error", "Missing or invalid Authorization header"
                ));
            }

            String token = authHeader.substring(7);
            Long userId = jwtService.extractUserId(token);

            FlashcardSetResponseDto createdSet = flashcardService.createFlashcardSet(flashcardSetRequestDto, userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Flashcard set created successfully",
                    "data", createdSet
            ));
        } catch (Exception e) {
            System.err.println("Error in createFlashcardSet: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "error", "Internal server error: " + e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllFlashcardSets(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of(
                        "status", "error",
                        "error", "Missing or invalid Authorization header"
                ));
            }

            String token = authHeader.substring(7);
            Long userId = jwtService.extractUserId(token);

            List<FlashcardSetResponseDto> flashcardSets = flashcardService.getAllFlashcardSets(userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Flashcard sets retrieved successfully",
                    "data", flashcardSets,
                    "total", flashcardSets.size()
            ));
        } catch (Exception e) {
            System.err.println("Error in getAllFlashcardSets: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "error", "Internal server error: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFlashcardSetById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of(
                        "status", "error",
                        "error", "Missing or invalid Authorization header"
                ));
            }

            String token = authHeader.substring(7);
            Long userId = jwtService.extractUserId(token);

            FlashcardSetResponseDto flashcardSet = flashcardService.getFlashcardSetById(id, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Flashcard set retrieved successfully",
                    "data", flashcardSet
            ));
        } catch (Exception e) {
            System.err.println("Error in getFlashcardSetById: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "error", "Internal server error: " + e.getMessage()
            ));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateFlashcardSet(
            @PathVariable Long id,
            @Valid @RequestBody FlashcardSetRequestDto flashcardSetRequestDto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of(
                        "status", "error",
                        "error", "Missing or invalid Authorization header"
                ));
            }

            String token = authHeader.substring(7);
            Long userId = jwtService.extractUserId(token);

            FlashcardSetResponseDto updatedSet = flashcardService.updateFlashcardSet(id, flashcardSetRequestDto, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Flashcard set updated successfully",
                    "data", updatedSet
            ));
        } catch (Exception e) {
            System.err.println("Error in updateFlashcardSet: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "error", "Internal server error: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFlashcardSet(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of(
                        "status", "error",
                        "error", "Missing or invalid Authorization header"
                ));
            }

            String token = authHeader.substring(7);
            Long userId = jwtService.extractUserId(token);

            flashcardService.deleteFlashcardSet(id, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Flashcard set deleted successfully"
            ));
        } catch (Exception e) {
            System.err.println("Error in deleteFlashcardSet: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "error", "Internal server error: " + e.getMessage()
            ));
        }
    }
} 