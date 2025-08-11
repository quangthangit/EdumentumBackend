package com.EdumentumBackend.EdumentumBackend.dtos.flashcard;

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
public class FlashcardResponseDto {
    private Long id;
    private String question;
    private List<String> choices;
    private Integer correctAnswer;
    private String explanation;
}
