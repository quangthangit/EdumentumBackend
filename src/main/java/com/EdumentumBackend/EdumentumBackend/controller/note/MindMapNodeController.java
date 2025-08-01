package com.EdumentumBackend.EdumentumBackend.controller.note;

import com.EdumentumBackend.EdumentumBackend.dtos.MindMapNodeRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapNodeResponseDto;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.jwt.JwtService;
import com.EdumentumBackend.EdumentumBackend.service.MindMapNodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mindmap/nodes")
@RequiredArgsConstructor
@Slf4j
public class MindMapNodeController {

    private final MindMapNodeService mindMapNodeService;
    private final JwtService jwtService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MindMapNodeResponseDto> createNode(@RequestBody MindMapNodeRequestDto dto) {
        log.info("Creating node with dto: {}", dto);
        Long userId = getCurrentUserId();
        log.info("User ID from authentication: {}", userId);
        return ResponseEntity.ok(mindMapNodeService.createNode(dto, userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MindMapNodeResponseDto> updateNode(@PathVariable Long id,
            @RequestBody MindMapNodeRequestDto dto) {
        log.info("Updating node {} with dto: {}", id, dto);
        Long userId = getCurrentUserId();
        log.info("User ID from authentication: {}", userId);
        return ResponseEntity.ok(mindMapNodeService.updateNode(id, dto, userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteNode(@PathVariable Long id) {
        log.info("Deleting node: {}", id);
        Long userId = getCurrentUserId();
        log.info("User ID from authentication: {}", userId);
        mindMapNodeService.deleteNode(id, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/only")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteNodeOnly(@PathVariable Long id) {
        log.info("Deleting only node: {} (children will be moved to parent)", id);
        Long userId = getCurrentUserId();
        log.info("User ID from authentication: {}", userId);
        mindMapNodeService.deleteNodeOnly(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tree/{noteId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MindMapNodeResponseDto>> getMindMapTree(@PathVariable Long noteId) {
        log.info("Getting mindmap tree for note: {}", noteId);
        Long userId = getCurrentUserId();
        log.info("User ID from authentication: {}", userId);
        return ResponseEntity.ok(mindMapNodeService.getMindMapTree(noteId, userId));
    }

    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null) {
                log.error("Authentication is null");
                throw new RuntimeException("Authentication is null");
            }

            if (authentication.getPrincipal() == null) {
                log.error("Authentication principal is null");
                throw new RuntimeException("Authentication principal is null");
            }

            log.info("Authentication principal type: {}", authentication.getPrincipal().getClass().getName());

            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                log.info("User details: {}", userDetails);
                return userDetails.getId();
            } else {
                log.error("Authentication principal is not CustomUserDetails: {}", authentication.getPrincipal());
                throw new RuntimeException("Authentication principal is not CustomUserDetails");
            }
        } catch (Exception e) {
            log.error("Error getting userId from authentication", e);
            throw new RuntimeException("Failed to get userId from authentication", e);
        }
    }
}