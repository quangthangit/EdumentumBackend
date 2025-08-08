package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.MindMapRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.MindMapEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.MindMapRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.MindMapService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapDataDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapFileResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapFileRequestDto;
import com.EdumentumBackend.EdumentumBackend.entity.MindMapType;

@Service
@RequiredArgsConstructor
public class MindMapServiceImpl implements MindMapService {

    private final MindMapRepository mindMapRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public MindMapFileResponseDto updateFileName(String id, String newName, Long userId) {
        MindMapEntity mindMap = mindMapRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new NotFoundException("File not found"));

        // Kiểm tra quyền sở hữu file
        if (!mindMap.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("File not found or not accessible");
        }

        mindMap.setName(newName);
        mindMap.setUpdatedAt(LocalDateTime.now()); // cập nhật thời gian chỉnh sửa
        MindMapEntity updatedMindMap = mindMapRepository.save(mindMap);

        return convertToMindMapFileResponseDto(updatedMindMap);
    }


    @Override
    public MindMapResponseDto createMindMap(MindMapRequestDto mindMapRequestDto, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Convert MindMapDataDto to JSON string
        String dataJson;
        try {
            dataJson = objectMapper.writeValueAsString(mindMapRequestDto.getData());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize mind map data", e);
        }

        MindMapEntity mindMap = MindMapEntity.builder()
                .name(mindMapRequestDto.getName())
                .data(dataJson)
                .type(mindMapRequestDto.getType() != null ? mindMapRequestDto.getType() : MindMapType.STUDY_NOTES)
                .user(user)
                .build();

        MindMapEntity savedMindMap = mindMapRepository.save(mindMap);
        return convertToResponseDto(savedMindMap);
    }


    @Override
    public MindMapResponseDto getMindMapById(Long id) {
        MindMapEntity mindMap = mindMapRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mind map not found"));
        return convertToResponseDto(mindMap);
    }

    @Override
    public List<MindMapResponseDto> getAllMindMapsByUserId(Long userId) {
        List<MindMapEntity> mindMaps = mindMapRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
        return mindMaps.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MindMapResponseDto> getMindMapsByUserIdAndType(Long userId, MindMapType type) {
        List<MindMapEntity> mindMaps = mindMapRepository.findByUserUserIdAndTypeOrderByCreatedAtDesc(userId, type);
        return mindMaps.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MindMapResponseDto> getMindMapsByType(MindMapType type) {
        List<MindMapEntity> mindMaps = mindMapRepository.findByTypeOrderByCreatedAtDesc(type);
        return mindMaps.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public MindMapResponseDto updateMindMap(Long id, MindMapRequestDto mindMapRequestDto, Long userId) {
        MindMapEntity mindMap = mindMapRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mind map not found"));

        // Check if user owns this mind map
        if (!mindMap.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("Mind map not found or not accessible");
        }

        // Convert MindMapDataDto to JSON string
        String dataJson;
        try {
            dataJson = objectMapper.writeValueAsString(mindMapRequestDto.getData());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize mind map data", e);
        }

        mindMap.setName(mindMapRequestDto.getName());
        mindMap.setData(dataJson);
        mindMap.setType(mindMapRequestDto.getType() != null ? mindMapRequestDto.getType() : mindMap.getType());
        MindMapEntity updatedMindMap = mindMapRepository.save(mindMap);
        return convertToResponseDto(updatedMindMap);
    }

    @Override
    public void deleteMindMap(Long id) {
        if (!mindMapRepository.existsById(id)) {
            throw new NotFoundException("Mind map not found");
        }
        mindMapRepository.deleteById(id);
    }

    // File-based operations
    @Override
    public List<MindMapFileResponseDto> getFilesByUserId(Long userId) {
        List<MindMapEntity> mindMaps = mindMapRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
        return mindMaps.stream()
                .map(this::convertToMindMapFileResponseDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<MindMapFileResponseDto> getFilesByUserIdAndType(Long userId, MindMapType type) {
        List<MindMapEntity> mindMaps = mindMapRepository.findByUserUserIdAndTypeOrderByCreatedAtDesc(userId, type);
        return mindMaps.stream()
                .map(this::convertToMindMapFileResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public MindMapFileResponseDto createFile(MindMapFileRequestDto mindMapFileRequestDto, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        MindMapEntity mindMap = MindMapEntity.builder()
                .name(mindMapFileRequestDto.getName())
                .data(mindMapFileRequestDto.getData())
                .type(mindMapFileRequestDto.getType() != null ? mindMapFileRequestDto.getType() : MindMapType.STUDY_NOTES)
                .user(user)
                .build();

        MindMapEntity savedMindMap = mindMapRepository.save(mindMap);
        return convertToMindMapFileResponseDto(savedMindMap);
    }

    @Override
    public MindMapFileResponseDto updateFile(String id, MindMapFileRequestDto mindMapFileRequestDto, Long userId) {
        MindMapEntity mindMap = mindMapRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new NotFoundException("File not found"));

        // Check if user owns this file
        if (!mindMap.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("File not found");
        }

        mindMap.setData(mindMapFileRequestDto.getData());
        mindMap.setType(mindMapFileRequestDto.getType() != null ? mindMapFileRequestDto.getType() : mindMap.getType());
        MindMapEntity updatedMindMap = mindMapRepository.save(mindMap);
        return convertToMindMapFileResponseDto(updatedMindMap);
    }

    @Override
    public void deleteFile(String id, Long userId) {
        MindMapEntity mindMap = mindMapRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new NotFoundException("File not found"));

        // Check if user owns this file
        if (!mindMap.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("File not found");
        }

        mindMapRepository.delete(mindMap);
    }

    @Override
    public MindMapFileResponseDto getFileById(String id, Long userId) {
        MindMapEntity mindMap = mindMapRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new NotFoundException("File not found"));

        // Check if user owns this file
        if (!mindMap.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("File not found");
        }

        return convertToMindMapFileResponseDto(mindMap);
    }

    private MindMapResponseDto convertToResponseDto(MindMapEntity mindMap) {
        // Convert JSON string back to MindMapDataDto
        MindMapDataDto dataDto;
        try {
            dataDto = objectMapper.readValue(mindMap.getData(), MindMapDataDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize mind map data", e);
        }

        return MindMapResponseDto.builder()
                .id(mindMap.getId())
                .name(mindMap.getName())
                .userId(mindMap.getUser().getUserId())
                .data(dataDto)
                .type(mindMap.getType())
                .createdAt(mindMap.getCreatedAt())
                .updatedAt(mindMap.getUpdatedAt())
                .build();
    }

    private MindMapFileResponseDto convertToMindMapFileResponseDto(MindMapEntity mindMap) {
        return MindMapFileResponseDto.builder()
                .id(mindMap.getId().toString())
                .name(mindMap.getName())
                .data(mindMap.getData())
                .type(mindMap.getType())
                .createdAt(mindMap.getCreatedAt())
                .updatedAt(mindMap.getUpdatedAt())
                .build();
    }


}