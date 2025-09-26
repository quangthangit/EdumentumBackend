package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.UsageTrackingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UsageTrackingRepository extends JpaRepository<UsageTrackingEntity, Long> {
    Optional<UsageTrackingEntity> findByUserIdAndFeatureIdAndPeriodStart(Long userId, Long featureId, LocalDateTime periodStart);
    Optional<UsageTrackingEntity> findByUserIdAndFeatureId(Long userId, Long featureId);
    
    @Query("SELECT COALESCE(SUM(u.usageCount), 0) FROM UsageTrackingEntity u WHERE u.userId = :userId AND u.featureId = :featureId AND u.periodStart >= :weekStart")
    Integer getWeeklyUsageCount(Long userId, Long featureId, LocalDateTime weekStart);
}