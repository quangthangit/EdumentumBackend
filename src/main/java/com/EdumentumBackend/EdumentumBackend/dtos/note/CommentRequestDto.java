package com.EdumentumBackend.EdumentumBackend.dtos.note;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    @NotNull
    private String content;
    private Long blockId;
    private Long parentId;
}


