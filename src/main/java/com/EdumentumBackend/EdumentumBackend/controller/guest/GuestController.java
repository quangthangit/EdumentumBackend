package com.EdumentumBackend.EdumentumBackend.controller.guest;

import com.EdumentumBackend.EdumentumBackend.dtos.role.RoleRequest;
import com.EdumentumBackend.EdumentumBackend.dtos.auth.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetails;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetailsService;
import com.EdumentumBackend.EdumentumBackend.jwt.JwtService;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/guest")
public class GuestController {

    private final UserService userService;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public GuestController(UserService userService,
                           JwtService jwtService,
                           CustomUserDetailsService customUserDetailsService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/set-user-role")
    public ResponseEntity<?> setUserRole(@Valid @RequestBody RoleRequest roleRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", "error",
                    "message", "Unauthorized"
            ));
        }

        Long userId = userDetails.getUserId();

        userService.assignRoleToUser(userId, roleRequest.getRoleName());

        UserResponseDto user = userService.getUserById(userId);
        UserDetails updatedUserDetails = customUserDetailsService.loadUserByUsername(user.getEmail());

        return buildAuthResponse(user, updatedUserDetails, jwtService);
    }


    // ------------------- PRIVATE METHOD -------------------

    private ResponseEntity<?> buildAuthResponse(UserResponseDto user, UserDetails userDetails, JwtService jwtService) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtService.generateToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "User role updated and login successful",
                "data", Map.of(
                        "user", user,
                        "accessToken", accessToken,
                        "refreshToken", refreshToken
                )
        ));
    }
}
