package com.EdumentumBackend.EdumentumBackend.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MindMapDataDto {
    
    @NotNull(message = "Nodes are required")
    private List<Map<String, Object>> nodes;
    
    @NotNull(message = "Edges are required")
    private List<Map<String, Object>> edges;
    
    private Map<String, Object> viewport;
}