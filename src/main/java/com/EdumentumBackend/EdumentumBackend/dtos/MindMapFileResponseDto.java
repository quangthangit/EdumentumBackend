package com.EdumentumBackend.EdumentumBackend.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MindMapFileResponseDto {
    private String id;
    private String name;
    private String data;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}