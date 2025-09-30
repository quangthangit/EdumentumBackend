package com.EdumentumBackend.EdumentumBackend.service.impl.redis;

import com.EdumentumBackend.EdumentumBackend.dtos.chat.ChanelDto;
import com.EdumentumBackend.EdumentumBackend.dtos.chat.ChatMessageDto;
import com.EdumentumBackend.EdumentumBackend.service.redis.ChatRedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatMessageServiceImpl implements ChatRedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CHAT_KEY_PREFIX = "chat:group:";
    private static final int MAX_MESSAGES_PER_GROUP = 200;

    public ChatMessageServiceImpl(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // Key cho channel info (Hash)
    private String getChannelKey(String groupId, String channelId) {
        return CHAT_KEY_PREFIX + groupId + ":channel:" + channelId;
    }

    // Key cho messages (List)
    private String getMessageKey(String groupId, String channelId) {
        return CHAT_KEY_PREFIX + groupId + ":messages:" + channelId;
    }

    @Override
    public void saveMessage(String groupId, ChatMessageDto message) {
        message.setTimestamp(LocalDateTime.now().toString());

        try {
            String json = objectMapper.writeValueAsString(message);
            String key = getMessageKey(groupId, message.getChannelId());
            redisTemplate.opsForList().leftPush(key, json);
            redisTemplate.opsForList().trim(key, 0, MAX_MESSAGES_PER_GROUP - 1);
        } catch (Exception e) {
            System.err.println("Error serializing message: " + e.getMessage());
        }
    }

    @Override
    public List<ChatMessageDto> getRecentMessages(String groupId, String channelId, int page, int size) {
        String key = getMessageKey(groupId, channelId);

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

    @Override
    public ChanelDto createChanel(String groupId, String name) {
        String channelId = UUID.randomUUID().toString().replace("-", "");
        String channelKey = getChannelKey(groupId, channelId);

        // Lưu thông tin channel (Hash)
        redisTemplate.opsForHash().put(channelKey, "name", name);

        // Tạo tin nhắn system đầu tiên (List)
        ChatMessageDto systemMessage = ChatMessageDto.builder()
                .roomId(channelId)
                .senderId(0L)
                .senderName("System")
                .content("Channel \"" + name + "\" created successfully.")
                .timestamp(LocalDateTime.now().toString())
                .build();

        try {
            String json = objectMapper.writeValueAsString(systemMessage);
            String messageKey = getMessageKey(groupId, channelId);
            redisTemplate.opsForList().leftPush(messageKey, json);
        } catch (Exception e) {
            System.err.println("Error serializing system message: " + e.getMessage());
        }

        return ChanelDto.builder()
                .groupId(groupId)
                .id(channelId)
                .name(name)
                .build();
    }

    @Override
    public List<ChanelDto> getChannel(String groupId) {
        String pattern = CHAT_KEY_PREFIX + groupId + ":channel:*";
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys == null || keys.isEmpty()) {
            return List.of();
        }

        return keys.stream()
                .map(key -> {
                    String[] parts = key.split(":");
                    String channelId = parts[parts.length - 1];

                    String channelName = (String) redisTemplate.opsForHash().get(key, "name");
                    if (channelName != null && channelName.startsWith("\"") && channelName.endsWith("\"")) {
                        channelName = channelName.substring(1, channelName.length() - 1);
                    }

                    return ChanelDto.builder()
                            .groupId(groupId)
                            .id(channelId)
                            .name(channelName)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
