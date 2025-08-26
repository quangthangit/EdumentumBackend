package com.EdumentumBackend.EdumentumBackend.dtos.note;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponseDto {
    private Long id;
    private String title;
    private Long ownerId;
    private Boolean isDeleted;
    private List<BlockResponseDto> blocks;
    private List<String> tags;
}


