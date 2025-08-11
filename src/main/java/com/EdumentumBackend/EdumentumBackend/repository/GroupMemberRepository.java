package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.GroupEntity;
import com.EdumentumBackend.EdumentumBackend.entity.GroupMemberEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMemberEntity, Long> {
    boolean existsByGroupAndUser(GroupEntity group, UserEntity user);
    int countByGroup(GroupEntity group);
    List<GroupMemberEntity> findAllByUser(UserEntity user);
    GroupMemberEntity findByGroupAndUser(GroupEntity groupEntity, UserEntity userEntity);
    @Query("SELECT gm.user FROM GroupMemberEntity gm WHERE gm.group = :group")
    List<UserEntity> findAllUsersByGroup(@Param("group") GroupEntity group);
}
