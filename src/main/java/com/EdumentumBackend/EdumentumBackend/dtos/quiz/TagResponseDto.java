package com.EdumentumBackend.EdumentumBackend.dtos.quiz;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResponseDto {
    private Long id;
    private String name;
    private String slug;
    private String icon;
    private String color;
    private String description;
}
