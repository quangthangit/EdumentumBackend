package com.EdumentumBackend.EdumentumBackend.dtos;

import jakarta.validation.constraints.NotBlank;
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
public class FlashcardDto {
    @NotBlank(message = "Question is required")
    private String question;

    private List<String> choices;
    
    private Integer correctAnswer;
    
    private String explanation;
} 