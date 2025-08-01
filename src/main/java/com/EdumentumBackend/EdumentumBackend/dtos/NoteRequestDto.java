package com.EdumentumBackend.EdumentumBackend.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequestDto {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be at most 100 characters long")
    private String title;

    private Long userId; // để tạo note gán cho user cụ thể (nếu không lấy từ context)
}
