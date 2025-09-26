package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.PlanConfigurationEntity;
import com.EdumentumBackend.EdumentumBackend.enums.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanConfigurationRepository extends JpaRepository<PlanConfigurationEntity, Long> {
    Optional<PlanConfigurationEntity> findByPlanTypeAndFeatureId(SubscriptionPlan planType, Long featureId);
    List<PlanConfigurationEntity> findByPlanType(SubscriptionPlan planType);
}