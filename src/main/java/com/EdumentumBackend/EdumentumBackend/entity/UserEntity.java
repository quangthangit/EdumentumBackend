package com.EdumentumBackend.EdumentumBackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull
    @Size(max = 20)
    @Column(nullable = false)
    private String username;

    @NotBlank
    @Size(min = 6)
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = true)
    private String imageUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}