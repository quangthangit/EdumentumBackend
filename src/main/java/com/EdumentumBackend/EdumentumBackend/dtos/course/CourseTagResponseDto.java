package com.EdumentumBackend.EdumentumBackend.dtos.course;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseTagResponseDto {

    private Long courseTagId;
    private String name;
    private String color;
}