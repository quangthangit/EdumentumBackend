package com.EdumentumBackend.EdumentumBackend.dtos.folder;


import com.EdumentumBackend.EdumentumBackend.dtos.file.FileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderResponseDto {
    private String folderName;
    private Long id;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;
    private List<FileDto> files;
}
