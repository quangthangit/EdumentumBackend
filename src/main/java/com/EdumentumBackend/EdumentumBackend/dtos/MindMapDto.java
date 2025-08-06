package com.EdumentumBackend.EdumentumBackend.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MindMapDto {
    private Long id;
    private String name;
    private Long userId;
    private String data; // JSON string containing nodes and edges
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}