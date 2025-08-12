package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.chat.ChatMessageDto;
import com.EdumentumBackend.EdumentumBackend.service.ChatRedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ChatMessageServiceImpl implements ChatRedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CHAT_KEY_PREFIX = "chat:group:";
    private static final int MAX_MESSAGES_PER_GROUP = 200;

    public ChatMessageServiceImpl(RedisTemplate<String, String> redisTemplate,ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    private String getKey(Long groupId) {
        return CHAT_KEY_PREFIX + groupId;
    }

    public void saveMessage(Long groupId, ChatMessageDto message) {
        message.setTimestamp(LocalDateTime.now().toString());
        try {
            String json = objectMapper.writeValueAsString(message);
            String key = getKey(groupId);
            redisTemplate.opsForList().leftPush(key, json);
            redisTemplate.opsForList().trim(key, 0, MAX_MESSAGES_PER_GROUP - 1);
        } catch (Exception e) {
            System.err.println("Error serializing message: " + e.getMessage());
        }
    }

    @Override
    public List<ChatMessageDto> getRecentMessages(Long groupId, int page, int size) {
        String key = getKey(groupId);

        int start = page * size;
        int end = start + size - 1;

        List<String> jsonMessages = redisTemplate.opsForList().range(key, start, end);
        if (jsonMessages == null || jsonMessages.isEmpty()) {
            return List.of();
        }

        return jsonMessages.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, ChatMessageDto.class);
                    } catch (Exception e) {
                        System.err.println("Error deserializing message: " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
