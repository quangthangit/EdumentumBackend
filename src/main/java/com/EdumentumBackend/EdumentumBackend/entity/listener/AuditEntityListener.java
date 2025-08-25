package com.EdumentumBackend.EdumentumBackend.entity.listener;

import com.EdumentumBackend.EdumentumBackend.entity.BaseEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

public class AuditEntityListener {

    @PrePersist
    public void setCreatedAt(BaseEntity entity) {
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
    }

    @PreUpdate
    public void setUpdatedAt(BaseEntity entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }
}
