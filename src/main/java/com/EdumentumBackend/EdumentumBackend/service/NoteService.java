package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.PaginatedResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.note.*;
import org.springframework.data.domain.Pageable;

public interface NoteService {
    PaginatedResponse<NoteResponseDto> listNotes(Pageable pageable, String query, Long ownerId, String tag);
    NoteResponseDto getNote(Long id);
    NoteResponseDto createNote(NoteRequestDto dto);
    NoteResponseDto updateNote(Long id, NoteRequestDto dto);
    void deleteNote(Long id);

    BlockResponseDto addBlock(Long noteId, BlockRequestDto dto);
    BlockResponseDto updateBlock(Long blockId, BlockRequestDto dto);
    void deleteBlock(Long blockId);
    void reorderBlocks(ReorderBlocksRequestDto dto);

    CollaboratorResponseDto addCollaborator(Long noteId, CollaboratorRequestDto dto);
    void removeCollaborator(Long noteId, Long userId);

    CommentResponseDto addComment(Long noteId, CommentRequestDto dto);
    void deleteComment(Long commentId);
}



