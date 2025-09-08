package com.EdumentumBackend.EdumentumBackend.controller.user;

import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.service.redis.StudyTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/user/study-time")
@RequiredArgsConstructor
public class StudyTimeController {

    private final StudyTimeService studyTimeService;

    @PostMapping()
    public ResponseEntity<?> ping() {
        Long userId = getCurrentUserId();
        studyTimeService.increaseStudyTime(userId);
        long total = studyTimeService.getStudyTime(userId);
        return ResponseEntity.ok(Map.of(
                "message", "Total time online",
                "status", "susses",
                "data", total
        ));
    }

    @GetMapping()
    public ResponseEntity<?> getStudyTime() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(Map.of(
                "message", "Total time online",
                "status", "susses",
                "data", studyTimeService.getStudyMatrix(userId, 7)
        ));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new RuntimeException("Unauthorized");
        }

        return userDetails.getId();
    }
}
