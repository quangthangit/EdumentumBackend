package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.folder.FolderRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.folder.FolderResponseDto;

import java.util.List;

public interface FolderService {
    FolderResponseDto createFolder(FolderRequestDto folderRequestDto, String publicGroupId, Long userId);
    List<FolderResponseDto> getAllFolderByGroup(String publicGroupId,Long userId);
    void deleteFolderById(Long folderId, Long userId, String publicGroupId);
}
