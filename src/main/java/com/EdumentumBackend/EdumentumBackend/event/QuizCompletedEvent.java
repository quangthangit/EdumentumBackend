package com.EdumentumBackend.EdumentumBackend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class QuizCompletedEvent extends ApplicationEvent {
    private final Long userId;

    public QuizCompletedEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }
}
