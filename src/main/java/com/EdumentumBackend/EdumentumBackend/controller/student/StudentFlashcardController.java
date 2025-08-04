package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.FlashcardSetDto;
import com.EdumentumBackend.EdumentumBackend.jwt.JwtService;
import com.EdumentumBackend.EdumentumBackend.service.FlashcardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
            @Valid @RequestBody FlashcardSetDto flashcardSetDto,
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

            // Debug logging
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("User: " + authentication.getName());
            System.out.println("Authorities: " + authentication.getAuthorities());
            System.out.println("UserId from token: " + userId);
            System.out.println("Request body: " + flashcardSetDto);

            FlashcardSetDto createdSet = flashcardService.createFlashcardSet(flashcardSetDto, userId);

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
} 