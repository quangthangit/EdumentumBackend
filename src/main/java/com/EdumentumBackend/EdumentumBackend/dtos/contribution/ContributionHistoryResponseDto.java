package com.EdumentumBackend.EdumentumBackend.dtos.contribution;

import lombok.Data;

@Data
public class ContributionHistoryResponseDto {
    private Long id;
    private Long userId;
    private String username;
    private String imageUrl;
    private int point;
    private String message;
}
