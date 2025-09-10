package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.attempt.AttemptReviewDto;
import com.EdumentumBackend.EdumentumBackend.dtos.attempt.SubmitAttemptRequest;

public interface AttemptService {

    AttemptReviewDto submit(Long quizId, Long userId, SubmitAttemptRequest req);
    AttemptReviewDto getReview(Long attemptId, Long userId);
    AttemptReviewDto getLatest(Long quizId, Long userId);
}
