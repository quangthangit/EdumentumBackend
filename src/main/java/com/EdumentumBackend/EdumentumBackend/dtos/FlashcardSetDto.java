package com.EdumentumBackend.EdumentumBackend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashcardSetDto {
    private Long id;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @Valid
    private List<FlashcardDto> flashcards;
} 