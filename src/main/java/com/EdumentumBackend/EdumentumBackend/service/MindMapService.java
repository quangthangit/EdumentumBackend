package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.mindmap.MindMapFileRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.mindmap.MindMapFileResponseDto;
import com.EdumentumBackend.EdumentumBackend.enums.MindMapType;

import java.util.List;

public interface MindMapService {

    List<MindMapFileResponseDto> getFilesByUserId(Long userId);

    List<MindMapFileResponseDto> getFilesByUserIdAndType(Long userId, MindMapType type);

    MindMapFileResponseDto updateFileName(String id, String newName, Long userId);

    MindMapFileResponseDto createFile(MindMapFileRequestDto mindMapFileRequestDto, Long userId);

    MindMapFileResponseDto updateFile(String id, MindMapFileRequestDto mindMapFileRequestDto, Long userId);

    void deleteFile(String id, Long userId);

    MindMapFileResponseDto getFileById(String id, Long userId);
}