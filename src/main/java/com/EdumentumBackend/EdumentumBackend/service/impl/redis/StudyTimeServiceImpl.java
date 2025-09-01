package com.EdumentumBackend.EdumentumBackend.service.impl.redis;

import com.EdumentumBackend.EdumentumBackend.service.redis.StudyTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class StudyTimeServiceImpl implements StudyTimeService {

    private final RedisTemplate<String, String> redisTemplate;

    private String buildKey(Long userId) {
        String today = LocalDate.now().toString();
        return "study:" + userId + ":" + today;
    }

    @Override
    public void increaseStudyTime(Long userId) {
        String key = buildKey(userId);
        redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.expire(key, Duration.ofDays(1));
    }

    @Override
    public long getStudyTime(Long userId) {
        String key = buildKey(userId);
        Object value = redisTemplate.opsForValue().get(key);
        return value == null ? 0 : Long.parseLong(value.toString());
    }
}
