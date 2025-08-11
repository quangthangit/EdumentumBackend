package com.EdumentumBackend.EdumentumBackend.dtos.flashcard;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
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
public class FlashcardRequestDto {
    @NotNull(message = "Question is required")
    private String question;
    
    private List<String> choices;
    
    @Min(value = 0, message = "Correct answer index must be non-negative")
    @Max(value = 10, message = "Correct answer index cannot exceed 10")
    private Integer correctAnswer;
    
    private String explanation;
}
