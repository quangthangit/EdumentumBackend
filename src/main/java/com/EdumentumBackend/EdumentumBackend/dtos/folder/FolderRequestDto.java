package com.EdumentumBackend.EdumentumBackend.dtos.folder;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FolderRequestDto {
    @NotNull(message = "Folder is required")
    @Size(max = 20, message = "Folder must be at most 20 characters long")
    @Column(nullable = false)
    private String name;
}
