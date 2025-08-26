package com.EdumentumBackend.EdumentumBackend.dtos.note;

import com.EdumentumBackend.EdumentumBackend.enums.BlockType;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockResponseDto {
    private Long id;
    private BlockType type;
    private Integer orderIndex;
    private JsonNode content;
}


