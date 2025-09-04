package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.auth.UserRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.auth.UserResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.user.UserProfileRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.user.UserProfileResponseDto;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);
    UserResponseDto getUserByEmail(String email);
    void assignRoleToUser(Long userId, String roleName);
    void deleteUserById(Long id);
    UserResponseDto getUserById(Long userId);
    UserProfileResponseDto updateUserProfile(UserProfileRequestDto userRequestDto , Long userId);
}
