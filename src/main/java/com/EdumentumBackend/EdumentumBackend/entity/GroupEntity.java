package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

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

    @Min(1)
    @Max(50)
    @Column
    private int memberCount;

    @Column(unique = true, nullable = false)
    @NotNull
    private String key;

    @PrePersist
    protected void onCreate() {
        if (this.key == null || this.key.isEmpty()) {
            this.key = generateShortUUIDKey();
        }
        this.createdAt = LocalDateTime.now();
    }

    private String generateShortUUIDKey() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }
}
