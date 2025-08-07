package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.FlashcardSetRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.FlashcardSetResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.GroupRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.GroupResponseDto;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/student/groups")
public class StudentGroupController {

    private final GroupService groupService;

    public StudentGroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<?> createGroup(@Valid @RequestBody GroupRequestDto groupRequestDto) {
        try {
            Long userId = getCurrentUserId();

            GroupResponseDto groupResponseDto = groupService.createGroup(groupRequestDto, userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "Group created successfully",
                    "data", groupResponseDto
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateGroup(
            @PathVariable Long id,
            @Valid @RequestBody GroupRequestDto groupRequestDto
    ) {
        try {
            Long userId = getCurrentUserId();

            GroupResponseDto groupResponseDto = groupService.updateGroup(groupRequestDto, id, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Group updated successfully",
                    "data", groupResponseDto
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
