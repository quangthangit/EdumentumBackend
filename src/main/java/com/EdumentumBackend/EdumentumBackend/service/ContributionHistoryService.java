package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.contribution.ContributionHistoryResponseDto;

import java.util.List;

public interface ContributionHistoryService {
    List<ContributionHistoryResponseDto> getContributionHistory(Long groupId);
}
