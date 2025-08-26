package com.EdumentumBackend.EdumentumBackend.dtos.note;

import com.EdumentumBackend.EdumentumBackend.enums.BlockType;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockRequestDto {
    @NotNull
    private BlockType type;
    @NotNull
    private Integer orderIndex;
    @NotNull
    private JsonNode content;
}


