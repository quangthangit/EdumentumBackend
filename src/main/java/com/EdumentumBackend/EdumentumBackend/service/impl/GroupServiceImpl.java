package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.*;
import com.EdumentumBackend.EdumentumBackend.dtos.contribution.ContributionHistoryRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupDetailResponse;
import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupResponseDto;
import com.EdumentumBackend.EdumentumBackend.dtos.group.UserGroupResponse;
import com.EdumentumBackend.EdumentumBackend.entity.*;
import com.EdumentumBackend.EdumentumBackend.enums.RoleGroup;
import com.EdumentumBackend.EdumentumBackend.exception.AuthenticationFailedException;
import com.EdumentumBackend.EdumentumBackend.exception.BadRequestException;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.*;
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
    private final RepositoryRepository repositoryRepository;
    private final ContributionHistoryRepository contributionHistoryRepository;
    private final PointRepository pointRepository;

    public GroupServiceImpl(PointRepository pointRepository,ContributionHistoryRepository contributionHistoryRepository,RepositoryRepository repositoryRepository,GroupRepository groupRepository, UserRepository userRepository, GroupMemberRepository groupMemberRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.repositoryRepository = repositoryRepository;
        this.contributionHistoryRepository = contributionHistoryRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.pointRepository = pointRepository;
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
        group.setMemberCount(1);
        group.setContributionPoints(0);
        group.setMemberLimit(groupRequestDto.getMemberLimit());

        group = groupRepository.save(group);

        RepositoryEntity repositoryEntity = new RepositoryEntity();
        repositoryEntity.setGroup(group);
        repositoryRepository.save(repositoryEntity);

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
                .contributionPoints(group.getContributionPoints())
                .tier(group.getTier())
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
        group.setMemberLimit(groupRequestDto.getMemberLimit());

        groupRepository.save(group);

        return GroupResponseDto.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .isPublic(group.isPublic())
                .contributionPoints(group.getContributionPoints())
                .tier(group.getTier())
                .memberCount(group.getMemberCount())
                .memberLimit(group.getMemberLimit())
                .key(group.getKey())
                .createdAt(group.getCreatedAt())
                .build();
    }

    @Override
    public PaginatedResponse<GroupResponseDto> findAllPublicGroups(Long userId,Pageable pageable) {
        Page<GroupResponseDto> page = groupRepository.findGroupsNotContainingUserDto(userId,pageable);
        return PaginatedResponse.fromPage(page);
    }

    @Override
    @Transactional
    public void joinGroup(Long groupId, Long userId) {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new BadRequestException("User has already joined the group");
        }
        if (!group.isPublic()) {
            throw new AccessDeniedException("Cannot join a private group");
        }

        int updated = groupRepository.incrementMemberCount(groupId);
        if (updated == 0) {
            throw new BadRequestException("Group is full");
        }

        GroupMemberEntity member = GroupMemberEntity.builder()
                .group(group)
                .user(user)
                .roleGroup(RoleGroup.MEMBER)
                .build();
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
                    dto.setMemberLimit(group.getMemberLimit());
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

        List<UserGroupResponse> userGroupResponses = groupMemberRepository.findAllUsersByGroupDto(group);
        GroupDetailResponse response = new GroupDetailResponse();
        response.setId(group.getId());
        response.setMemberCount(group.getMemberCount());
        response.setKey(group.getKey());
        response.setOwnerName(group.getOwner().getUsername());
        response.setOwnerId(group.getOwner().getUserId());
        response.setName(group.getName());
        response.setDescription(group.getDescription());
        response.setMemberLimit(group.getMemberLimit());
        response.setUserGroupResponseList(userGroupResponses);
        return response;
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

}