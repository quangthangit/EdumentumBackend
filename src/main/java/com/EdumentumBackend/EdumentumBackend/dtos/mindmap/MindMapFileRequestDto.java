package com.EdumentumBackend.EdumentumBackend.dtos.mindmap;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MindMapFileRequestDto {
    
    @NotBlank(message = "File name is required")
    @Size(min = 1, max = 255, message = "File name must be between 1 and 255 characters")
    private String name;
    
    @NotNull(message = "File data is required")
    private String data;
}
