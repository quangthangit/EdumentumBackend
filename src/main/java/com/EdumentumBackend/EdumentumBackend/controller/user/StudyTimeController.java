package com.EdumentumBackend.EdumentumBackend.controller.user;

import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.service.redis.StudyTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/api/v1/user/ping")
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
                "status" , "susses",
                "data" ,  total
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
