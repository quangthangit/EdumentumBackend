package com.EdumentumBackend.EdumentumBackend.entity;

import com.EdumentumBackend.EdumentumBackend.entity.listener.UserEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(UserEntityListener.class)
public class UserEntity extends BaseEntity {

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
    private Boolean isActive;

    @Column(nullable = true)
    private String imageUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles;
}