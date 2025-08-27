package com.EdumentumBackend.EdumentumBackend.controller.note;

import com.EdumentumBackend.EdumentumBackend.dtos.PaginatedResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.note.NoteResponseDto;
import com.EdumentumBackend.EdumentumBackend.service.NoteService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/search")
public class NoteSearchController {

    private final NoteService noteService;

    public NoteSearchController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/notes")
    public ResponseEntity<PaginatedResponse<NoteResponseDto>> search(@RequestParam String query,
                                                                     @RequestParam(required = false) String tag,
                                                                     @RequestParam(required = false) Long ownerId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(noteService.listNotes(pageable, query, ownerId, tag));
    }
}


