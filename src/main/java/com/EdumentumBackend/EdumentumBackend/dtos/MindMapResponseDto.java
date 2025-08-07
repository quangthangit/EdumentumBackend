package com.EdumentumBackend.EdumentumBackend.dtos;

import com.EdumentumBackend.EdumentumBackend.entity.MindMapType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MindMapResponseDto {
    private Long id;
    private String name;
    private Long userId;
    private MindMapDataDto data;
    private MindMapType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
