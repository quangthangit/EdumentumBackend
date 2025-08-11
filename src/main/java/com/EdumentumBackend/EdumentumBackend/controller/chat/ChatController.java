package com.EdumentumBackend.EdumentumBackend.controller.chat;

import com.EdumentumBackend.EdumentumBackend.dtos.chat.ChatMessageDto;
import com.EdumentumBackend.EdumentumBackend.service.ChatRedisService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRedisService chatRedisService;

    public ChatController(ChatRedisService chatRedisService, SimpMessagingTemplate simpMessagingTemplate) {
        this.chatRedisService = chatRedisService;
        this.messagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageDto message) {
        String destination = "/topic/room/" + message.getRoomId();
        chatRedisService.saveMessage(message.getRoomId(),message);
        messagingTemplate.convertAndSend(destination, message);
    }
}

