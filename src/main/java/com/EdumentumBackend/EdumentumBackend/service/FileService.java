package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.file.FileDto;
import com.EdumentumBackend.EdumentumBackend.dtos.file.FileRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.file.FileResponseDto;

import java.util.List;

public interface FileService {
    List<FileDto> uploadFileResponseDto(FileRequestDto fileRequestDto, Long userId, Long folderId);
    void deleteFile(Long fileId);
}
