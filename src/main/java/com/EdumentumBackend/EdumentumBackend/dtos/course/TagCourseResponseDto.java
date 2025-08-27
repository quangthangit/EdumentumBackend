package com.EdumentumBackend.EdumentumBackend.dtos.course;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagCourseResponseDto {

    private Long tagCourseId;
    private String name;
    private String color;
}