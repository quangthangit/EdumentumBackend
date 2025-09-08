package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.dtos.achievement.AchievementResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.AchievementEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<AchievementEntity, Integer> {
    @Query("""
                SELECT new com.EdumentumBackend.EdumentumBackend.dtos.achievement.AchievementResponseDto(
                    a.id, a.icon, a.title, a.description, a.targetValue, a.points, a.rarity,
                    COALESCE(ua.currentValue, 0), COALESCE(ua.achieved, false)
                )
                FROM AchievementEntity a
                LEFT JOIN UserAchievementEntity ua 
                    ON a.id = ua.achievement.id AND ua.user.userId = :userId
            """)
    List<AchievementResponseDto> findAllWithUserProgress(@Param("userId") Long userId);
}
