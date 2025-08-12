package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.GroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
    Page<GroupEntity> findAllByIsPublicTrue(Pageable pageable);
    @Query("""
                SELECT g
                FROM GroupEntity g
                LEFT JOIN GroupMemberEntity gm ON gm.group = g
                WHERE gm.id IS NULL
            """)
    Page<GroupEntity> findGroupsWithoutUsers(Pageable pageable);
}
