package com.EdumentumBackend.EdumentumBackend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AttendanceCreatedEvent extends ApplicationEvent {
    private final Long userId;
    private final Boolean loggedYesterday;

    public AttendanceCreatedEvent(Object source, Long userId,Boolean loggedYesterday) {
        super(source);
        this.userId = userId;
        this.loggedYesterday = loggedYesterday;
    }
}
