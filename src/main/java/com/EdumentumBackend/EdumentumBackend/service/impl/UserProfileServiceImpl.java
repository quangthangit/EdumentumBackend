package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.user.UserAttendanceResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.user.UserProfileInfoResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.AttendanceEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserProfileEntity;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.AttendanceRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserProfileRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.UserProfileService;
import com.EdumentumBackend.EdumentumBackend.service.redis.StudyTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final StudyTimeService studyTimeService;
    private final AttendanceRepository attendanceRepository;

    @Override
    public UserProfileInfoResponseDto getUserProfileInfo(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with UserId " + userId + " not found"));

        UserProfileEntity profile = userProfileRepository.findByUserUserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        Long time = studyTimeService.getStudyTime(user.getUserId());

        new UserProfileInfoResponseDto();
        return UserProfileInfoResponseDto.builder()
                .id(profile.getId())
                .levelProgress(profile.getLevelProgress())
                .streak(profile.getStreak())
                .totalAttendance(profile.getTotalAttendance())
                .totalStudyTime(profile.getTotalStudyTime())
                .totalStudyTimeToday(time)
                .totalFlashCardCreated(profile.getTotalFlashCardCreated())
                .totalQuizzesCompleted(profile.getTotalQuizzesCompleted())
                .totalFlashCardCompleted(profile.getTotalFlashCardCompleted())
                .createdAt(user.getCreatedAt())
                .totalQuizzesCreated(profile.getTotalQuizzesCreated())
                .username(user.getUsername())
                .build();
    }

    @Override
    public List<UserAttendanceResponseDto> findAllByUserId(Long userId) {
        List<AttendanceEntity> attendanceEntities = attendanceRepository.findByUserUserId(userId);
        return attendanceEntities.stream()
                .map(entity -> UserAttendanceResponseDto.builder()
                        .localDate(entity.getLocalDate())
                        .build())
                .toList();
    }
}
