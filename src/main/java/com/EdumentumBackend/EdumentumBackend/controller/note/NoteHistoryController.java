package com.EdumentumBackend.EdumentumBackend.controller.note;

import com.EdumentumBackend.EdumentumBackend.entity.NoteEntity;
import com.EdumentumBackend.EdumentumBackend.entity.NoteVersionEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.NoteRepository;
import com.EdumentumBackend.EdumentumBackend.repository.NoteVersionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notes")
public class NoteHistoryController {

    private final NoteRepository noteRepository;
    private final NoteVersionRepository versionRepository;

    public NoteHistoryController(NoteRepository noteRepository, NoteVersionRepository versionRepository) {
        this.noteRepository = noteRepository;
        this.versionRepository = versionRepository;
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<NoteVersionEntity>> history(@PathVariable Long id) {
        NoteEntity note = noteRepository.findActiveById(id).orElseThrow(() -> new NotFoundException("Note not found"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) throw new BadRequestException("Unauthenticated");
        return ResponseEntity.ok(versionRepository.findByNoteOrderByCreatedAtDesc(note));
    }

    @PostMapping("/{id}/restore/{versionId}")
    public ResponseEntity<Void> restore(@PathVariable Long id, @PathVariable Long versionId) {
        noteRepository.findActiveById(id).orElseThrow(() -> new NotFoundException("Note not found"));
        // Business: applying diff to blocks is out of scope here (requires CRDT/OT replay). Placeholder.
        return ResponseEntity.noContent().build();
    }
}


