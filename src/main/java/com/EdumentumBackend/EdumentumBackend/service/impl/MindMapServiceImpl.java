package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.mindmap.MindMapRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.mindmap.MindMapResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.MindMapEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.exception.BadRequestException;
import com.EdumentumBackend.EdumentumBackend.repository.MindMapRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.MindMapService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
<<<<<<< HEAD
import com.EdumentumBackend.EdumentumBackend.dtos.mindmap.MindMapDataDto;
import com.EdumentumBackend.EdumentumBackend.dtos.mindmap.MindMapFileResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.mindmap.MindMapFileRequestDto;
=======
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapDataDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapFileResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapFileRequestDto;
import com.EdumentumBackend.EdumentumBackend.enums.MindMapType;
>>>>>>> 10b38ecee888f965814a6e22ef45f13be12f5b02

@Service
public class MindMapServiceImpl implements MindMapService {

    private final MindMapRepository mindMapRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public MindMapServiceImpl(MindMapRepository mindMapRepository, UserRepository userRepository,
            ObjectMapper objectMapper) {
        this.mindMapRepository = mindMapRepository;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
    }

    @Override
    public MindMapFileResponseDto updateFileName(String id, String newName, Long userId) {
        // Input validation
        if (id == null || id.trim().isEmpty()) {
            throw new BadRequestException("File ID cannot be empty");
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new BadRequestException("File name cannot be empty");
        }
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        Long fileId;
        try {
            fileId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid file ID format");
        }

        MindMapEntity mindMap = mindMapRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("File not found"));

        if (!mindMap.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("File not found or not accessible");
        }

        mindMap.setName(newName.trim());
        mindMap.setUpdatedAt(LocalDateTime.now());
        MindMapEntity updatedMindMap = mindMapRepository.save(mindMap);

        return convertToMindMapFileResponseDto(updatedMindMap);
    }

    @Override
    public MindMapResponseDto createMindMap(MindMapRequestDto mindMapRequestDto, Long userId) {
        // Input validation
        if (mindMapRequestDto == null) {
            throw new BadRequestException("Mind map request data cannot be null");
        }
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }
        if (mindMapRequestDto.getName() == null || mindMapRequestDto.getName().trim().isEmpty()) {
            throw new BadRequestException("Mind map name cannot be empty");
        }
        if (mindMapRequestDto.getData() == null) {
            throw new BadRequestException("Mind map data cannot be null");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Convert MindMapDataDto to JSON string
        String dataJson;
        try {
            dataJson = objectMapper.writeValueAsString(mindMapRequestDto.getData());
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Failed to serialize mind map data: " + e.getMessage());
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
    public MindMapResponseDto getMindMapById(Long id, Long userId) {
        MindMapEntity mindMap = mindMapRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mind map not found"));

        // Check if user owns this mind map
        if (!mindMap.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("Mind map not found or not accessible");
        }

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
        // Input validation
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid mind map ID");
        }
        if (mindMapRequestDto == null) {
            throw new BadRequestException("Mind map request data cannot be null");
        }
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }
        if (mindMapRequestDto.getName() == null || mindMapRequestDto.getName().trim().isEmpty()) {
            throw new BadRequestException("Mind map name cannot be empty");
        }
        if (mindMapRequestDto.getData() == null) {
            throw new BadRequestException("Mind map data cannot be null");
        }

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
            throw new BadRequestException("Failed to serialize mind map data: " + e.getMessage());
        }

        mindMap.setName(mindMapRequestDto.getName());
        mindMap.setData(dataJson);
        mindMap.setType(mindMapRequestDto.getType() != null ? mindMapRequestDto.getType() : mindMap.getType());
        MindMapEntity updatedMindMap = mindMapRepository.save(mindMap);
        return convertToResponseDto(updatedMindMap);
    }

    @Override
    public void deleteMindMap(Long id, Long userId) {
        MindMapEntity mindMap = mindMapRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mind map not found"));

        // Check if user owns this mind map
        if (!mindMap.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("Mind map not found or not accessible");
        }

        mindMapRepository.delete(mindMap);
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
        // Input validation
        if (mindMapFileRequestDto == null) {
            throw new BadRequestException("File request data cannot be null");
        }
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }
        if (mindMapFileRequestDto.getName() == null || mindMapFileRequestDto.getName().trim().isEmpty()) {
            throw new BadRequestException("File name cannot be empty");
        }
        if (mindMapFileRequestDto.getData() == null || mindMapFileRequestDto.getData().trim().isEmpty()) {
            throw new BadRequestException("File data cannot be empty");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        MindMapEntity mindMap = MindMapEntity.builder()
                .name(mindMapFileRequestDto.getName())
                .data(mindMapFileRequestDto.getData())
                .type(mindMapFileRequestDto.getType() != null ? mindMapFileRequestDto.getType()
                        : MindMapType.STUDY_NOTES)
                .user(user)
                .build();

        MindMapEntity savedMindMap = mindMapRepository.save(mindMap);
        return convertToMindMapFileResponseDto(savedMindMap);
    }

    @Override
    public MindMapFileResponseDto updateFile(String id, MindMapFileRequestDto mindMapFileRequestDto, Long userId) {
        // Input validation
        if (id == null || id.trim().isEmpty()) {
            throw new BadRequestException("File ID cannot be empty");
        }
        if (mindMapFileRequestDto == null) {
            throw new BadRequestException("File request data cannot be null");
        }
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }
        if (mindMapFileRequestDto.getName() == null || mindMapFileRequestDto.getName().trim().isEmpty()) {
            throw new BadRequestException("File name cannot be empty");
        }
        if (mindMapFileRequestDto.getData() == null || mindMapFileRequestDto.getData().trim().isEmpty()) {
            throw new BadRequestException("File data cannot be empty");
        }

        Long fileId;
        try {
            fileId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid file ID format");
        }

        MindMapEntity mindMap = mindMapRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("File not found"));

        // Check if user owns this file
        if (!mindMap.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("File not found or not accessible");
        }

        mindMap.setName(mindMapFileRequestDto.getName());
        mindMap.setData(mindMapFileRequestDto.getData());
        mindMap.setType(mindMapFileRequestDto.getType() != null ? mindMapFileRequestDto.getType() : mindMap.getType());
        MindMapEntity updatedMindMap = mindMapRepository.save(mindMap);
        return convertToMindMapFileResponseDto(updatedMindMap);
    }

    @Override
    public void deleteFile(String id, Long userId) {
        // Input validation
        if (id == null || id.trim().isEmpty()) {
            throw new BadRequestException("File ID cannot be empty");
        }
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        Long fileId;
        try {
            fileId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid file ID format");
        }

        MindMapEntity mindMap = mindMapRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("File not found"));

        // Check if user owns this file
        if (!mindMap.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("File not found or not accessible");
        }

        mindMapRepository.delete(mindMap);
    }

    @Override
    public MindMapFileResponseDto getFileById(String id, Long userId) {
        // Input validation
        if (id == null || id.trim().isEmpty()) {
            throw new BadRequestException("File ID cannot be empty");
        }
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Invalid user ID");
        }

        Long fileId;
        try {
            fileId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid file ID format");
        }

        MindMapEntity mindMap = mindMapRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("File not found"));

        // Check if user owns this file
        if (!mindMap.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("File not found or not accessible");
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