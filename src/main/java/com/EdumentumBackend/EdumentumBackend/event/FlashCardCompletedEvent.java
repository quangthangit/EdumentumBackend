package com.EdumentumBackend.EdumentumBackend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FlashCardCompletedEvent extends ApplicationEvent {
    private final Long userId;

    public FlashCardCompletedEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }
}
