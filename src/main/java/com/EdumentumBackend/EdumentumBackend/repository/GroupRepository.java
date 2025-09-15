package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.dtos.group.GroupResponseDto;
import com.EdumentumBackend.EdumentumBackend.entity.GroupEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
    @Query("SELECT new com.EdumentumBackend.EdumentumBackend.dtos.group.GroupResponseDto(" +
            "g.publicId, g.name, g.description, g.isPublic, g.owner.userId, g.owner.username, g.memberCount, g.memberLimit, g.key, g.createdAt, g.contributionPoints, g.tier) " +
            "FROM GroupEntity g " +
            "LEFT JOIN GroupMemberEntity gm ON gm.group = g AND gm.user.userId = :userId " +
            "WHERE g.isPublic = true AND gm.id IS NULL " +
            "AND (LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "     OR LOWER(g.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<GroupResponseDto> findGroupsNotContainingUserDto(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable);

    Optional<GroupEntity> findGroupByPublicId(String publicId);

    @Modifying
    @Query("""
                UPDATE GroupEntity g
                SET g.memberCount = g.memberCount + 1
                WHERE g.id = :groupId AND g.isPublic = true AND g.memberCount < g.memberLimit
            """)
    int incrementMemberCountIfJoinable(@Param("groupId") Long groupId);


    @Modifying
    @Query("UPDATE GroupEntity g SET g.contributionPoints = g.contributionPoints + :points WHERE g.id = :groupId")
    void addContributionPoints(@Param("groupId") Long groupId, @Param("points") int points);

    @Query("SELECT g.owner.userId FROM GroupEntity g WHERE g.id = :groupId")
    Optional<GroupEntity> findOwnerIdByGroupId(@Param("groupId") Long groupId, Long ownerId);

    Optional<GroupEntity> findByIdAndOwnerUserId(Long groupId, Long ownerId);
}
