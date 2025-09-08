package com.EdumentumBackend.EdumentumBackend.dtos.achievement;

import com.EdumentumBackend.EdumentumBackend.enums.Rarity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementResponseDto {
    private Long id;
    private String icon;
    private String title;
    private String description;
    private int targetValue;
    private int points;
    private Rarity rarity;
    private int currentValue;
    private boolean achieved;
}
