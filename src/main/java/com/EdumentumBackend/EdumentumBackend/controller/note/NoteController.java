package com.EdumentumBackend.EdumentumBackend.controller.note;

import com.EdumentumBackend.EdumentumBackend.dtos.PaginatedResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.note.*;
import com.EdumentumBackend.EdumentumBackend.entity.NoteCollaboratorEntity;
import com.EdumentumBackend.EdumentumBackend.entity.NoteCommentEntity;
import com.EdumentumBackend.EdumentumBackend.entity.NoteEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.enums.NotePermission;
import com.EdumentumBackend.EdumentumBackend.exception.BadRequestException;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.NoteCollaboratorRepository;
import com.EdumentumBackend.EdumentumBackend.repository.NoteCommentRepository;
import com.EdumentumBackend.EdumentumBackend.repository.NoteRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/notes")
public class NoteController {

    private final NoteService noteService;
    private final NoteRepository noteRepository;
    private final NoteCommentRepository commentRepository;
    private final NoteCollaboratorRepository collaboratorRepository;
    private final UserRepository userRepository;

    public NoteController(NoteService noteService, NoteRepository noteRepository, NoteCommentRepository commentRepository, NoteCollaboratorRepository collaboratorRepository, UserRepository userRepository) {
        this.noteService = noteService;
        this.noteRepository = noteRepository;
        this.commentRepository = commentRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<NoteResponseDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) String tag
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(noteService.listNotes(pageable, query, ownerId, tag));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(noteService.getNote(id));
    }

    @PostMapping
    public ResponseEntity<NoteResponseDto> create(@Valid @RequestBody NoteRequestDto dto) {
        return ResponseEntity.ok(noteService.createNote(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseDto> update(@PathVariable Long id, @Valid @RequestBody NoteRequestDto dto) {
        return ResponseEntity.ok(noteService.updateNote(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/blocks")
    public ResponseEntity<BlockResponseDto> addBlock(@PathVariable Long id, @Valid @RequestBody BlockRequestDto dto) {
        return ResponseEntity.ok(noteService.addBlock(id, dto));
    }

    @PutMapping("/blocks/{blockId}")
    public ResponseEntity<BlockResponseDto> updateBlock(@PathVariable Long blockId, @Valid @RequestBody BlockRequestDto dto) {
        return ResponseEntity.ok(noteService.updateBlock(blockId, dto));
    }

    @DeleteMapping("/blocks/{blockId}")
    public ResponseEntity<Void> deleteBlock(@PathVariable Long blockId) {
        noteService.deleteBlock(blockId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/blocks/reorder")
    public ResponseEntity<Void> reorder(@Valid @RequestBody ReorderBlocksRequestDto dto) {
        noteService.reorderBlocks(dto);
        return ResponseEntity.noContent().build();
    }

    // Collaborators list could be implemented later if needed as public DTO list

    @PostMapping("/{id}/collaborators")
    public ResponseEntity<CollaboratorResponseDto> addCollaborator(@PathVariable Long id, @Valid @RequestBody CollaboratorRequestDto dto) {
        return ResponseEntity.ok(noteService.addCollaborator(id, dto));
    }

    @DeleteMapping("/{id}/collaborators/{userId}")
    public ResponseEntity<Void> removeCollaborator(@PathVariable Long id, @PathVariable Long userId) {
        noteService.removeCollaborator(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponseDto> addComment(@PathVariable Long id, @Valid @RequestBody CommentRequestDto dto) {
        return ResponseEntity.ok(noteService.addComment(id, dto));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<?> listComments(@PathVariable Long id) {
        NoteEntity note = noteRepository.findActiveById(id).orElseThrow(() -> new NotFoundException("Note not found"));
        // Permission: owner or collaborator (any permission)
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity current = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
        boolean allowed = note.getOwner().getUserId().equals(current.getUserId()) ||
                collaboratorRepository.existsByNoteAndUserAndPermissionIn(note, current, java.util.List.of(NotePermission.OWNER, NotePermission.EDITOR, NotePermission.VIEWER));
        if (!allowed) throw new BadRequestException("No permission");
        return ResponseEntity.ok(commentRepository.findByNoteAndIsDeletedFalseOrderByCreatedAtAsc(note));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        noteService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

}


