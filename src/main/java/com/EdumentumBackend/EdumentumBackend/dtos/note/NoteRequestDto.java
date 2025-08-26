package com.EdumentumBackend.EdumentumBackend.dtos.note;

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
    private List<String> tags;
}


