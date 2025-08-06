package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.FilePropsDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapDto;

import java.util.List;

public interface MindMapService {
    MindMapDto createMindMap(MindMapDto mindMapDto);

    MindMapDto getMindMapById(Long id);

    List<MindMapDto> getAllMindMapsByUserId(Long userId);

    MindMapDto updateMindMap(Long id, MindMapDto mindMapDto);

    void deleteMindMap(Long id);

    // File-based operations
    List<FilePropsDto> getFilesByUserId(Long userId);

    FilePropsDto createFile(String name, String data, Long userId);

    FilePropsDto updateFile(String id, String data, Long userId);

    void deleteFile(String id, Long userId);

    FilePropsDto getFileById(String id, Long userId);
}