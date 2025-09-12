package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.PaginatedResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.achievement.AchievementResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.achievement.AchievementSummaryResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.AchievementEntity;
import com.EdumentumBackend.EdumentumBackend.enums.Rarity;
import com.EdumentumBackend.EdumentumBackend.repository.AchievementRepository;
import com.EdumentumBackend.EdumentumBackend.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;

    @Override
    public PaginatedResponse<AchievementResponseDto> findAll(Long userId, String keyword, Rarity rarity, Boolean achieved, Pageable pageable) {
        Page<AchievementResponseDto> achievementResponseDtos = achievementRepository.findAllWithUserProgress(userId, keyword, rarity, achieved, pageable);
        return PaginatedResponse.fromPage(achievementResponseDtos);
    }

    @Override
    public AchievementSummaryResponseDto summaryAchievementSummaryResponseDto(Long userId) {
        return achievementRepository.getAchievementSummary(userId);
    }
}
