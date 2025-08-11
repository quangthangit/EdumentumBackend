package com.EdumentumBackend.EdumentumBackend.dtos.mindmap;

import com.EdumentumBackend.EdumentumBackend.enums.MindMapType;
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
    private MindMapType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}