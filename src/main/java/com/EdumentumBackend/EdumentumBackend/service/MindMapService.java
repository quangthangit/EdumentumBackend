package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.MindMapFileRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapFileResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapResponseDto;
import com.EdumentumBackend.EdumentumBackend.enums.MindMapType;

import java.util.List;

public interface MindMapService {
    MindMapResponseDto createMindMap(MindMapRequestDto mindMapRequestDto, Long userId);

    MindMapResponseDto getMindMapById(Long id, Long userId);

    List<MindMapResponseDto> getAllMindMapsByUserId(Long userId);

    List<MindMapResponseDto> getMindMapsByUserIdAndType(Long userId, MindMapType type);

    List<MindMapResponseDto> getMindMapsByType(MindMapType type);

    MindMapResponseDto updateMindMap(Long id, MindMapRequestDto mindMapRequestDto, Long userId);

    void deleteMindMap(Long id, Long userId);

    List<MindMapFileResponseDto> getFilesByUserId(Long userId);

    List<MindMapFileResponseDto> getFilesByUserIdAndType(Long userId, MindMapType type);

    MindMapFileResponseDto updateFileName(String id, String newName, Long userId);

    MindMapFileResponseDto createFile(MindMapFileRequestDto mindMapFileRequestDto, Long userId);

    MindMapFileResponseDto updateFile(String id, MindMapFileRequestDto mindMapFileRequestDto, Long userId);

    void deleteFile(String id, Long userId);

    MindMapFileResponseDto getFileById(String id, Long userId);
}