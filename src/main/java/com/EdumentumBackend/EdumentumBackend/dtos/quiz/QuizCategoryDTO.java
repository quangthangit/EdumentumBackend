package com.EdumentumBackend.EdumentumBackend.dtos.quiz;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuizCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
}