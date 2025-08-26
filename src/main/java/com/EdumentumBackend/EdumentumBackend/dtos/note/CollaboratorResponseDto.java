package com.EdumentumBackend.EdumentumBackend.dtos.note;

import com.EdumentumBackend.EdumentumBackend.enums.NotePermission;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollaboratorResponseDto {
    private Long userId;
    private NotePermission permission;
}


