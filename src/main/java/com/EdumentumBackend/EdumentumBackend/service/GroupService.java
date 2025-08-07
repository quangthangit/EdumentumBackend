package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.GroupRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.GroupResponseDto;

public interface GroupService {
    GroupResponseDto createGroup(GroupRequestDto groupRequestDto,Long ownerId);
    GroupResponseDto updateGroup(GroupRequestDto groupRequestDto,Long groupId,Long ownerId);
}
