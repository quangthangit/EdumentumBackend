package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.file.FileDto;
import com.EdumentumBackend.EdumentumBackend.dtos.folder.FolderRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.folder.FolderResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.FolderEntity;
import com.EdumentumBackend.EdumentumBackend.entity.GroupEntity;
import com.EdumentumBackend.EdumentumBackend.entity.GroupMemberEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.AuthenticationFailedException;
import com.EdumentumBackend.EdumentumBackend.exception.BadRequestException;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.FolderRepository;
import com.EdumentumBackend.EdumentumBackend.repository.GroupMemberRepository;
import com.EdumentumBackend.EdumentumBackend.repository.GroupRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.FolderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public FolderServiceImpl(GroupMemberRepository groupMemberRepository, FolderRepository folderRepository, UserRepository userRepository, GroupRepository groupRepository) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @Override
    public FolderResponseDto createFolder(FolderRequestDto folderRequestDto, Long groupId, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        GroupMemberEntity groupMemberEntity = groupMemberRepository.findByGroupAndUser(group, user);
        if (groupMemberEntity == null) {
            throw new AuthenticationFailedException("You are not a member of this group");
        }

        FolderEntity folderEntity = FolderEntity.builder()
                .name(folderRequestDto.getName())
                .groupEntity(group)
                .userEntity(user)
                .build();

        FolderEntity savedFolder = folderRepository.save(folderEntity);

        return FolderResponseDto.builder()
                .id(savedFolder.getId())
                .folderName(savedFolder.getName())
                .createdAt(savedFolder.getCreatedAt())
                .ownerId(user.getUserId())
                .ownerName(user.getUsername())
                .build();
    }

    @Override
    public List<FolderResponseDto> getAllFolderByGroup(Long groupId, Long userId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        List<FolderEntity> folderEntities = folderRepository.findAllByGroupEntity(group);

        return folderEntities.stream()
                .map(folder -> {
                    List<FileDto> fileDtos = folder.getFiles().stream()
                            .map(file -> {
                                FileDto dto = new FileDto();
                                dto.setId(file.getId());
                                dto.setOwnerName(file.getUserEntity().getUsername());
                                dto.setOwnerId(file.getUserEntity().getUserId());
                                dto.setFilename(file.getFilename());
                                dto.setFileType(file.getFileType());
                                dto.setFileUrl(file.getFileUrl());
                                dto.setFileSize(file.getFileSize());
                                return dto;
                            })
                            .toList();

                    return FolderResponseDto.builder()
                            .id(folder.getId())
                            .folderName(folder.getName())
                            .files(fileDtos)
                            .ownerId(folder.getUserEntity().getUserId())
                            .ownerName(folder.getUserEntity().getUsername())
                            .createdAt(folder.getCreatedAt())
                            .build();
                })
                .toList();
    }
}
