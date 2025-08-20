package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.file.FileDto;
import com.EdumentumBackend.EdumentumBackend.dtos.file.FileRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.file.FileResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.FileEntity;
import com.EdumentumBackend.EdumentumBackend.entity.FolderEntity;
import com.EdumentumBackend.EdumentumBackend.entity.GroupEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.FileRepository;
import com.EdumentumBackend.EdumentumBackend.repository.FolderRepository;
import com.EdumentumBackend.EdumentumBackend.repository.GroupRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.FileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final FolderRepository folderRepository;

    public FileServiceImpl(FolderRepository folderRepository,FileRepository fileRepository, UserRepository userRepository, GroupRepository groupRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.folderRepository = folderRepository;
    }

    @Override
    public List<FileDto> uploadFileResponseDto(FileRequestDto fileRequestDto, Long userId, Long folderId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        FolderEntity folderEntity = folderRepository.findById(folderId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        List<FileEntity> fileEntities = fileRequestDto.getFiles().stream().map(fileDto -> {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFilename(fileDto.getFilename());
            fileEntity.setFileType(fileDto.getFileType());
            fileEntity.setFileUrl(fileDto.getFileUrl());
            fileEntity.setFolder(folderEntity);
            fileEntity.setUserEntity(user);
            fileEntity.setFileSize(fileDto.getFileSize());
            return fileEntity;
        }).toList();
        List<FileEntity> savedFiles = fileRepository.saveAll(fileEntities);

        List<FileDto> fileDtos = savedFiles.stream().map(file -> {
            FileDto dto = new FileDto();
            dto.setId(file.getId());
            dto.setFilename(file.getFilename());
            dto.setFileType(file.getFileType());
            dto.setFileUrl(file.getFileUrl());
            dto.setOwnerName(file.getUserEntity().getUsername());
            dto.setOwnerId(file.getUserEntity().getUserId());
            dto.setFileSize(file.getFileSize());
            return dto;
        }).toList();

        return fileDtos;
    }

    @Override
    public void deleteFile(Long fileId) {
        fileRepository.deleteById(fileId);
    }
}
