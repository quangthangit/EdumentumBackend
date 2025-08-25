package com.EdumentumBackend.EdumentumBackend.entity.listener;

import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import jakarta.persistence.PrePersist;

public class UserEntityListener {

    @PrePersist
    public void prePersist(UserEntity entity) {
        if (entity.getIsActive() == null) {
            entity.setIsActive(true);
        }
    }
}
