package com.EdumentumBackend.EdumentumBackend.dtos.file;

import lombok.Data;

import java.util.List;

@Data
public class FileRequestDto {
    private List<FileDto> files;
}
