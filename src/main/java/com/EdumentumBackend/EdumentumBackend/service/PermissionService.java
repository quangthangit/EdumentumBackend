package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.entity.FeatureEntity;
import com.EdumentumBackend.EdumentumBackend.entity.PlanConfigurationEntity;
import com.EdumentumBackend.EdumentumBackend.entity.SubscriptionEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UsageTrackingEntity;
import com.EdumentumBackend.EdumentumBackend.enums.SubscriptionPlan;
import com.EdumentumBackend.EdumentumBackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermissionService {
    
    private final PlanConfigurationRepository planConfigurationRepository;
    private final UsageTrackingRepository usageTrackingRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final FeatureRepository featureRepository;
    
    public boolean canUseFeature(Long userId, String featureKey) {
        // Get feature
        Optional<FeatureEntity> featureOpt = featureRepository.findByFeatureKey(featureKey);
        if (!featureOpt.isPresent()) {
            return false; // Feature doesn't exist
        }
        
        FeatureEntity feature = featureOpt.get();
        
        // Free features are always allowed
        if (!feature.getIsPremium()) {
            return true;
        }
        
        // Get user's subscription
        SubscriptionPlan planType = getUserPlan(userId);
        
        // Get plan configuration for this feature
        Optional<PlanConfigurationEntity> configOpt = planConfigurationRepository
            .findByPlanTypeAndFeatureId(planType, feature.getId());
        
        if (!configOpt.isPresent() || !configOpt.get().getIsAllowed()) {
            return false; // Not allowed in this plan
        }
        
        PlanConfigurationEntity config = configOpt.get();
        
        // Unlimited features
        if (config.getLimitValue() == null) {
            return true;
        }
        
        // Check usage limits
        if (config.getLimitPeriod() != null) {
            // Period-based limits (daily, weekly, monthly)
            LocalDateTime periodStart = getPeriodStart(config.getLimitPeriod());
            
            Optional<UsageTrackingEntity> usageOpt = usageTrackingRepository
                .findByUserIdAndFeatureIdAndPeriodStart(userId, feature.getId(), periodStart);
            
            if (usageOpt.isPresent()) {
                return usageOpt.get().getUsageCount() < config.getLimitValue();
            } else {
                return true; // First use in this period
            }
        } else {
            // One-time limits
            Optional<UsageTrackingEntity> usageOpt = usageTrackingRepository
                .findByUserIdAndFeatureId(userId, feature.getId());
            
            if (usageOpt.isPresent()) {
                return usageOpt.get().getUsageCount() < config.getLimitValue();
            } else {
                return true; // First use ever
            }
        }
    }
    
    public void incrementUsage(Long userId, String featureKey) {
        Optional<FeatureEntity> featureOpt = featureRepository.findByFeatureKey(featureKey);
        if (!featureOpt.isPresent()) return;
        
        FeatureEntity feature = featureOpt.get();
        SubscriptionPlan planType = getUserPlan(userId);
        
        Optional<PlanConfigurationEntity> configOpt = planConfigurationRepository
            .findByPlanTypeAndFeatureId(planType, feature.getId());
        
        if (!configOpt.isPresent()) return;
        
        PlanConfigurationEntity config = configOpt.get();
        
        if (config.getLimitPeriod() != null) {
            // Period-based tracking
            LocalDateTime periodStart = getPeriodStart(config.getLimitPeriod());
            
            Optional<UsageTrackingEntity> usageOpt = usageTrackingRepository
                .findByUserIdAndFeatureIdAndPeriodStart(userId, feature.getId(), periodStart);
            
            if (usageOpt.isPresent()) {
                UsageTrackingEntity usage = usageOpt.get();
                usage.setUsageCount(usage.getUsageCount() + 1);
                usage.setLastUsed(LocalDateTime.now());
                usageTrackingRepository.save(usage);
            } else {
                UsageTrackingEntity newUsage = UsageTrackingEntity.builder()
                    .userId(userId)
                    .featureId(feature.getId())
                    .periodStart(periodStart)
                    .usageCount(1)
                    .lastUsed(LocalDateTime.now())
                    .build();
                usageTrackingRepository.save(newUsage);
            }
        } else {
            // One-time tracking
            Optional<UsageTrackingEntity> usageOpt = usageTrackingRepository
                .findByUserIdAndFeatureId(userId, feature.getId());
            
            if (usageOpt.isPresent()) {
                UsageTrackingEntity usage = usageOpt.get();
                usage.setUsageCount(usage.getUsageCount() + 1);
                usage.setLastUsed(LocalDateTime.now());
                usageTrackingRepository.save(usage);
            } else {
                UsageTrackingEntity newUsage = UsageTrackingEntity.builder()
                    .userId(userId)
                    .featureId(feature.getId())
                    .usageCount(1)
                    .lastUsed(LocalDateTime.now())
                    .build();
                usageTrackingRepository.save(newUsage);
            }
        }
    }
    
    public SubscriptionPlan getUserPlan(Long userId) {
        Optional<SubscriptionEntity> subscriptionOpt = subscriptionRepository.findActiveSubscriptionByUserId(userId);
        return subscriptionOpt.isPresent() ? subscriptionOpt.get().getPlanType() : SubscriptionPlan.FREE;
    }
    
    private LocalDateTime getPeriodStart(String periodType) {
        LocalDateTime now = LocalDateTime.now();
        switch (periodType.toUpperCase()) {
            case "DAILY":
                return now.withHour(0).withMinute(0).withSecond(0);
            case "WEEKLY":
                return now.with(java.time.DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0);
            case "MONTHLY":
                return now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            default:
                return now;
        }
    }
}