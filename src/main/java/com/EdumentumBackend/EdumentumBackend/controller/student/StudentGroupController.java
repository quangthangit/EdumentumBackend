package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupDetailResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupResponseDto;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.service.GroupService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupById(
            @PathVariable Long id
    ) {
        try {
            Long userId = getCurrentUserId();

            GroupDetailResponse groupDetailResponse = groupService.findGroupById(id, userId);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Get Group Detail successfully",
                    "data", groupDetailResponse
            ));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @GetMapping("/public")
    public ResponseEntity<?> getPublicGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Long userId = getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);

        var result = groupService.findAllPublicGroups(userId, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Group updated successfully");
        response.put("data", result.getData());
        response.put("pagination", result.getPagination());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<?> joinGroup(@PathVariable Long groupId) throws BadRequestException {
        Long userId = getCurrentUserId();
        groupService.joinGroup(groupId, userId);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Join Group successfully"
        ));
    }

    @GetMapping("/my-group")
    public ResponseEntity<?> getMyGroupByIdUser() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Get My Group successfully",
                "data", groupService.findByUEntities(userId)
        ));
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
