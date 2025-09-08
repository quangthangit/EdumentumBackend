package com.EdumentumBackend.EdumentumBackend.dtos.course;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherSummaryResponseDto{

    private Long userId;
    private String username;
    private String email;
    private String imageUrl;
}
