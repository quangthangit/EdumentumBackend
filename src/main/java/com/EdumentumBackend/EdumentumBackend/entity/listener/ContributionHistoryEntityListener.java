package com.EdumentumBackend.EdumentumBackend.entity.listener;

import com.EdumentumBackend.EdumentumBackend.entity.ContributionHistoryEntity;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

public class ContributionHistoryEntityListener {

    @PrePersist
    public void prePersist(ContributionHistoryEntity entity) {
        entity.setCreatedAt(LocalDateTime.now());
    }
}
