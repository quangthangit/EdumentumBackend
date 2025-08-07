package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_invites", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"group_id", "invited_user_id"})
})
public class GroupInviteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

    @ManyToOne
    @JoinColumn(name = "invited_user_id", nullable = false)
    private UserEntity invitedUser;

    @ManyToOne
    @JoinColumn(name = "invited_by", nullable = false)
    private UserEntity invitedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InviteStatus status = InviteStatus.PENDING;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

