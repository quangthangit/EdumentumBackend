package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "features")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureEntity extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "feature_key", nullable = false, unique = true)
    private String featureKey; // e.g., "CREATE_QUIZ", "CREATE_MINDMAP", "UNLIMITED_STORAGE"
    
    @Column(name = "feature_name", nullable = false)
    private String featureName; // Human readable name
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "is_premium", nullable = false)
    private Boolean isPremium = false;
}