package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.PaginatedResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.note.*;
import com.EdumentumBackend.EdumentumBackend.entity.*;
import com.EdumentumBackend.EdumentumBackend.enums.NoteAction;
import com.EdumentumBackend.EdumentumBackend.enums.NotePermission;
import com.EdumentumBackend.EdumentumBackend.exception.BadRequestException;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.*;
import com.EdumentumBackend.EdumentumBackend.service.NoteService;
import jakarta.transaction.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final NoteBlockRepository noteBlockRepository;
    private final NoteCollaboratorRepository collaboratorRepository;
    private final NoteCommentRepository commentRepository;
    private final NoteTagRepository noteTagRepository;
    private final UserRepository userRepository;
    private final NoteVersionRepository noteVersionRepository;
    private final ObjectMapper objectMapper;

    public NoteServiceImpl(NoteRepository noteRepository,
                           NoteBlockRepository noteBlockRepository,
                           NoteCollaboratorRepository collaboratorRepository,
                           NoteCommentRepository commentRepository,
                           NoteTagRepository noteTagRepository,
                           UserRepository userRepository,
                           NoteVersionRepository noteVersionRepository,
                           ObjectMapper objectMapper) {
        this.noteRepository = noteRepository;
        this.noteBlockRepository = noteBlockRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.commentRepository = commentRepository;
        this.noteTagRepository = noteTagRepository;
        this.userRepository = userRepository;
        this.noteVersionRepository = noteVersionRepository;
        this.objectMapper = objectMapper;
    }

    private UserEntity getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) throw new BadRequestException("Unauthenticated");
        return userRepository.findByEmail(auth.getName()).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private boolean hasEditPermission(NoteEntity note, UserEntity user) {
        if (Objects.equals(note.getOwner().getUserId(), user.getUserId())) return true;
        return collaboratorRepository.existsByNoteAndUserAndPermissionIn(note, user, List.of(NotePermission.OWNER, NotePermission.EDITOR));
    }

    private boolean hasViewPermission(NoteEntity note, UserEntity user) {
        if (Objects.equals(note.getOwner().getUserId(), user.getUserId())) return true;
        return collaboratorRepository.existsByNoteAndUserAndPermissionIn(note, user, List.of(NotePermission.OWNER, NotePermission.EDITOR, NotePermission.VIEWER));
    }

    private NoteResponseDto mapNote(NoteEntity note) {
        List<BlockResponseDto> blocks = noteBlockRepository.findByNoteAndIsDeletedFalseOrderByOrderIndexAsc(note).stream()
                .map(b -> BlockResponseDto.builder()
                        .id(b.getId())
                        .type(b.getType())
                        .orderIndex(b.getOrderIndex())
                        .content(b.getContent())
                        .build())
                .collect(Collectors.toList());
        List<String> tags = noteTagRepository.findByNote(note).stream()
                .map(NoteTagEntity::getTagName)
                .toList();
        return NoteResponseDto.builder()
                .id(note.getId())
                .title(note.getTitle())
                .ownerId(note.getOwner().getUserId())
                .isDeleted(note.getIsDeleted())
                .blocks(blocks)
                .tags(tags)
                .build();
    }

    @Override
    public PaginatedResponse<NoteResponseDto> listNotes(Pageable pageable, String query, Long ownerId, String tag) {
        UserEntity current = getCurrentUser();
        System.out.println("DEBUG: Current user: " + current.getEmail() + " (ID: " + current.getUserId() + ")");
        System.out.println("DEBUG: Query params - query: '" + query + "', ownerId: " + ownerId + ", tag: '" + tag + "'");

        // Normalize empty strings to null
        String normalizedQuery = (query != null && query.trim().isEmpty()) ? null : query;
        String normalizedTag = (tag != null && tag.trim().isEmpty()) ? null : tag;

        Page<NoteEntity> page;
        Pageable usePageable = pageable == null ? PageRequest.of(0, 20) : pageable;
        if (normalizedQuery != null || ownerId != null || normalizedTag != null) {
            System.out.println("DEBUG: Using searchAccessibleNotes with normalized params");
            page = noteRepository.searchAccessibleNotes(current, normalizedQuery, ownerId, normalizedTag, usePageable);
        } else {
            System.out.println("DEBUG: Using findAccessibleNotes");
            page = noteRepository.findAccessibleNotes(current, usePageable);
        }

        System.out.println("DEBUG: Found " + page.getTotalElements() + " notes, page content size: " + page.getContent().size());
        for (NoteEntity note : page.getContent()) {
            System.out.println("DEBUG: Note - ID: " + note.getId() + ", Title: '" + note.getTitle() + "', Owner: " + note.getOwner().getUserId() + ", isDeleted: " + note.getIsDeleted());
        }

        return PaginatedResponse.fromPage(page.map(this::mapNote));
    }

    @Override
    public NoteResponseDto getNote(Long id) {
        UserEntity current = getCurrentUser();
        NoteEntity note = noteRepository.findActiveById(id).orElseThrow(() -> new NotFoundException("Note not found"));
        if (!hasViewPermission(note, current)) throw new BadRequestException("No permission");
        return mapNote(note);
    }

    @Transactional
    @Override
    public NoteResponseDto createNote(NoteRequestDto dto) {
        UserEntity current = getCurrentUser();
        NoteEntity note = NoteEntity.builder()
                .title(dto.getTitle())
                .owner(current)
                .isDeleted(false)
                .build();
        note = noteRepository.save(note);
        saveVersion(note, current, NoteAction.CREATE_NOTE, "{}");

        if (dto.getTags() != null) {
            for (String tagName : dto.getTags()) {
                if (!noteTagRepository.existsByNoteAndTagName(note, tagName)) {
                    noteTagRepository.save(NoteTagEntity.builder()
                            .note(note)
                            .tagName(tagName)
                            .build());
                }
            }
        }

        return mapNote(note);
    }

    @Transactional
    @Override
    public NoteResponseDto updateNote(Long id, NoteRequestDto dto) {
        UserEntity current = getCurrentUser();
        NoteEntity note = noteRepository.findActiveById(id).orElseThrow(() -> new NotFoundException("Note not found"));
        if (!hasEditPermission(note, current)) throw new BadRequestException("No permission");
        note.setTitle(dto.getTitle());
        note = noteRepository.save(note);
        saveVersion(note, current, NoteAction.UPDATE_NOTE, "{}");

        if (dto.getTags() != null) {
            List<NoteTagEntity> existing = noteTagRepository.findByNote(note);
            for (NoteTagEntity e : new ArrayList<>(existing)) {
                if (!dto.getTags().contains(e.getTagName())) {
                    noteTagRepository.delete(e);
                }
            }
            for (String tagName : dto.getTags()) {
                if (!noteTagRepository.existsByNoteAndTagName(note, tagName)) {
                    noteTagRepository.save(NoteTagEntity.builder()
                            .note(note)
                            .tagName(tagName)
                            .build());
                }
            }
        }
        return mapNote(note);
    }

    @Transactional
    @Override
    public void deleteNote(Long id) {
        UserEntity current = getCurrentUser();
        NoteEntity note = noteRepository.findActiveById(id).orElseThrow(() -> new NotFoundException("Note not found"));
        if (!Objects.equals(note.getOwner().getUserId(), current.getUserId())) throw new BadRequestException("Only owner can delete");
        note.setIsDeleted(true);
        noteRepository.save(note);
        saveVersion(note, current, NoteAction.DELETE_NOTE, "{}");
    }

    @Transactional
    @Override
    public BlockResponseDto addBlock(Long noteId, BlockRequestDto dto) {
        UserEntity current = getCurrentUser();
        NoteEntity note = noteRepository.findActiveById(noteId).orElseThrow(() -> new NotFoundException("Note not found"));
        if (!hasEditPermission(note, current)) throw new BadRequestException("No permission");

        NoteBlockEntity block = NoteBlockEntity.builder()
                .note(note)
                .type(dto.getType())
                .content(dto.getContent())
                .orderIndex(dto.getOrderIndex())
                .isDeleted(false)
                .build();
        block = noteBlockRepository.save(block);
        saveVersion(note, current, NoteAction.CREATE_BLOCK, "{\"blockId\":" + block.getId() + "}");
        return BlockResponseDto.builder()
                .id(block.getId())
                .type(block.getType())
                .orderIndex(block.getOrderIndex())
                .content(block.getContent())
                .build();
    }

    @Transactional
    @Override
    public BlockResponseDto updateBlock(Long blockId, BlockRequestDto dto) {
        UserEntity current = getCurrentUser();
        NoteBlockEntity block = noteBlockRepository.findById(blockId).orElseThrow(() -> new NotFoundException("Block not found"));
        if (!hasEditPermission(block.getNote(), current)) throw new BadRequestException("No permission");
        block.setType(dto.getType());
        block.setContent(dto.getContent());
        block.setOrderIndex(dto.getOrderIndex());
        block = noteBlockRepository.save(block);
        saveVersion(block.getNote(), current, NoteAction.UPDATE_BLOCK, "{\"blockId\":" + block.getId() + "}");
        return BlockResponseDto.builder()
                .id(block.getId())
                .type(block.getType())
                .orderIndex(block.getOrderIndex())
                .content(block.getContent())
                .build();
    }

    @Transactional
    @Override
    public void deleteBlock(Long blockId) {
        UserEntity current = getCurrentUser();
        NoteBlockEntity block = noteBlockRepository.findById(blockId).orElseThrow(() -> new NotFoundException("Block not found"));
        if (!hasEditPermission(block.getNote(), current)) throw new BadRequestException("No permission");
        block.setIsDeleted(true);
        noteBlockRepository.save(block);
        saveVersion(block.getNote(), current, NoteAction.DELETE_BLOCK, "{\"blockId\":" + block.getId() + "}");
    }

    @Transactional
    @Override
    public void reorderBlocks(ReorderBlocksRequestDto dto) {
        UserEntity current = getCurrentUser();
        NoteEntity note = noteRepository.findActiveById(dto.getNoteId()).orElseThrow(() -> new NotFoundException("Note not found"));
        if (!hasEditPermission(note, current)) throw new BadRequestException("No permission");
        List<NoteBlockEntity> blocks = noteBlockRepository.findByNoteAndIsDeletedFalseOrderByOrderIndexAsc(note);
        for (int i = 0; i < dto.getOrderedBlockIds().size(); i++) {
            Long id = dto.getOrderedBlockIds().get(i);
            for (NoteBlockEntity b : blocks) {
                if (Objects.equals(b.getId(), id)) {
                    b.setOrderIndex(i);
                }
            }
        }
        noteBlockRepository.saveAll(blocks);
        saveVersion(note, current, NoteAction.REORDER_BLOCKS, "{}");
    }

    private void saveVersion(NoteEntity note, UserEntity user, NoteAction action, String diffJson) {
        try {
            NoteVersionEntity version = NoteVersionEntity.builder()
                    .note(note)
                    .user(user)
                    .action(action)
                    .diff(objectMapper.readTree(diffJson))
                    .build();
            noteVersionRepository.save(version);
        } catch (Exception e) {
            throw new BadRequestException("Invalid diff JSON");
        }
    }

    @Transactional
    @Override
    public CollaboratorResponseDto addCollaborator(Long noteId, CollaboratorRequestDto dto) {
        UserEntity current = getCurrentUser();
        NoteEntity note = noteRepository.findActiveById(noteId).orElseThrow(() -> new NotFoundException("Note not found"));
        if (!Objects.equals(note.getOwner().getUserId(), current.getUserId())) throw new BadRequestException("Only owner can share");
        UserEntity target = userRepository.findById(dto.getUserId()).orElseThrow(() -> new NotFoundException("User not found"));
        NoteCollaboratorEntity collab = collaboratorRepository.findByNoteAndUser(note, target)
                .orElse(NoteCollaboratorEntity.builder().note(note).user(target).build());
        collab.setPermission(dto.getPermission());
        collaboratorRepository.save(collab);
        saveVersion(note, current, NoteAction.ADD_COLLABORATOR, "{\"userId\":" + target.getUserId() + ",\"permission\":\"" + collab.getPermission() + "\"}");
        return CollaboratorResponseDto.builder().userId(target.getUserId()).permission(collab.getPermission()).build();
    }

    @Transactional
    @Override
    public void removeCollaborator(Long noteId, Long userId) {
        UserEntity current = getCurrentUser();
        NoteEntity note = noteRepository.findActiveById(noteId).orElseThrow(() -> new NotFoundException("Note not found"));
        if (!Objects.equals(note.getOwner().getUserId(), current.getUserId())) throw new BadRequestException("Only owner can remove");
        UserEntity target = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        collaboratorRepository.findByNoteAndUser(note, target).ifPresent(c -> {
            collaboratorRepository.delete(c);
            saveVersion(note, current, NoteAction.REMOVE_COLLABORATOR, "{\"userId\":" + target.getUserId() + "}");
        });
    }

    @Transactional
    @Override
    public CommentResponseDto addComment(Long noteId, CommentRequestDto dto) {
        UserEntity current = getCurrentUser();
        NoteEntity note = noteRepository.findActiveById(noteId).orElseThrow(() -> new NotFoundException("Note not found"));
        if (!hasViewPermission(note, current)) throw new BadRequestException("No permission");
        NoteBlockEntity block = null;
        if (dto.getBlockId() != null) {
            block = noteBlockRepository.findById(dto.getBlockId()).orElseThrow(() -> new NotFoundException("Block not found"));
            if (!Objects.equals(block.getNote().getId(), note.getId())) throw new BadRequestException("Invalid block");
        }
        NoteCommentEntity parent = null;
        if (dto.getParentId() != null) {
            parent = commentRepository.findById(dto.getParentId()).orElseThrow(() -> new NotFoundException("Parent comment not found"));
            if (!Objects.equals(parent.getNote().getId(), note.getId())) throw new BadRequestException("Invalid parent");
        }
        NoteCommentEntity comment = NoteCommentEntity.builder()
                .note(note)
                .block(block)
                .parent(parent)
                .user(current)
                .content(dto.getContent())
                .isDeleted(false)
                .build();
        comment = commentRepository.save(comment);
        saveVersion(note, current, NoteAction.ADD_COMMENT, "{\"commentId\":" + comment.getId() + "}");
        return CommentResponseDto.builder()
                .id(comment.getId())
                .userId(current.getUserId())
                .blockId(block == null ? null : block.getId())
                .content(comment.getContent())
                .build();
    }

    @Transactional
    @Override
    public void deleteComment(Long commentId) {
        UserEntity current = getCurrentUser();
        NoteCommentEntity comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found"));
        if (!Objects.equals(comment.getUser().getUserId(), current.getUserId()) && !Objects.equals(comment.getNote().getOwner().getUserId(), current.getUserId())) {
            throw new BadRequestException("No permission");
        }
        comment.setIsDeleted(true);
        commentRepository.save(comment);
        saveVersion(comment.getNote(), current, NoteAction.DELETE_COMMENT, "{\"commentId\":" + comment.getId() + "}");
    }
}
