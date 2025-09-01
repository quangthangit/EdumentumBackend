package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.user.UserAttendanceResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.user.UserProfileInfoResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.AttendanceEntity;

import java.util.List;

public interface UserProfileService {
    UserProfileInfoResponseDto getUserProfileInfo(Long userId);
    List<UserAttendanceResponseDto> findAllByUserId(Long userId);
}
