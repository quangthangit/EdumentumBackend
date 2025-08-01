package com.EdumentumBackend.EdumentumBackend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MindMapNodeRequestDto {

    @NotNull(message = "Note ID is required")
    private Long noteId;

    private Long parentId;

    @NotBlank(message = "Text is required")
    private String text;

    private Integer position;
}

