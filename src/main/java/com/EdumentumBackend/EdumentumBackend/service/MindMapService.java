package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.MindMapFileRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapFileResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapResponseDto;

import java.util.List;

public interface MindMapService {
    MindMapResponseDto createMindMap(MindMapRequestDto mindMapRequestDto, Long userId);
    MindMapResponseDto getMindMapById(Long id);
    List<MindMapResponseDto> getAllMindMapsByUserId(Long userId);
    MindMapResponseDto updateMindMap(Long id, MindMapRequestDto mindMapRequestDto, Long userId);
    void deleteMindMap(Long id);
    List<MindMapFileResponseDto> getFilesByUserId(Long userId);
    MindMapFileResponseDto updateFileName(String id, String newName, Long userId);
    MindMapFileResponseDto createFile(MindMapFileRequestDto mindMapFileRequestDto, Long userId);
    MindMapFileResponseDto updateFile(String id, MindMapFileRequestDto mindMapFileRequestDto, Long userId);
    void deleteFile(String id, Long userId);
    MindMapFileResponseDto getFileById(String id, Long userId);
}