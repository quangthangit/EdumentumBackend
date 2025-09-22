package com.EdumentumBackend.EdumentumBackend.dtos.note;

import com.EdumentumBackend.EdumentumBackend.enums.NoteType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequestDto {
    @NotBlank
    private String title;

    @Builder.Default
    private NoteType type = NoteType.BLOCK;

    private String content; // Cho markdown

    private List<BlockRequestDto> blocks; // Cho block

    private List<String> tags;
}
