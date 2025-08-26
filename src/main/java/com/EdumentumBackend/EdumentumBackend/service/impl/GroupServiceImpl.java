package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.*;
import com.EdumentumBackend.EdumentumBackend.dtos.contribution.ContributionHistoryRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupDetailResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.group.UserGroupResponse;
import com.EdumentumBackend.EdumentumBackend.entity.*;
import com.EdumentumBackend.EdumentumBackend.enums.RoleGroup;
import com.EdumentumBackend.EdumentumBackend.exception.BadRequestException;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.*;
import com.EdumentumBackend.EdumentumBackend.service.GroupService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ContributionHistoryRepository contributionHistoryRepository;
    private final PointRepository pointRepository;
    private final FolderRepository folderRepository;

    private GroupResponseDto mapGroup(GroupEntity entity) {
        return GroupResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .isPublic(entity.isPublic())
                .contributionPoints(entity.getContributionPoints())
                .tier(entity.getTier())
                .memberLimit(entity.getMemberLimit())
                .ownerId(entity.getOwner().getUserId())
                .ownerName(entity.getOwner().getUsername())
                .memberCount(entity.getMemberCount())
                .key(entity.getKey())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private GroupDetailResponse mapGroupDetail(GroupEntity entity, List<UserGroupResponse> userGroupResponse) {
        return GroupDetailResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .contributionPoints(entity.getContributionPoints())
                .groupTier(entity.getTier())
                .ownerId(entity.getOwner().getUserId())
                .ownerName(entity.getOwner().getUsername())
                .memberCount(entity.getMemberCount())
                .key(entity.getKey())
                .memberLimit(entity.getMemberLimit())
                .createdAt(entity.getCreatedAt())
                .userGroupResponseList(userGroupResponse)
                .build();
    }

    @Override
    @Transactional
    public GroupResponseDto createGroup(GroupRequestDto dto, Long ownerId) {
        UserEntity owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Owner not found"));

        GroupEntity group = groupRepository.save(
                GroupEntity.builder()
                        .name(dto.getName())
                        .description(dto.getDescription())
                        .isPublic(dto.isPublic())
                        .owner(owner)
                        .memberLimit(dto.getMemberLimit())
                        .build()
        );

        groupMemberRepository.save(
                GroupMemberEntity.builder()
                        .group(group)
                        .user(owner)
                        .roleGroup(RoleGroup.OWNER)
                        .build()
        );

        return mapGroup(group);
    }


    @Override
    @Transactional
    public GroupResponseDto updateGroup(GroupRequestDto dto, Long groupId, Long ownerId) {
        GroupEntity group = groupRepository.findByIdAndOwnerUserId(groupId, ownerId)
                .orElseThrow(() -> new AccessDeniedException("Only the group owner can update the group"));

        group.setName(dto.getName());
        group.setDescription(dto.getDescription());
        group.setPublic(dto.isPublic());
        group.setMemberLimit(dto.getMemberLimit());

        return mapGroup(groupRepository.save(group));
    }

    @Override
    public PaginatedResponse<GroupResponseDto> findAllPublicGroups(Long userId, Pageable pageable, String keyword) {
        Page<GroupResponseDto> page = groupRepository.findGroupsNotContainingUserDto(userId, keyword, pageable);
        return PaginatedResponse.fromPage(page);
    }

    @Override
    @Transactional
    public void joinGroup(Long groupId, Long userId) {
        if (groupMemberRepository.existsByGroup_IdAndUser_UserId(groupId, userId)) {
            throw new BadRequestException("User has already joined the group");
        }

        int updated = groupRepository.incrementMemberCountIfJoinable(groupId);
        if (updated == 0) {
            throw new BadRequestException("Group is full or private");
        }
        groupMemberRepository.save(GroupMemberEntity.builder()
                .group(GroupEntity.builder().id(groupId).build())
                .user(UserEntity.builder().userId(userId).build())
                .roleGroup(RoleGroup.MEMBER)
                .build());
    }

    @Override
    public List<GroupResponseDto> findByUEntities(Long userId) {
        return groupMemberRepository.findGroupMemberByUserUserId(userId)
                .stream()
                .map(member -> mapGroup(member.getGroup()))
                .toList();
    }

    @Override
    public GroupDetailResponse findGroupById(Long groupId, Long userId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));
        List<UserGroupResponse> userGroupResponses = groupMemberRepository.findAllUsersByGroupDto(group);
        return mapGroupDetail(group,userGroupResponses);
    }

    @Override
    @Transactional
    public void contributeToGroup(ContributionHistoryRequestDto dto, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        GroupEntity group = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new NotFoundException("Group not found"));

        if (!groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new BadRequestException("User is not a member of this group");
        }

        PointEntity point = pointRepository.findByUserEntity(user);
        if (point == null || point.getPoint() < dto.getPoints()) {
            throw new BadRequestException("User does not have enough points to contribute");
        }

        point.setPoint(point.getPoint() - dto.getPoints());
        pointRepository.saveAndFlush(point);

        ContributionHistoryEntity history = ContributionHistoryEntity.builder()
                .user(user)
                .group(group)
                .points(dto.getPoints())
                .message(dto.getMessage())
                .build();
        contributionHistoryRepository.save(history);

        groupRepository.addContributionPoints(group.getId(), dto.getPoints());
    }

    @Override
    @Transactional
    public void deleteGroup(Long groupId, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

        if (!group.getOwner().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("Only the group owner can update the group");
        }
        groupMemberRepository.deleteAllByGroup(group);
        folderRepository.deleteAllByGroupId(groupId);
        groupRepository.deleteById(groupId);
    }

}