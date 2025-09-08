package com.EdumentumBackend.EdumentumBackend.dtos.user;

import com.EdumentumBackend.EdumentumBackend.enums.LevelProgress;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserProfileInfoResponseDto {
    private Long id;
    private String username;
    private LocalDateTime createdAt;
    private int streak;
    private int maxStreak;
    private LevelProgress levelProgress;
    private int totalStudyTime;
    private Long totalStudyTimeToday;
    private int totalQuizzesCreated;
    private int totalQuizzesCompleted;
    private int totalFlashCardCreated;
    private int totalFlashCardCompleted;
    private int totalAttendance;
}
