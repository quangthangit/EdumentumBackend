package com.EdumentumBackend.EdumentumBackend.service;

import com.EdumentumBackend.EdumentumBackend.dtos.chat.ChatMessageDto;

import java.util.List;

public interface ChatRedisService {
    void saveMessage(Long groupId, ChatMessageDto message);
    List<ChatMessageDto> getRecentMessages(Long groupId, int count);
}
