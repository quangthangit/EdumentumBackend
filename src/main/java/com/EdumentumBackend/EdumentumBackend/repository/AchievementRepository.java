package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.dtos.achievement.AchievementResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.AchievementEntity;
import com.EdumentumBackend.EdumentumBackend.enums.Rarity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
                        WHERE (:rarity IS NULL OR a.rarity = :rarity)
                  AND (:achieved IS NULL OR COALESCE(ua.achieved, false) = :achieved)
                  AND (:keyword IS NULL OR :keyword = '' OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<AchievementResponseDto> findAllWithUserProgress(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("rarity") Rarity rarity,
            @Param("achieved") Boolean achieved,
            Pageable pageable);
}

