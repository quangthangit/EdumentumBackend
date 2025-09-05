package com.EdumentumBackend.EdumentumBackend.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class QuizCreatedEvent extends ApplicationEvent {
    private final Long userId;

    public QuizCreatedEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }
}