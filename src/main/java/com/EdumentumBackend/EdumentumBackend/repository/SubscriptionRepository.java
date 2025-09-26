package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.SubscriptionEntity;
import com.EdumentumBackend.EdumentumBackend.enums.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    List<SubscriptionEntity> findByUserId(Long userId);
    
    @Query("SELECT s FROM SubscriptionEntity s WHERE s.userId = :userId AND s.isActive = true ORDER BY s.startDate DESC")
    List<SubscriptionEntity> findActiveSubscriptionsByUserId(Long userId);
    
    default Optional<SubscriptionEntity> findActiveSubscriptionByUserId(Long userId) {
        List<SubscriptionEntity> activeSubscriptions = findActiveSubscriptionsByUserId(userId);
        return activeSubscriptions.isEmpty() ? Optional.empty() : Optional.of(activeSubscriptions.get(0));
    }
    
    List<SubscriptionEntity> findByPlanTypeAndIsActive(SubscriptionPlan planType, Boolean isActive);
}