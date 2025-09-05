package com.EdumentumBackend.EdumentumBackend.dtos.course;

import com.EdumentumBackend.EdumentumBackend.enums.ResourceType;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponseDto {
    
    private Long resourceId;
    private String title;
    private String description;
    private ResourceType resourceType;
    private String url;
    private Integer orderIndex;
    private LocalDateTime createdAt;
}
