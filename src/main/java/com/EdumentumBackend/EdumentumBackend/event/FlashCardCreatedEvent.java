package com.EdumentumBackend.EdumentumBackend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FlashCardCreatedEvent extends ApplicationEvent {
    private final Long userId;

    public FlashCardCreatedEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }
}
