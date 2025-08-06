package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.FilePropsDto;
import com.EdumentumBackend.EdumentumBackend.dtos.MindMapDto;
import com.EdumentumBackend.EdumentumBackend.entity.MindMapEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.MindMapRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.MindMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MindMapServiceImpl implements MindMapService {

    private final MindMapRepository mindMapRepository;
    private final UserRepository userRepository;

    @Override
    public MindMapDto createMindMap(MindMapDto mindMapDto) {
        UserEntity user = userRepository.findById(mindMapDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        MindMapEntity mindMap = MindMapEntity.builder()
                .name(mindMapDto.getName())
                .data(mindMapDto.getData())
                .user(user)
                .build();

        MindMapEntity savedMindMap = mindMapRepository.save(mindMap);
        return convertToDto(savedMindMap);
    }

    @Override
    public MindMapDto getMindMapById(Long id) {
        MindMapEntity mindMap = mindMapRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mind map not found"));
        return convertToDto(mindMap);
    }

    @Override
    public List<MindMapDto> getAllMindMapsByUserId(Long userId) {
        List<MindMapEntity> mindMaps = mindMapRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
        return mindMaps.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MindMapDto updateMindMap(Long id, MindMapDto mindMapDto) {
        MindMapEntity mindMap = mindMapRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Mind map not found"));

        mindMap.setName(mindMapDto.getName());
        mindMap.setData(mindMapDto.getData());
        MindMapEntity updatedMindMap = mindMapRepository.save(mindMap);
        return convertToDto(updatedMindMap);
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
    public List<FilePropsDto> getFilesByUserId(Long userId) {
        List<MindMapEntity> mindMaps = mindMapRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
        return mindMaps.stream()
                .map(this::convertToFilePropsDto)
                .collect(Collectors.toList());
    }

    @Override
    public FilePropsDto createFile(String name, String data, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        MindMapEntity mindMap = MindMapEntity.builder()
                .name(name)
                .data(data)
                .user(user)
                .build();

        MindMapEntity savedMindMap = mindMapRepository.save(mindMap);
        return convertToFilePropsDto(savedMindMap);
    }

    @Override
    public FilePropsDto updateFile(String id, String data, Long userId) {
        MindMapEntity mindMap = mindMapRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new NotFoundException("File not found"));

        // Check if user owns this file
        if (!mindMap.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("File not found");
        }

        mindMap.setData(data);
        MindMapEntity updatedMindMap = mindMapRepository.save(mindMap);
        return convertToFilePropsDto(updatedMindMap);
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
    public FilePropsDto getFileById(String id, Long userId) {
        MindMapEntity mindMap = mindMapRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new NotFoundException("File not found"));

        // Check if user owns this file
        if (!mindMap.getUser().getUserId().equals(userId)) {
            throw new NotFoundException("File not found");
        }

        return convertToFilePropsDto(mindMap);
    }

    private MindMapDto convertToDto(MindMapEntity mindMap) {
        return MindMapDto.builder()
                .id(mindMap.getId())
                .name(mindMap.getName())
                .data(mindMap.getData())
                .userId(mindMap.getUser().getUserId())
                .createdAt(mindMap.getCreatedAt())
                .updatedAt(mindMap.getUpdatedAt())
                .build();
    }

    private FilePropsDto convertToFilePropsDto(MindMapEntity mindMap) {
        return FilePropsDto.builder()
                .id(mindMap.getId().toString())
                .name(mindMap.getName())
                .data(mindMap.getData())
                .createdAt(mindMap.getCreatedAt())
                .updatedAt(mindMap.getUpdatedAt())
                .build();
    }
}