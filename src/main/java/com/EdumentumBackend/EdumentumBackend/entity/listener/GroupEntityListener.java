package com.EdumentumBackend.EdumentumBackend.entity.listener;

import com.EdumentumBackend.EdumentumBackend.entity.GroupEntity;
import com.EdumentumBackend.EdumentumBackend.enums.GroupTier;
import jakarta.persistence.PrePersist;

import java.util.UUID;

public class GroupEntityListener {

    @PrePersist
    public void prePersist(GroupEntity entity) {
        entity.setContributionPoints(0);
        entity.setKey(UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase());
        entity.setTier(GroupTier.BRONZE);
        entity.setMemberCount(1);
    }
}
