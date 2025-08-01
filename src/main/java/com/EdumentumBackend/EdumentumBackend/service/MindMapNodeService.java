package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.MindMapNodeRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapNodeResponseDto;

import java.util.List;

public interface MindMapNodeService {
    MindMapNodeResponseDto createNode(MindMapNodeRequestDto dto, Long userId);

    MindMapNodeResponseDto updateNode(Long id, MindMapNodeRequestDto dto, Long userId);

    void deleteNode(Long id, Long userId);

    void deleteNodeOnly(Long id, Long userId);

    List<MindMapNodeResponseDto> getMindMapTree(Long noteId, Long userId);
}