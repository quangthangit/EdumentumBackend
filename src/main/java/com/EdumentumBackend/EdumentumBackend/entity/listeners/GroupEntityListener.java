package com.EdumentumBackend.EdumentumBackend.entity.listeners;

import com.EdumentumBackend.EdumentumBackend.entity.GroupEntity;
import com.EdumentumBackend.EdumentumBackend.enums.GroupTier;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class GroupEntityListener {

    @PrePersist
    public void updateTier(GroupEntity groupEntity) {
        GroupTier groupTier = GroupTier.fromPoints(groupEntity.getContributionPoints());
        groupEntity.setTier(groupTier);
    }
}
