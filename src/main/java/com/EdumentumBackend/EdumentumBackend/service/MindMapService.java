package com.EdumentumBackend.EdumentumBackend.service;

<<<<<<< HEAD
import com.EdumentumBackend.EdumentumBackend.dtos.mindmap.MindMapFileRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.mindmap.MindMapFileResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.mindmap.MindMapRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.mindmap.MindMapResponseDto;
=======
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapFileRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapFileResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapResponseDto;
import com.EdumentumBackend.EdumentumBackend.enums.MindMapType;
>>>>>>> 10b38ecee888f965814a6e22ef45f13be12f5b02

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