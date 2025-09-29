package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.flashcard.FlashcardSetRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.flashcard.FlashcardSetResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.common.ApiResponse;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.service.FlashcardService;
import com.EdumentumBackend.EdumentumBackend.service.PermissionService;
import com.EdumentumBackend.EdumentumBackend.repository.UsageTrackingRepository;
import com.EdumentumBackend.EdumentumBackend.repository.FeatureRepository;
import com.EdumentumBackend.EdumentumBackend.enums.SubscriptionPlan;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/student/flashcards")
public class StudentFlashcardController {

    private final FlashcardService flashcardService;
    private final PermissionService permissionService;
    private final UsageTrackingRepository usageTrackingRepository;
    private final FeatureRepository featureRepository;

    public StudentFlashcardController(FlashcardService flashcardService, PermissionService permissionService, UsageTrackingRepository usageTrackingRepository, FeatureRepository featureRepository) {
        this.flashcardService = flashcardService;
        this.permissionService = permissionService;
        this.usageTrackingRepository = usageTrackingRepository;
        this.featureRepository = featureRepository;
    }

    @PostMapping
    public ResponseEntity<?> createFlashcardSet(@Valid @RequestBody FlashcardSetRequestDto flashcardSetRequestDto) {
        try {
            Long userId = getCurrentUserId();

            // Check if user has permission to create flashcards
            if (!permissionService.canUseFeature(userId, "CREATE_FLASHCARD")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "status", "error",
                        "message", "You don't have permission to create flashcard sets. Please upgrade your plan."
                ));
            }

            FlashcardSetResponseDto createdSet = flashcardService.createFlashcardSet(flashcardSetRequestDto, userId);

            // Track usage for the CREATE_FLASHCARD feature
            permissionService.incrementUsage(userId, "CREATE_FLASHCARD");

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
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy
    ) {
        try {
            Long userId = getCurrentUserId();
            Pageable pageable = PageRequest.of(page, size);

            var result = flashcardService.getAllFlashcardSets(userId, pageable, search, sortBy);

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
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy
    ) {
        try {
            // Get userId if user is logged in (optional)
            Long userId = null;
            try {
                userId = getCurrentUserId();
            } catch (Exception e) {
                // User is not logged in, set userId = null
            }

            Pageable pageable = PageRequest.of(page, size);

            var result = flashcardService.getPublicFlashcardSets(pageable, search, sortBy, userId);

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

    @GetMapping("/limit")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFlashcardLimit() {
        try {
            Long userId = getCurrentUserId();

            // Get the CREATE_FLASHCARD feature
            var featureOpt = featureRepository.findByFeatureKey("CREATE_FLASHCARD");
            if (featureOpt.isEmpty()) {
                throw new RuntimeException("CREATE_FLASHCARD feature not found");
            }

            Long featureId = featureOpt.get().getId();

            LocalDateTime weekStart = LocalDateTime.now()
                .with(java.time.DayOfWeek.MONDAY)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

            // Get usage count for this week
            Integer usageCount = usageTrackingRepository.getWeeklyUsageCount(userId, featureId, weekStart);

            SubscriptionPlan userPlan = permissionService.getUserPlan(userId);
            int weeklyLimit;
            boolean canCreateFlashcard;
            if (userPlan == SubscriptionPlan.PRO_MONTHLY || userPlan == SubscriptionPlan.PRO_YEARLY) {
                weeklyLimit = 1000;
                canCreateFlashcard = true;
            } else {
                weeklyLimit = 3;
                canCreateFlashcard = usageCount < weeklyLimit;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("canCreateFlashcard", canCreateFlashcard);
            result.put("flashcardSetsCreatedThisWeek", usageCount);
            result.put("weeklyLimit", weeklyLimit);

            return ResponseEntity.ok(ApiResponse.success(result, "Flashcard limit information retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve flashcard limit: " + e.getMessage(), 500));
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
