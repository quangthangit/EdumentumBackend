package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.dtos.group.UserGroupResponse;
import com.EdumentumBackend.EdumentumBackend.entity.GroupEntity;
import com.EdumentumBackend.EdumentumBackend.entity.GroupMemberEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMemberEntity, Long> {
    boolean existsByGroupAndUser(GroupEntity group, UserEntity user);

    List<GroupMemberEntity> findGroupMemberByUserUserId(Long userId);

    GroupMemberEntity findByGroupAndUser(GroupEntity groupEntity, UserEntity userEntity);

    Optional<GroupMemberEntity> findByGroup_IdAndUser_UserId(Long groupId, Long userId);

    void deleteAllByGroup(GroupEntity groupEntity);

    boolean existsByGroup_IdAndUser_UserId(Long groupId, Long userId);

    @Query("SELECT new com.EdumentumBackend.EdumentumBackend.dtos.group.UserGroupResponse(gm.user.userId, gm.user.username, gm.user.imageUrl) " +
            "FROM GroupMemberEntity gm WHERE gm.group = :group")
    List<UserGroupResponse> findAllUsersByGroupDto(@Param("group") GroupEntity group);

}
