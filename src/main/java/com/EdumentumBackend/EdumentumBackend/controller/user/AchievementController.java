package com.EdumentumBackend.EdumentumBackend.controller.user;

import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.service.AchievementService;
import com.EdumentumBackend.EdumentumBackend.service.redis.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/api/v1/user/achievement")
@RequiredArgsConstructor
public class AchievementController {
    private final AchievementService achievementService;

//    @GetMapping()
//    public ResponseEntity<?> getAllAchievement() {
//        try {
//            return ResponseEntity.ok(Map.of(
//                    "message", "Get all Achievement successfully",
//                    "status" , "susses",
//                    "data" ,  achievementService.findAll()
//            ));
//        } catch (Exception e) {
//            return buildServerError(e);
//        }
//    }

    @GetMapping()
    public ResponseEntity<?> getAllAchievementByUserId() {
        try {
            Long userId = getCurrentUserId();
            return ResponseEntity.ok(Map.of(
                    "message", "Get all Achievement successfully",
                    "status" , "susses",
                    "data" ,  achievementService.findAll(userId)
            ));
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
