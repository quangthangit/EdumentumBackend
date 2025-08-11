package com.EdumentumBackend.EdumentumBackend.dtos.role;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {
    @NotNull(message = "Role name is required")
    @Pattern(regexp = "ROLE_STUDENT|ROLE_TEACHER", message = "Role must be either ROLE_STUDENT or ROLE_TEACHER")
    private String roleName;
}
