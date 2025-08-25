package com.EdumentumBackend.EdumentumBackend.dtos.file;

import lombok.Data;

import java.util.List;

@Data
public class FileResponseDto {
    private Long folderId;
    private String name;
    private Long ownerId;
    private String ownerName;
    private List<FileDto> files;
}