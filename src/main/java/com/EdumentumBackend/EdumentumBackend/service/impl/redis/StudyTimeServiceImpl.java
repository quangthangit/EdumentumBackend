package com.EdumentumBackend.EdumentumBackend.service.impl.redis;

import com.EdumentumBackend.EdumentumBackend.service.redis.StudyTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyTimeServiceImpl implements StudyTimeService {

    private final RedisTemplate<String, String> redisTemplate;

    private int getTimeSlot() {
        int hour = LocalDateTime.now().getHour();
        return hour / 2;
    }

    private String buildKey(Long userId, int slot) {
        String today = LocalDate.now().toString();
        return "study:" + userId + ":" + today + ":slot" + slot;
    }

    @Override
    public void increaseStudyTime(Long userId) {
        int slot = getTimeSlot();
        String key = buildKey(userId, slot);
        redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.expire(key, Duration.ofDays(30));
    }

    @Override
    public long getStudyTime(Long userId) {
        long total = 0;
        for (int slot = 0; slot < 12; slot++) {
            String key = buildKey(userId, slot);
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                total += Long.parseLong(value.toString());
            }
        }
        return total;
    }

    @Override
    public long[][] getStudyMatrix(Long userId, int days) {
        long[][] matrix = new long[days][12];
        LocalDate today = LocalDate.now();

        List<String> allKeys = new ArrayList<>();

        for (int d = 0; d < days; d++) {
            LocalDate date = today.minusDays(d);
            for (int slot = 0; slot < 12; slot++) {
                allKeys.add("study:" + userId + ":" + date + ":slot" + slot);
            }
        }

        List<String> values = redisTemplate.opsForValue().multiGet(allKeys);

        int index = 0;
        for (int d = 0; d < days; d++) {
            for (int slot = 0; slot < 12; slot++) {
                String v = null;
                if (values != null) {
                    v = values.get(index++);
                }
                matrix[d][slot] = v == null ? 0 : Long.parseLong(v);
            }
        }
        return matrix;
    }
}
