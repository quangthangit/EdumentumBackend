package com.EdumentumBackend.EdumentumBackend.controller.auth;

import com.EdumentumBackend.EdumentumBackend.dtos.UserRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.UserRequestLoginDto;
import com.EdumentumBackend.EdumentumBackend.dtos.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.exception.AuthenticationFailedException;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.jwt.CustomUserDetailsService;
import com.EdumentumBackend.EdumentumBackend.jwt.JwtService;
import com.EdumentumBackend.EdumentumBackend.service.GoogleTokenVerifierService;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final GoogleTokenVerifierService googleTokenVerifierService;

    public AuthController(AuthenticationManager authManager,
                          JwtService jwtService,
                          UserService userService,
                          CustomUserDetailsService customUserDetailsService,
                          GoogleTokenVerifierService googleTokenVerifierService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
        this.googleTokenVerifierService = googleTokenVerifierService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserRequestLoginDto loginDto) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
            );

            UserResponseDto user = userService.getUserByEmail(loginDto.getEmail());
            return ResponseEntity.ok(buildAuthResponse(user, authentication, "User login successfully"));

        } catch (BadCredentialsException ex) {
            throw new AuthenticationFailedException("Invalid username or password");
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred during login");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequestDto registerDto) {
        UserResponseDto user = userService.createUser(registerDto);
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerDto.getEmail(), registerDto.getPassword())
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(buildAuthResponse(user, authentication, "User registered successfully"));
    }

    // ------------------- LOGIN GOOGLE -------------------

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Missing Google token"
            ));
        }

        try {
            GoogleIdToken.Payload payload = googleTokenVerifierService.verifyToken(token);
            String email = payload.getEmail();
            String username = (String) payload.get("name");

            UserResponseDto user;
            try {
                user = userService.getUserByEmail(email);
            } catch (NotFoundException e) {
                UserRequestDto newUser = new UserRequestDto();
                newUser.setEmail(email);
                newUser.setPassword(UUID.randomUUID().toString());
                newUser.setUsername(username);
                user = userService.createUser(newUser);
            }

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return ResponseEntity.ok(buildAuthResponse(user, authentication, "Login with Google successful"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "message", "Invalid Google ID token"
            ));
        }
    }

    // ------------------- PRIVATE METHOD -------------------

    private Map<String, Object> buildAuthResponse(UserResponseDto user, Authentication auth, String message) {
        String accessToken = jwtService.generateToken(auth);
        String refreshToken = jwtService.generateRefreshToken(auth);

        return Map.of(
                "status", "success",
                "message", message,
                "data", Map.of(
                        "user", user,
                        "accessToken", accessToken,
                        "refreshToken", refreshToken
                )
        );
    }
}
