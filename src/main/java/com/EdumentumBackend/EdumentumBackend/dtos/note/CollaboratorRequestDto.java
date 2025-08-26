package com.EdumentumBackend.EdumentumBackend.dtos.note;

import com.EdumentumBackend.EdumentumBackend.enums.NotePermission;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollaboratorRequestDto {
    @NotNull
    private Long userId;
    @NotNull
    private NotePermission permission;
}


