package com.EdumentumBackend.EdumentumBackend.entity;

import com.EdumentumBackend.EdumentumBackend.enums.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plan_configurations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanConfigurationEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false)
    private SubscriptionPlan planType;
    
    @Column(name = "feature_id", nullable = false)
    private Long featureId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", insertable = false, updatable = false)
    private FeatureEntity feature;
    
    @Column(name = "limit_value") // null means unlimited
    private Integer limitValue;
    
    @Column(name = "limit_period") // DAILY, WEEKLY, MONTHLY, null for one-time limits
    private String limitPeriod;
    
    @Column(name = "is_allowed", nullable = false)
    private Boolean isAllowed = true;
}