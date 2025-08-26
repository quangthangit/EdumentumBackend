package com.EdumentumBackend.EdumentumBackend.dtos.note;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteRealtimeDto {
    private String event; // BLOCK_CREATED, BLOCK_UPDATED, BLOCK_DELETED, BLOCK_REORDERED
    private Long noteId;
    private Long blockId;
    private String payload; // optional JSON payload
}


