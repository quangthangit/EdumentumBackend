package com.EdumentumBackend.EdumentumBackend.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponseDto {
    private Long id;
    private String title;
    private Long userId;
}

