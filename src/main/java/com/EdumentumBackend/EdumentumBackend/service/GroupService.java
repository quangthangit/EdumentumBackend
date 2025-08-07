package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.GroupRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.GroupResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.PaginatedResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Pageable;

public interface GroupService {
    GroupResponseDto createGroup(GroupRequestDto groupRequestDto, Long ownerId);
    GroupResponseDto updateGroup(GroupRequestDto groupRequestDto, Long groupId, Long ownerId);
    PaginatedResponse<GroupResponseDto> findAllPublicGroups(Pageable pageable);
    void joinGroup(Long groupId, Long userId) throws BadRequestException;
}
