package com.EdumentumBackend.EdumentumBackend.dtos.contribution;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContributionHistoryRequestDto {
    @NotNull(message = "Group ID must not be null")
    private Long groupId;

    @Min(value = 1, message = "Contribution points must be at least 1")
    private int points;

    private String message;
}
