package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.auth.UserRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.auth.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.PointEntity;
import com.EdumentumBackend.EdumentumBackend.entity.RoleEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.AlreadyExistsException;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.PointRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.RoleService;
import com.EdumentumBackend.EdumentumBackend.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final PointRepository pointRepository;

    public UserServiceImpl(PointRepository pointRepository, UserRepository userRepository,
                           PasswordEncoder passwordEncoder, RoleServiceImpl roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.pointRepository = pointRepository;
    }

    @Transactional
    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        userRepository.findByEmail(userRequestDto.getEmail())
                .ifPresent(u -> { throw new AlreadyExistsException("User with Email " + userRequestDto.getEmail() + " already exists"); });

        RoleEntity role = roleService.findByName("ROLE_GUEST");

        UserEntity user = UserEntity.builder()
                .email(userRequestDto.getEmail())
                .username(userRequestDto.getUsername())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .isActive(true)
                .roles(Collections.singleton(role))
                .build();

        UserEntity savedUser = userRepository.save(user);

        pointRepository.save(PointEntity.builder()
                .userEntity(savedUser)
                .point(10)
                .build());

        return mapToDto(savedUser);
    }

    private UserResponseDto mapToDto(UserEntity user) {
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .isActive(user.getIsActive())
                .roles(user.getRoles())
                .build();
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapToDto)
                .orElseThrow(() -> new NotFoundException("User with Email " + email + " not found"));
    }

    @Transactional
    @Override
    public void assignRoleToUser(Long userId, String roleName) {
        if (!roleName.equals("ROLE_STUDENT") && !roleName.equals("ROLE_TEACHER")) {
            throw new IllegalArgumentException("Invalid role: " + roleName);
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with UserId " + userId + " not found"));

        RoleEntity newRole = roleService.findByName(roleName);

        Set<RoleEntity> roles = new HashSet<>();
        roles.add(newRole);

        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with ID " + id + " not found."));
        userRepository.delete(user);
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with UserId " + userId + " not found"));
        return mapToDto(user);
    }
}
