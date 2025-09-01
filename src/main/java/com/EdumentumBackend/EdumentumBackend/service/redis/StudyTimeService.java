package com.EdumentumBackend.EdumentumBackend.service.redis;

public interface StudyTimeService {
    void increaseStudyTime(Long userId);
    long getStudyTime(Long userId);
}
