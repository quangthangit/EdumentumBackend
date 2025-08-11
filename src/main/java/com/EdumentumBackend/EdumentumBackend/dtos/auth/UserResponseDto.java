package com.EdumentumBackend.EdumentumBackend.dtos.auth;

import com.EdumentumBackend.EdumentumBackend.entity.RoleEntity;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String username;
    private String email;
    private Set<RoleEntity> roles;
    private Boolean isActive;
}
