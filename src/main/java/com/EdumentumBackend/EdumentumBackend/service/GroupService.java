package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.contribution.ContributionHistoryRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupDetailResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.PaginatedResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GroupService {
    GroupResponseDto createGroup(GroupRequestDto groupRequestDto, Long ownerId);
    GroupResponseDto updateGroup(GroupRequestDto groupRequestDto, String publicGroupId, Long ownerId);
    PaginatedResponse<GroupResponseDto> findAllPublicGroups(Long userId,Pageable pageable,String keyword);
    void joinGroup(String publicGroupId, Long userId) throws BadRequestException;
    List<GroupResponseDto> findByUEntities(Long userId);
    GroupDetailResponse findGroupById(String publicGroupId, Long userId);
    void contributeToGroup(ContributionHistoryRequestDto contributionRequestDto, Long userId);
    void deleteGroup(String publicGroupId, Long userId);
}
