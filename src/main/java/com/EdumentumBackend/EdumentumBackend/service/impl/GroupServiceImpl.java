package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.*;
import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupDetailResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.group.UserGroupResponse;
import com.EdumentumBackend.EdumentumBackend.entity.GroupEntity;
import com.EdumentumBackend.EdumentumBackend.entity.GroupMemberEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.enums.RoleGroup;
import com.EdumentumBackend.EdumentumBackend.exception.AuthenticationFailedException;
import com.EdumentumBackend.EdumentumBackend.exception.BadRequestException;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.GroupMemberRepository;
import com.EdumentumBackend.EdumentumBackend.repository.GroupRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
import com.EdumentumBackend.EdumentumBackend.service.GroupService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    public GroupServiceImpl(GroupRepository groupRepository, UserRepository userRepository, GroupMemberRepository groupMemberRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    @Override
    @Transactional
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

        GroupMemberEntity groupMemberEntity = new GroupMemberEntity();
        groupMemberEntity.setGroup(group);
        groupMemberEntity.setUser(owner);
        groupMemberEntity.setRoleGroup(RoleGroup.OWNER);
        groupMemberRepository.save(groupMemberEntity);

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

    @Override
    public PaginatedResponse<GroupResponseDto> findAllPublicGroups(Pageable pageable) {
        Page<GroupResponseDto> page = groupRepository.findAllByIsPublicTrue(pageable)
                .map(group -> GroupResponseDto.builder()
                        .id(group.getId())
                        .name(group.getName())
                        .description(group.getDescription())
                        .ownerId(group.getOwner().getUserId())
                        .ownerName(group.getOwner().getUsername())
                        .key(group.getKey())
                        .isPublic(group.isPublic())
                        .memberCount(group.getMemberCount())
                        .build());
        return PaginatedResponse.fromPage(page);
    }

    @Override
    public void joinGroup(Long groupId, Long userId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean alreadyJoined = groupMemberRepository.existsByGroupAndUser(group, user);

        if (alreadyJoined) {
            throw new BadRequestException("User has already joined the group");
        }

        if (!group.isPublic()) {
            throw new AccessDeniedException("Cannot join a private group");
        }

        int currentMemberCount = groupMemberRepository.countByGroup(group);
        System.out.println(currentMemberCount);
        if (currentMemberCount >= group.getMemberCount()) {
            throw new BadRequestException("Group is full");
        }

        GroupMemberEntity member = new GroupMemberEntity();
        member.setGroup(group);
        member.setUser(user);
        member.setRoleGroup(RoleGroup.MEMBER);
        groupMemberRepository.save(member);
    }

    @Override
    public List<GroupResponseDto> findByUEntities(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<GroupMemberEntity> groupMemberEntities = groupMemberRepository.findAllByUser(user);

        return groupMemberEntities.stream()
                .map(member -> {
                    GroupEntity group = member.getGroup();
                    GroupResponseDto dto = new GroupResponseDto();
                    dto.setId(group.getId());
                    dto.setName(group.getName());
                    dto.setDescription(group.getDescription());
                    dto.setPublic(group.isPublic());
                    dto.setOwnerId(group.getOwner().getUserId());
                    dto.setMemberCount(group.getMemberCount());
                    dto.setCreatedAt(group.getCreatedAt());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public GroupDetailResponse findGroupById(Long groupId, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        GroupMemberEntity groupMemberEntity = groupMemberRepository.findByGroupAndUser(group, user);
        if (groupMemberEntity == null) {
            throw new AuthenticationFailedException("You are not a member of this group");
        }

        List<UserEntity> userEntities = groupMemberRepository.findAllUsersByGroup(group);

        List<UserGroupResponse> userGroupResponses = userEntities.stream()
                .map(member -> {
                    UserGroupResponse dto = new UserGroupResponse();
                    dto.setId(member.getUserId());
                    dto.setUsername(member.getUsername());
                    return dto;
                })
                .toList();

        GroupDetailResponse response = new GroupDetailResponse();
        response.setId(group.getId());
        response.setMember(userGroupResponses.size());
        response.setMemberCount(group.getMemberCount());
        response.setKey(group.getKey());
        response.setOwnerName(group.getOwner().getUsername());
        response.setOwnerId(group.getOwner().getUserId());
        response.setName(group.getName());
        response.setDescription(group.getDescription());
        response.setUserGroupResponseList(userGroupResponses);

        return response;
    }

}