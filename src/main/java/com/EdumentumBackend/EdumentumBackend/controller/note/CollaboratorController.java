package com.EdumentumBackend.EdumentumBackend.controller.note;

import com.EdumentumBackend.EdumentumBackend.dtos.note.CollaboratorResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.NoteEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.enums.NotePermission;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.NoteCollaboratorRepository;
import com.EdumentumBackend.EdumentumBackend.repository.NoteRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notes")
public class CollaboratorController {

    private final NoteRepository noteRepository;
    private final NoteCollaboratorRepository collaboratorRepository;
    private final UserRepository userRepository;

    public CollaboratorController(NoteRepository noteRepository, NoteCollaboratorRepository collaboratorRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}/collaborators")
    public ResponseEntity<List<CollaboratorResponseDto>> list(@PathVariable Long id) {
        NoteEntity note = noteRepository.findActiveById(id).orElseThrow(() -> new NotFoundException("Note not found"));
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity current = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        if (!note.getOwner().getUserId().equals(current.getUserId())) {
            return ResponseEntity.status(403).build();
        }
        List<CollaboratorResponseDto> list = collaboratorRepository.findByNote(note).stream()
                .map(c -> CollaboratorResponseDto.builder().userId(c.getUser().getUserId()).permission(c.getPermission()).build())
                .toList();
        return ResponseEntity.ok(list);
    }
}


