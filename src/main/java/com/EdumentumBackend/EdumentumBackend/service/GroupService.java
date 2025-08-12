package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupDetailResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.PaginatedResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GroupService {
    GroupResponseDto createGroup(GroupRequestDto groupRequestDto, Long ownerId);
    GroupResponseDto updateGroup(GroupRequestDto groupRequestDto, Long groupId, Long ownerId);
    PaginatedResponse<GroupResponseDto> findAllPublicGroups(Long userId,Pageable pageable);
    void joinGroup(Long groupId, Long userId) throws BadRequestException;
    List<GroupResponseDto> findByUEntities(Long userId);
    GroupDetailResponse findGroupById(Long groupId, Long userId);
}
