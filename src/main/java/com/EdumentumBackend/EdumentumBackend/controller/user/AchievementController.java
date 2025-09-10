package com.EdumentumBackend.EdumentumBackend.controller.user;

import com.EdumentumBackend.EdumentumBackend.enums.Rarity;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.service.AchievementService;
import com.EdumentumBackend.EdumentumBackend.service.redis.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/user/achievement")
@RequiredArgsConstructor
public class AchievementController {
    private final AchievementService achievementService;

    @GetMapping()
    public ResponseEntity<?> getAllAchievementByUserId(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Rarity rarity,
            @RequestParam(required = false) Boolean achieved,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        try {
            Long userId = getCurrentUserId();
            Pageable pageable = PageRequest.of(page, size);
            var result = achievementService.findAll(userId, keyword, rarity, achieved, pageable);

            HashMap<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Group updated successfully");
            response.put("data", result.getData());
            response.put("pagination", result.getPagination());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

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
