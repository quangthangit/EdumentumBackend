package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.dtos.group.UserGroupResponse;
import com.EdumentumBackend.EdumentumBackend.entity.GroupEntity;
import com.EdumentumBackend.EdumentumBackend.entity.GroupMemberEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMemberEntity, Long> {
    boolean existsByGroupAndUser(GroupEntity group, UserEntity user);

    List<GroupMemberEntity> findAllByUser(UserEntity user);

    GroupMemberEntity findByGroupAndUser(GroupEntity groupEntity, UserEntity userEntity);

    @Query("SELECT new com.EdumentumBackend.EdumentumBackend.dtos.group.UserGroupResponse(gm.user.userId, gm.user.username, gm.user.imageUrl) " +
            "FROM GroupMemberEntity gm WHERE gm.group = :group")
    List<UserGroupResponse> findAllUsersByGroupDto(@Param("group") GroupEntity group);

}
