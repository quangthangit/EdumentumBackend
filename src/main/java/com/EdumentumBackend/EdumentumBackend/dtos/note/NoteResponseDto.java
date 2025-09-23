package com.EdumentumBackend.EdumentumBackend.dtos.note;

import com.EdumentumBackend.EdumentumBackend.enums.NoteType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponseDto {
    private Long id;
    private String title;
    private NoteType type;
    private String content; // Cho markdown
    private Long ownerId;
    private Boolean isDeleted;
    private List<BlockResponseDto> blocks; // Cho block
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
