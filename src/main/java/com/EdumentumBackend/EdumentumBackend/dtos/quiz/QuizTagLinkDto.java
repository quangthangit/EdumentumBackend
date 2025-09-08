package com.EdumentumBackend.EdumentumBackend.dtos.quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuizTagLinkDto {
    private Long quizId;
    private Long tagId;
    private String slug;
    private String tagName;
    private String tagDescription;
    public QuizTagLinkDto(Long quizId, Long tagId, String name, String description) {
        this.quizId = quizId;
        this.tagId = tagId;
        this.tagName = name;
        this.tagDescription = description;
    }

}

