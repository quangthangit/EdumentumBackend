package com.EdumentumBackend.EdumentumBackend.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GroupResponseDto {
    private Long id;
    private String name;
    private String description;
    private boolean isPublic;
    private Long ownerId;
    private String ownerName;
    private int memberCount;
    private String key;
    private LocalDateTime createdAt;
}
