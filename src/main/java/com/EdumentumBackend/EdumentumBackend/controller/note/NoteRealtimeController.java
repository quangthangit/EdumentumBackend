package com.EdumentumBackend.EdumentumBackend.controller.note;

import com.EdumentumBackend.EdumentumBackend.dtos.note.NoteRealtimeDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NoteRealtimeController {

    private final SimpMessagingTemplate messagingTemplate;

    public NoteRealtimeController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/note.events")
    public void forward(@Payload NoteRealtimeDto message) {
        String destination = "/topic/note/" + message.getNoteId();
        messagingTemplate.convertAndSend(destination, message);
    }
}


