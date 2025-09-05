package com.EdumentumBackend.EdumentumBackend.service.redis;

import java.time.LocalDate;

public interface StudyTimeService {
    void increaseStudyTime(Long userId);
    long getStudyTime(Long userId);
    long[][] getStudyMatrix(Long userId, int day);
}
