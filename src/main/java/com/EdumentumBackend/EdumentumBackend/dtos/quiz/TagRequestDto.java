package com.EdumentumBackend.EdumentumBackend.dtos.quiz;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TagRequestDto {
    private Long id;
    @Size(max = 100, message = "Tag name must be less than 100 characters")
    private String name;
    private String icon;
    private String color;
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
}
