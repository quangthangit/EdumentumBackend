package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.PaginatedResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.achievement.AchievementResponseDto;
import com.EdumentumBackend.EdumentumBackend.enums.Rarity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AchievementService {
    PaginatedResponse<AchievementResponseDto> findAll(Long userId,String keyword, Rarity rarity, Boolean achieved, Pageable pageable);
}
