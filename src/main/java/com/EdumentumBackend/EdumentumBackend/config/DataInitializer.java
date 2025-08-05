package com.EdumentumBackend.EdumentumBackend.config;

import com.EdumentumBackend.EdumentumBackend.entity.RoleEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.repository.RoleRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository,PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            roleRepository.save(RoleEntity.builder().name("ROLE_ADMIN").build());
            roleRepository.save(RoleEntity.builder().name("ROLE_GUEST").build());
            roleRepository.save(RoleEntity.builder().name("ROLE_STUDENT").build());
            roleRepository.save(RoleEntity.builder().name("ROLE_TEACHER").build());
        }
        Optional<UserEntity> optionalUser = userRepository.findByEmail("admin@gmail.com");

        if (optionalUser.isEmpty()) {
            RoleEntity adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

            UserEntity adminUser = UserEntity.builder()
                    .username("Admin")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Collections.singleton(adminRole))
                    .isActive(true)
                    .build();
            userRepository.save(adminUser);
        }
    }
}

