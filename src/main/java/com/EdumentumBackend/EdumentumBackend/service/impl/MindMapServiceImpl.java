package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.mindmap.MindMapFileRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.mindmap.MindMapFileResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.MindMapEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.exception.BadRequestException;
import com.EdumentumBackend.EdumentumBackend.repository.MindMapRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.MindMapService;
import com.EdumentumBackend.EdumentumBackend.enums.MindMapType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MindMapServiceImpl implements MindMapService {

    private final MindMapRepository mindMapRepository;
    private final UserRepository userRepository;

    public MindMapServiceImpl(MindMapRepository mindMapRepository, UserRepository userRepository) {
        this.mindMapRepository = mindMapRepository;
        this.userRepository = userRepository;
    }

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

        Long fileId = parseFileId(id);
        MindMapEntity mindMap = findAndValidateOwnership(fileId, userId);

        mindMap.setName(newName.trim());
        mindMap.setUpdatedAt(LocalDateTime.now());
        MindMapEntity updatedMindMap = mindMapRepository.save(mindMap);

        return convertToMindMapFileResponseDto(updatedMindMap);
    }

    @Override
    public MindMapFileResponseDto createFile(MindMapFileRequestDto mindMapFileRequestDto, Long userId) {
        // Input validation
        validateFileRequest(mindMapFileRequestDto, userId);

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
        // Input validation
        if (id == null || id.trim().isEmpty()) {
            throw new BadRequestException("File ID cannot be empty");
        }
        validateFileRequest(mindMapFileRequestDto, userId);

        Long fileId = parseFileId(id);
        MindMapEntity mindMap = findAndValidateOwnership(fileId, userId);

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

        Long fileId = parseFileId(id);
        MindMapEntity mindMap = findAndValidateOwnership(fileId, userId);

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

        Long fileId = parseFileId(id);
        MindMapEntity mindMap = findAndValidateOwnership(fileId, userId);

        return convertToMindMapFileResponseDto(mindMap);
    }

    // Helper methods
    private Long parseFileId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid file ID format");
        }
    }

    private MindMapEntity findAndValidateOwnership(Long fileId, Long userId) {
        MindMapEntity mindMap = mindMapRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("File not found"));

        if (!mindMap.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("File not found or not accessible");
        }

        return mindMap;
    }

    private void validateFileRequest(MindMapFileRequestDto mindMapFileRequestDto, Long userId) {
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

