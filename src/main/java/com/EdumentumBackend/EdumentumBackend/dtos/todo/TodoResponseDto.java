package com.EdumentumBackend.EdumentumBackend.dtos.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoResponseDto {
    private Long id;
    private String nameTask;
    private String status;
    private String creationAt;
}
