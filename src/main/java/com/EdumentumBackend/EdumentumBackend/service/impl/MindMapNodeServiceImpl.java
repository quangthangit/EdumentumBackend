package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.MindMapNodeRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapNodeResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.MindMapNodeEntity;
import com.EdumentumBackend.EdumentumBackend.entity.NoteEntity;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.MindMapNodeRepository;
import com.EdumentumBackend.EdumentumBackend.repository.NoteRepository;
import com.EdumentumBackend.EdumentumBackend.service.MindMapNodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MindMapNodeServiceImpl implements MindMapNodeService {

    private final MindMapNodeRepository nodeRepository;
    private final NoteRepository noteRepository;
    @Override
    public MindMapNodeResponseDto createNode(MindMapNodeRequestDto dto, Long userId) {
        NoteEntity note = noteRepository.findById(dto.getNoteId())
                .orElseThrow(() -> new NotFoundException("Note not found"));

        // Check if user owns this note
        if (!note.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("Note not found or access denied");
        }

        MindMapNodeEntity parent = null;
        if (dto.getParentId() != null) {
            parent = nodeRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent node not found"));

            // Check if parent node belongs to the same note
            if (!parent.getNote().getId().equals(dto.getNoteId())) {
                throw new NotFoundException("Parent node does not belong to the specified note");
            }
        }

        MindMapNodeEntity node = MindMapNodeEntity.builder()
                .text(dto.getText())
                .position(dto.getPosition())
                .parent(parent)
                .note(note)
                .build();

        MindMapNodeEntity saved = nodeRepository.save(node);
        return toDto(saved);
    }

    @Override
    public MindMapNodeResponseDto updateNode(Long id, MindMapNodeRequestDto dto, Long userId) {
        MindMapNodeEntity node = nodeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Node not found"));

        // Check if user owns the note that contains this node
        if (!node.getNote().getUser().getUserId().equals(userId)) {
            throw new NotFoundException("Node not found or access denied");
        }

        node.setText(dto.getText());
        node.setPosition(dto.getPosition());
        return toDto(nodeRepository.save(node));
    }

    @Override
    @Transactional
    public void deleteNode(Long id, Long userId) {
        MindMapNodeEntity node = nodeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Node not found"));

        // Check if user owns the note that contains this node
        if (!node.getNote().getUser().getUserId().equals(userId)) {
            throw new NotFoundException("Node not found or access denied");
        }

        log.info("Deleting node: {} with text: {}", id, node.getText());

        // Delete all children recursively first
        deleteNodeRecursively(node);

        // Then delete the node itself
        nodeRepository.delete(node);

        log.info("Successfully deleted node: {} and all its children", id);
    }

    @Override
    @Transactional
    public void deleteNodeOnly(Long id, Long userId) {
        MindMapNodeEntity node = nodeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Node not found"));

        // Check if user owns the note that contains this node
        if (!node.getNote().getUser().getUserId().equals(userId)) {
            throw new NotFoundException("Node not found or access denied");
        }

        log.info("Deleting only node: {} with text: {} (children will be moved to parent)", id, node.getText());

        // Move all children to the parent of this node
        List<MindMapNodeEntity> children = node.getChildren();
        MindMapNodeEntity parent = node.getParent();

        for (MindMapNodeEntity child : children) {
            child.setParent(parent);
            nodeRepository.save(child);
            log.info("Moved child node: {} to parent: {}", child.getId(), parent != null ? parent.getId() : "root");
        }

        // Delete the node itself
        nodeRepository.delete(node);

        log.info("Successfully deleted only node: {} (children moved to parent)", id);
    }

    private void deleteNodeRecursively(MindMapNodeEntity node) {
        // Get all children of this node
        List<MindMapNodeEntity> children = node.getChildren();

        // Delete each child recursively
        for (MindMapNodeEntity child : children) {
            log.info("Deleting child node: {} with text: {}", child.getId(), child.getText());
            deleteNodeRecursively(child);
            nodeRepository.delete(child);
        }
    }

    @Override
    public List<MindMapNodeResponseDto> getMindMapTree(Long noteId, Long userId) {
        NoteEntity note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NotFoundException("Note not found"));

        // Check if user owns this note
        if (!note.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("Note not found or access denied");
        }

        List<MindMapNodeEntity> roots = nodeRepository.findByNoteIdAndParentIsNull(noteId);
        return roots.stream().map(this::buildTree).collect(Collectors.toList());
    }

    private MindMapNodeResponseDto buildTree(MindMapNodeEntity node) {
        return MindMapNodeResponseDto.builder()
                .id(node.getId())
                .text(node.getText())
                .position(node.getPosition())
                .noteId(node.getNote().getId())
                .parentId(node.getParent() != null ? node.getParent().getId() : null)
                .children(node.getChildren().stream().map(this::buildTree).collect(Collectors.toList()))
                .build();
    }

    private MindMapNodeResponseDto toDto(MindMapNodeEntity node) {
        return MindMapNodeResponseDto.builder()
                .id(node.getId())
                .text(node.getText())
                .position(node.getPosition())
                .noteId(node.getNote().getId())
                .parentId(node.getParent() != null ? node.getParent().getId() : null)
                .build();
    }
}