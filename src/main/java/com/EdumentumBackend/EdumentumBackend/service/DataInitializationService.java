package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.entity.FeatureEntity;
import com.EdumentumBackend.EdumentumBackend.entity.PlanConfigurationEntity;
import com.EdumentumBackend.EdumentumBackend.enums.SubscriptionPlan;
import com.EdumentumBackend.EdumentumBackend.repository.FeatureRepository;
import com.EdumentumBackend.EdumentumBackend.repository.PlanConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService {

    private final FeatureRepository featureRepository;
    private final PlanConfigurationRepository planConfigurationRepository;

    @PostConstruct
    @Transactional
    public void initializeDefaultData() {
        log.info("Starting data initialization...");

        // Initialize features
        initializeFeatures();

        // Initialize plan configurations
        initializePlanConfigurations();

        log.info("Data initialization completed successfully!");
    }

    private void initializeFeatures() {
        log.info("Initializing default features...");

        List<FeatureData> defaultFeatures = Arrays.asList(
            new FeatureData("CREATE_QUIZ", "Create Quiz", "Ability to create quizzes", true),
            new FeatureData("CREATE_FLASHCARD", "Create Flashcard", "Ability to create flashcards", false),
            new FeatureData("CREATE_MINDMAP", "Create Mindmap", "Ability to create mindmaps", true),
            new FeatureData("UNLIMITED_STORAGE", "Unlimited Storage", "Unlimited file storage", true),
            new FeatureData("PRIORITY_SUPPORT", "Priority Support", "24/7 priority customer support", true)
        );

        for (FeatureData featureData : defaultFeatures) {
            if (featureRepository.findByFeatureKey(featureData.featureKey).isEmpty()) {
                FeatureEntity feature = FeatureEntity.builder()
                    .featureKey(featureData.featureKey)
                    .featureName(featureData.featureName)
                    .description(featureData.description)
                    .isPremium(featureData.isPremium)
                    .build();

                featureRepository.save(feature);
                log.info("Created feature: {}", featureData.featureKey);
            } else {
                log.debug("Feature already exists: {}", featureData.featureKey);
            }
        }
    }

    private void initializePlanConfigurations() {
        log.info("Initializing default plan configurations...");

        // Get all features by their keys
        FeatureEntity createQuizFeature = featureRepository.findByFeatureKey("CREATE_QUIZ").orElse(null);
        FeatureEntity createFlashcardFeature = featureRepository.findByFeatureKey("CREATE_FLASHCARD").orElse(null);
        FeatureEntity createMindmapFeature = featureRepository.findByFeatureKey("CREATE_MINDMAP").orElse(null);
        FeatureEntity unlimitedStorageFeature = featureRepository.findByFeatureKey("UNLIMITED_STORAGE").orElse(null);
        FeatureEntity prioritySupportFeature = featureRepository.findByFeatureKey("PRIORITY_SUPPORT").orElse(null);

        if (createQuizFeature == null || createFlashcardFeature == null || createMindmapFeature == null ||
            unlimitedStorageFeature == null || prioritySupportFeature == null) {
            log.error("Some features are missing. Cannot initialize plan configurations.");
            return;
        }

        // FREE plan configurations
        createPlanConfigIfNotExists(SubscriptionPlan.FREE, createQuizFeature.getId(), 3, "WEEKLY", true);
        createPlanConfigIfNotExists(SubscriptionPlan.FREE, createFlashcardFeature.getId(), 1, "WEEKLY", true);
        createPlanConfigIfNotExists(SubscriptionPlan.FREE, createMindmapFeature.getId(), 5, "WEEKLY", true);
        createPlanConfigIfNotExists(SubscriptionPlan.FREE, unlimitedStorageFeature.getId(), 100, null, true); // 100MB
        createPlanConfigIfNotExists(SubscriptionPlan.FREE, prioritySupportFeature.getId(), null, null, false);

        // PRO_MONTHLY plan configurations
        createPlanConfigIfNotExists(SubscriptionPlan.PRO_MONTHLY, createQuizFeature.getId(), null, null, true);
        createPlanConfigIfNotExists(SubscriptionPlan.PRO_MONTHLY, createFlashcardFeature.getId(), null, null, true);
        createPlanConfigIfNotExists(SubscriptionPlan.PRO_MONTHLY, createMindmapFeature.getId(), null, null, true);
        createPlanConfigIfNotExists(SubscriptionPlan.PRO_MONTHLY, unlimitedStorageFeature.getId(), 10000, null, true); // 10GB
        createPlanConfigIfNotExists(SubscriptionPlan.PRO_MONTHLY, prioritySupportFeature.getId(), null, null, true);

        // PRO_YEARLY plan configurations
        createPlanConfigIfNotExists(SubscriptionPlan.PRO_YEARLY, createQuizFeature.getId(), null, null, true);
        createPlanConfigIfNotExists(SubscriptionPlan.PRO_YEARLY, createFlashcardFeature.getId(), null, null, true);
        createPlanConfigIfNotExists(SubscriptionPlan.PRO_YEARLY, createMindmapFeature.getId(), null, null, true);
        createPlanConfigIfNotExists(SubscriptionPlan.PRO_YEARLY, unlimitedStorageFeature.getId(), 10000, null, true); // 10GB
        createPlanConfigIfNotExists(SubscriptionPlan.PRO_YEARLY, prioritySupportFeature.getId(), null, null, true);
    }

    private void createPlanConfigIfNotExists(SubscriptionPlan planType, Long featureId,
                                           Integer limitValue, String limitPeriod, Boolean isAllowed) {
        if (planConfigurationRepository.findByPlanTypeAndFeatureId(planType, featureId).isEmpty()) {
            PlanConfigurationEntity config = PlanConfigurationEntity.builder()
                .planType(planType)
                .featureId(featureId)
                .limitValue(limitValue)
                .limitPeriod(limitPeriod)
                .isAllowed(isAllowed)
                .build();

            planConfigurationRepository.save(config);
            log.info("Created plan configuration: {} - Feature ID: {}", planType, featureId);
        } else {
            log.debug("Plan configuration already exists: {} - Feature ID: {}", planType, featureId);
        }
    }

    // Inner class to hold feature data
    private static class FeatureData {
        final String featureKey;
        final String featureName;
        final String description;
        final Boolean isPremium;

        FeatureData(String featureKey, String featureName, String description, Boolean isPremium) {
            this.featureKey = featureKey;
            this.featureName = featureName;
            this.description = description;
            this.isPremium = isPremium;
        }
    }
}
