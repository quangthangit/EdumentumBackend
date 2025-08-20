package com.EdumentumBackend.EdumentumBackend.dtos.file;

import com.EdumentumBackend.EdumentumBackend.enums.FileType;
import lombok.Data;

@Data
public class FileDto {
    private Long id;
    private String filename;
    private FileType fileType;
    private String fileUrl;
    private Long fileSize;
    private Long ownerId;
    private String ownerName;
}
