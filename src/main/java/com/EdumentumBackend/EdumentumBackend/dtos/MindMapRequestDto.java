package com.EdumentumBackend.EdumentumBackend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MindMapRequestDto {
    
    @NotBlank(message = "Mind map name is required")
    @Size(min = 1, max = 255, message = "Mind map name must be between 1 and 255 characters")
    private String name;
    
    @NotNull(message = "Mind map data is required")
    private MindMapDataDto data;
}
