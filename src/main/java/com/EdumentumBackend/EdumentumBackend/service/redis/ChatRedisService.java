package com.EdumentumBackend.EdumentumBackend.service.redis;

import com.EdumentumBackend.EdumentumBackend.dtos.chat.ChanelDto;
import com.EdumentumBackend.EdumentumBackend.dtos.chat.ChatMessageDto;

import java.util.List;

public interface ChatRedisService {
    void saveMessage(String groupId, ChatMessageDto message);
    List<ChatMessageDto> getRecentMessages(String groupId,String channelId, int page, int size);
    ChanelDto createChanel(String groupId, String name);
    List<ChanelDto> getChannel(String groupId);
}
