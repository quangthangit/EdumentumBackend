package com.EdumentumBackend.EdumentumBackend.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MindMapNodeResponseDto {
    private Long id;
    private String text;
    private Integer position;
    private Long parentId;
    private Long noteId;
    private List<MindMapNodeResponseDto> children;
}
