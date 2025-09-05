package com.EdumentumBackend.EdumentumBackend.service.impl.redis;

import com.EdumentumBackend.EdumentumBackend.entity.AttendanceEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.event.AttendanceCreatedEvent;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.AttendanceRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.redis.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final RedisTemplate<String, String> redisTemplate;
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    @Override
    public boolean checkIn(Long userId) {
        String today = LocalDate.now().toString();
        String redisKey = "attendance:" + userId + ":" + today;

        if (redisTemplate.hasKey(redisKey)) {
            return false;
        }

        try {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            AttendanceEntity att = AttendanceEntity.builder()
                    .user(user)
                    .localDate(today)
                    .build();
            attendanceRepository.save(att);
            eventPublisher.publishEvent(new AttendanceCreatedEvent(this, userId));
        } catch (DataIntegrityViolationException e) {
            return false;
        }
        redisTemplate.opsForValue().set(redisKey, "1", 1, TimeUnit.DAYS);
        return true;
    }
}
