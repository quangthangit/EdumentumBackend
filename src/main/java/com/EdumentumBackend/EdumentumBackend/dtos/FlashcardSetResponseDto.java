package com.EdumentumBackend.EdumentumBackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashcardSetResponseDto {
    private Long id;
    private String title;
    private String description;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private UserResponseDto user;
    private List<FlashcardResponseDto> flashcards;
}
