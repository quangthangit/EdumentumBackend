package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.contribution.ContributionHistoryResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.ContributionHistoryEntity;
import com.EdumentumBackend.EdumentumBackend.entity.GroupEntity;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.ContributionHistoryRepository;
import com.EdumentumBackend.EdumentumBackend.repository.GroupRepository;
import com.EdumentumBackend.EdumentumBackend.service.ContributionHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContributionHistoryServiceImpl implements ContributionHistoryService {

    private final GroupRepository groupRepository;
    private final ContributionHistoryRepository contributionHistoryRepository;

    public ContributionHistoryServiceImpl(GroupRepository groupRepository,ContributionHistoryRepository contributionHistoryRepository) {
        this.contributionHistoryRepository = contributionHistoryRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public List<ContributionHistoryResponseDto> getContributionHistory(Long groupId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        List<ContributionHistoryEntity> histories = contributionHistoryRepository.findByGroup(group);

        return histories.stream().map(history -> {
            ContributionHistoryResponseDto dto = new ContributionHistoryResponseDto();
            dto.setId(history.getId());
            dto.setUserId(history.getUser().getUserId());
            dto.setUsername(history.getUser().getUsername());
            dto.setImageUrl(history.getUser().getImageUrl());
            dto.setPoint(history.getPoints());
            dto.setMessage(history.getMessage());
            return dto;
        }).toList();
    }
}
