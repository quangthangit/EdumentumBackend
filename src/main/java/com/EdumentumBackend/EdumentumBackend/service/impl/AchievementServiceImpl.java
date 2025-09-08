package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.achievement.AchievementResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.AchievementEntity;
import com.EdumentumBackend.EdumentumBackend.repository.AchievementRepository;
import com.EdumentumBackend.EdumentumBackend.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;

    @Override
    public List<AchievementResponseDto> findAll(Long userId) {
        return achievementRepository.findAllWithUserProgress(userId);
    }
}
