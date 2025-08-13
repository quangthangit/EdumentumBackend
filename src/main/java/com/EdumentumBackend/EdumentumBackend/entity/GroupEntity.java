package com.EdumentumBackend.EdumentumBackend.entity;

import com.EdumentumBackend.EdumentumBackend.enums.GroupTier;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "groups")
public class GroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 6, max = 100)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Size(max = 255)
    private String description;

    @Column(name = "is_public")
    private boolean isPublic = true;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "member_count",nullable = false)
    private int memberCount;

    @Min(1)
    @Max(50)
    @Column(name = "member_limit")
    private int memberLimit;

    @Column(unique = true, nullable = false)
    @NotNull
    private String key;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupTier tier = GroupTier.BRONZE;

    @Column(nullable = false)
    private int contributionPoints = 0;

    @PrePersist
    protected void onCreate() {
        if (this.key == null || this.key.isEmpty()) {
            this.key = generateShortUUIDKey();
        }
        this.createdAt = LocalDateTime.now();
    }

    public void addContributionPoints(int points) {
        this.contributionPoints += points;
        GroupTier newTier = GroupTier.fromPoints(this.contributionPoints);
        if (newTier != this.tier) {
            this.tier = newTier;
            System.out.println("🎉 Update " + this.tier);
        }
    }

    private String generateShortUUIDKey() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }
}
