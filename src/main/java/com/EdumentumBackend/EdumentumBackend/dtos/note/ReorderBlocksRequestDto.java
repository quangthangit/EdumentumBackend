package com.EdumentumBackend.EdumentumBackend.dtos.note;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReorderBlocksRequestDto {
    @NotNull
    private Long noteId;
    @NotNull
    private List<Long> orderedBlockIds;
}


