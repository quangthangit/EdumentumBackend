package com.EdumentumBackend.EdumentumBackend.entity.listener;

import com.EdumentumBackend.EdumentumBackend.entity.UserProfileEntity;
import com.EdumentumBackend.EdumentumBackend.enums.LevelProgress;
import jakarta.persistence.PrePersist;

public class UserProfileEntityListener {

    @PrePersist
    public void prePersist(UserProfileEntity entity) {
        entity.setStreak(0);
        entity.setLevelProgress(LevelProgress.LEVEL_1);
        entity.setTotalQuizzesCompleted(0);
        entity.setTotalFlashCardCompleted(0);
        entity.setTotalQuizzesCreated(0);
        entity.setTotalFlashCardCreated(0);
        entity.setTotalStudyTime(0);
        entity.setTotalAttendance(0);
        entity.setMaxStreak(0);
    }
}
