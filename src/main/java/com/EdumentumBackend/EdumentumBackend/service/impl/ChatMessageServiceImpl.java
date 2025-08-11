package com.EdumentumBackend.EdumentumBackend.service.impl;

import com.EdumentumBackend.EdumentumBackend.dtos.chat.ChatMessageDto;
import com.EdumentumBackend.EdumentumBackend.entity.GroupEntity;
import com.EdumentumBackend.EdumentumBackend.entity.UserEntity;
import com.EdumentumBackend.EdumentumBackend.exception.NotFoundException;
import com.EdumentumBackend.EdumentumBackend.repository.GroupRepository;
import com.EdumentumBackend.EdumentumBackend.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public ChatMessageServiceImpl(RedisTemplate<String, String> redisTemplate,ObjectMapper objectMapper,UserRepository userRepository,GroupRepository groupRepository) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    private String getKey(Long groupId) {
        return CHAT_KEY_PREFIX + groupId;
    }

    public void saveMessage(Long groupId, ChatMessageDto message) {

        UserEntity user = userRepository.findById(message.getSenderId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Group not found"));

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

    public List<ChatMessageDto> getRecentMessages(Long groupId, int count) {
        String key = getKey(groupId);
        List<String> jsonMessages = redisTemplate.opsForList().range(key, 0, count - 1);
        if (jsonMessages == null || jsonMessages.isEmpty()) return List.of();

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
