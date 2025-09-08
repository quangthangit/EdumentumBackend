package com.EdumentumBackend.EdumentumBackend.entity;

import com.EdumentumBackend.EdumentumBackend.enums.Rarity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "achievements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String icon;
    private String title;
    private String description;
    private int targetValue;
    private int points;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rarity rarity;
}
