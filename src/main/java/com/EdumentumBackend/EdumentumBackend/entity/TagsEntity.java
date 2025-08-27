package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagsEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 100)
    private String slug;

    // SEO & Metadata
    @Column(name = "meta_title", length = 160)
    private String metaTitle;

    @Column(name = "meta_description", length = 160)
    private String metaDescription;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Display properties
//    @Column(name = "icon", length = 50)
//    private String icon = "tag";
//
//    @Column(name = "color", length = 7)
//    private String color = "#6B7280";
//
//    @Column(name = "background_color", length = 7)
//    private String backgroundColor;

    // Statistics & Analytics
    @Column(name = "usage_count")
    private Integer usageCount = 0;

    @Column(name = "popularity_score", precision = 10, scale = 2)
    private BigDecimal popularityScore = BigDecimal.ZERO;

    @Column(name = "quiz_count")
    private Integer quizCount = 0;

    @Column(name = "total_attempts")
    private Integer totalAttempts = 0;

    @Column(name = "avg_success_rate", precision = 5, scale = 2)
    private BigDecimal avgSuccessRate = BigDecimal.ZERO;

    // Status & Management
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "is_trending")
    private Boolean isTrending = false;

    @Column(name = "display_order")
    private Integer displayOrder = 0;
}
