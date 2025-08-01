package com.EdumentumBackend.EdumentumBackend.controller.note;

import com.EdumentumBackend.EdumentumBackend.dtos.NoteRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.NoteResponseDto;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.jwt.JwtService;
import com.EdumentumBackend.EdumentumBackend.service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
@Slf4j
public class NoteController {

    private final NoteService noteService;
    private final JwtService jwtService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NoteResponseDto> createNote(@RequestBody NoteRequestDto dto) {
        log.info("Creating note with dto: {}", dto);
        Long userId = getCurrentUserId();
        log.info("User ID from authentication: {}", userId);
        return ResponseEntity.ok(noteService.createNote(dto, userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NoteResponseDto> updateNote(@PathVariable Long id,
            @RequestBody NoteRequestDto dto) {
        log.info("Updating note {} with dto: {}", id, dto);
        Long userId = getCurrentUserId();
        log.info("User ID from authentication: {}", userId);
        return ResponseEntity.ok(noteService.updateNote(id, dto, userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        log.info("Deleting note: {}", id);
        Long userId = getCurrentUserId();
        log.info("User ID from authentication: {}", userId);
        noteService.deleteNote(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NoteResponseDto> getNoteById(@PathVariable Long id) {
        log.info("Getting note by ID: {}", id);
        Long userId = getCurrentUserId();
        log.info("User ID from authentication: {}", userId);
        return ResponseEntity.ok(noteService.getNoteById(id, userId));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NoteResponseDto>> getNotesByUser() {
        log.info("Getting notes for user");
        Long userId = getCurrentUserId();
        log.info("User ID from authentication: {}", userId);
        return ResponseEntity.ok(noteService.getNotesByUserId(userId));
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