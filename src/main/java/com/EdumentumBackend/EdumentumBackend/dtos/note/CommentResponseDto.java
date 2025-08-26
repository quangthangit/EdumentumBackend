package com.EdumentumBackend.EdumentumBackend.dtos.note;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;
    private Long userId;
    private Long blockId;
    private String content;
}


