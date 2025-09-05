package com.EdumentumBackend.EdumentumBackend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AttendanceCreatedEvent extends ApplicationEvent {
    private final Long userId;

    public AttendanceCreatedEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }
}
