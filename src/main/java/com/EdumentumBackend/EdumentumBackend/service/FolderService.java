package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.folder.FolderRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.folder.FolderResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface FolderService {
    FolderResponseDto createFolder(FolderRequestDto folderRequestDto, Long groupId, Long userId);
    List<FolderResponseDto> getAllFolderByGroup(Long groupId,Long userId);
}
