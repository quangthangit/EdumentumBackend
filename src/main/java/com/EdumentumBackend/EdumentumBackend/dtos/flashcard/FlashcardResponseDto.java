package com.EdumentumBackend.EdumentumBackend.dtos.flashcard;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlashcardResponseDto {
    private Long id;
    private String question;
    private List<String> choices;
    private Integer correctAnswer;

    private String vocabulary;
    private String meaning;
    private String example;

    private String explanation;
}
