package com.EdumentumBackend.EdumentumBackend.dtos.achievement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementSummaryResponseDto {
    private Long totalUnlocked;
    private Long totalAchievements;
    private Double percentCompleted;
    private Long totalXP;

    public AchievementSummaryResponseDto(Number totalUnlocked,
                                         Number totalAchievements,
                                         Number percentCompleted,
                                         Number totalXP) {
        this.totalUnlocked = totalUnlocked != null ? totalUnlocked.longValue() : 0L;
        this.totalAchievements = totalAchievements != null ? totalAchievements.longValue() : 0L;
        this.percentCompleted = percentCompleted != null ? percentCompleted.doubleValue() : 0.0;
        this.totalXP = totalXP != null ? totalXP.longValue() : 0L;
    }
}
