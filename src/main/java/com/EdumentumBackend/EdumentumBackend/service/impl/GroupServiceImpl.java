package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.GroupRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.GroupResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.GroupEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.GroupRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.GroupService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupServiceImpl(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @Override
    public GroupResponseDto createGroup(GroupRequestDto groupRequestDto, Long ownerId) {
        UserEntity owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner not found"));

        GroupEntity group = new GroupEntity();
        group.setName(groupRequestDto.getName());
        group.setDescription(groupRequestDto.getDescription());
        group.setPublic(groupRequestDto.isPublic());
        group.setOwner(owner);
        group.setMemberCount(groupRequestDto.getMemberCount());
        group = groupRepository.save(group);

        return GroupResponseDto.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .isPublic(group.isPublic())
                .ownerId(owner.getUserId())
                .ownerName(owner.getUsername())
                .memberCount(group.getMemberCount())
                .key(group.getKey())
                .createdAt(group.getCreatedAt())
                .build();
    }

    @Override
    public GroupResponseDto updateGroup(GroupRequestDto groupRequestDto, Long groupId, Long ownerId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        UserEntity owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!group.getOwner().getUserId().equals(owner.getUserId())) {
            throw new AccessDeniedException("Only the group owner can update the group");
        }

        group.setName(groupRequestDto.getName());
        group.setDescription(groupRequestDto.getDescription());
        group.setPublic(groupRequestDto.isPublic());
        group.setMemberCount(groupRequestDto.getMemberCount());

        groupRepository.save(group);

        return GroupResponseDto.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .isPublic(group.isPublic())
                .memberCount(group.getMemberCount())
                .key(group.getKey())
                .createdAt(group.getCreatedAt())
                .build();
    }
}
