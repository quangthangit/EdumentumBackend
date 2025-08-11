package com.EdumentumBackend.EdumentumBackend.dtos.group;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GroupRequestDto {

    @NotNull(message = "Group name must not be empty")
    @Size(min = 6, max = 100, message = "Group name must be between 6 and 100 characters")
    private String name;

    @NotNull(message = "Description must not be empty")
    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

    @Min(value = 1, message = "Group must have at least 1 member")
    @Max(value = 50, message = "Group cannot have more than 50 members")
    private int memberCount;

    @NotNull
    private boolean isPublic;
}
