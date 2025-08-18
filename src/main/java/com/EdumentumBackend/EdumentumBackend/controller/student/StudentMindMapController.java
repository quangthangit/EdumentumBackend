package com.EdumentumBackend.EdumentumBackend.controller.student;

import com.EdumentumBackend.EdumentumBackend.dtos.mindmap.MindMapFileResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.mindmap.MindMapFileRequestDto;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.service.MindMapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.EdumentumBackend.EdumentumBackend.enums.MindMapType;

@RestController
@RequestMapping("/api/v1/student/mindmaps")
@RequiredArgsConstructor
@Slf4j
public class StudentMindMapController {

    private final MindMapService mindMapService;

    @GetMapping("/files")
    public ResponseEntity<?> getFiles() {
        try {
            Long userId = getCurrentUserId();
            List<MindMapFileResponseDto> files = mindMapService.getFilesByUserId(userId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Files retrieved successfully",
                    "data", files));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @PostMapping("/files")
    public ResponseEntity<?> createFile(@Valid @RequestBody MindMapFileRequestDto mindMapFileRequestDto) {
        try {
            Long userId = getCurrentUserId();
            MindMapFileResponseDto createdFile = mindMapService.createFile(mindMapFileRequestDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "status", "success",
                    "message", "File created successfully",
                    "data", createdFile));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @PutMapping("/files/{id}")
    public ResponseEntity<?> updateFile(@PathVariable String id,
            @Valid @RequestBody MindMapFileRequestDto mindMapFileRequestDto) {
        try {
            Long userId = getCurrentUserId();
            MindMapFileResponseDto updatedFile = mindMapService.updateFile(id, mindMapFileRequestDto, userId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "File updated successfully",
                    "data", updatedFile));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @DeleteMapping("/files/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable String id) {
        try {
            Long userId = getCurrentUserId();
            mindMapService.deleteFile(id, userId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "File deleted successfully"));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<?> getFileById(@PathVariable String id) {
        try {
            Long userId = getCurrentUserId();
            MindMapFileResponseDto file = mindMapService.getFileById(id, userId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "File retrieved successfully",
                    "data", file));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @PutMapping("/files/{id}/name")
    public ResponseEntity<?> updateFileName(@PathVariable String id, @RequestBody Map<String, String> request) {
        try {
            Long userId = getCurrentUserId();
            String newName = request.get("name");
            if (newName == null || newName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "error", "File name is required"));
            }
            MindMapFileResponseDto updatedFile = mindMapService.updateFileName(id, newName, userId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "File name updated successfully",
                    "data", updatedFile));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    @GetMapping("/files/type/{type}")
    public ResponseEntity<?> getFilesByType(@PathVariable String type) {
        try {
            Long userId = getCurrentUserId();
            MindMapType mindMapType = MindMapType.valueOf(type.toUpperCase());
            List<MindMapFileResponseDto> files = mindMapService.getFilesByUserIdAndType(userId, mindMapType);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Files retrieved successfully",
                    "data", files));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "error",
                    "Invalid mind map type. Valid types: STUDY_NOTES, PROJECT_PLANNING, CONCEPT_MAPPING, BRAINSTORMING, LESSON_PLAN, RESEARCH, PRESENTATION, PERSONAL"));
        } catch (Exception e) {
            return buildServerError(e);
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUserId();
        }
        throw new RuntimeException("User not authenticated");
    }

    private ResponseEntity<?> buildServerError(Exception e) {
        log.error("Error in MindMap operation: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "error", e.getMessage()));
    }
}
