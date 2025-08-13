package com.EdumentumBackend.EdumentumBackend.repository;

import com.EdumentumBackend.EdumentumBackend.entity.ContributionHistoryEntity;
import com.EdumentumBackend.EdumentumBackend.entity.GroupEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContributionHistoryRepository extends JpaRepository<ContributionHistoryEntity, Long> {
    List<ContributionHistoryEntity> findByGroup(GroupEntity group);
}
