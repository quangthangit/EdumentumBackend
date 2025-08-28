package com.EdumentumBackend.EdumentumBackend.dtos.flashcard;

import com.EdumentumBackend.EdumentumBackend.enums.FlashcardType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashcardSetRequestDto {
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private Boolean isPublic;

    private Long categoryId;

    @NotNull(message = "Flashcard type is required")
    private FlashcardType flashcardType;

    @Valid
    private List<FlashcardRequestDto> flashcards;
}
