package com.EdumentumBackend.EdumentumBackend.entity;


import com.EdumentumBackend.EdumentumBackend.enums.MindMapType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mind_maps")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MindMapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "data", columnDefinition = "TEXT")
    private String data; // JSON string containing nodes and edges

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MindMapType type = MindMapType.DEFAULT;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
