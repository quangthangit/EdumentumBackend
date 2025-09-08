package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.achievement.AchievementResponseDto;

import java.util.List;

public interface AchievementService {
    List<AchievementResponseDto> findAll(Long userId);
}
