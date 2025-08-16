package com.EdumentumBackend.EdumentumBackend.entity.listener;

import com.EdumentumBackend.EdumentumBackend.entity.PointEntity;
import jakarta.persistence.PrePersist;

public class PointEntityListener {

    @PrePersist
    public void prePersist(PointEntity entity) {
        entity.setPoint(0);
    }
}
