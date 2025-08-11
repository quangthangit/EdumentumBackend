package com.EdumentumBackend.EdumentumBackend.service.scheduler;

import com.EdumentumBackend.EdumentumBackend.entity.ChatMessageEntity;
import com.EdumentumBackend.EdumentumBackend.repository.ChatMessageRepository;
import com.EdumentumBackend.EdumentumBackend.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatFlushScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ChatFlushScheduler.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final GroupRepository groupRepository;

    private static final String REDIS_KEY_PATTERN = "chat:group:*";
    private static final int KEEP_RECENT_MESSAGE_COUNT = 200;
    private static final int BATCH_SIZE = 500;


    /**
     * Periodically flushes chat messages stored in Redis to the database.
     */
    @Scheduled(fixedRate = 300000)
    public void flushRedisToDatabase() {
        Set<String> keys = redisTemplate.keys(REDIS_KEY_PATTERN);
        if (keys.isEmpty()) return;

        for (String key : keys) {
            processKey(key);
        }
    }

    /**
     * Reads each Redis key matching the pattern and saves the corresponding data into the database in batches.
     */
    private void processKey(String key) {
        Long totalSize = redisTemplate.opsForList().size(key);
        if (totalSize == null || totalSize <= KEEP_RECENT_MESSAGE_COUNT) return;

        List<String> messagesToFlush = fetchMessagesToFlush(key);
        if (messagesToFlush.isEmpty()) return;

        Long groupId = extractGroupIdFromKey(key);
        if (groupId == null) return;

        saveMessagesInBatches(messagesToFlush, groupId);
        redisTemplate.opsForList().trim(key, 0, KEEP_RECENT_MESSAGE_COUNT - 1);
    }

    /**
     * Retrieves messages from the Redis list starting from index KEEP_RECENT_MESSAGE_COUNT onward.
     */
    private List<String> fetchMessagesToFlush(String key) {
        List<String> messages = redisTemplate.opsForList().range(key, KEEP_RECENT_MESSAGE_COUNT, -1);
        return messages != null ? messages : Collections.emptyList();
    }

    /**
     * Reads each Redis key matching the pattern and saves the corresponding data into the database in batches.
     */
    private void saveMessagesInBatches(List<String> messages, Long groupId) {
        List<ChatMessageEntity> entities = messages.stream()
                .map(msg -> ChatMessageEntity.builder()
                        .groupId(groupId)
                        .data(msg)
                        .build())
                .collect(Collectors.toList());


        for (int i = 0; i < entities.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, entities.size());
            List<ChatMessageEntity> batch = entities.subList(i, end);
            chatMessageRepository.saveAll(batch);
            logger.info("Saved {} messages for groupId={} (batch {}/{}).",
                    batch.size(), groupId, (i / BATCH_SIZE) + 1, (entities.size() + BATCH_SIZE - 1) / BATCH_SIZE);
        }
    }

    private Long extractGroupIdFromKey(String key) {
        try {
            String[] parts = key.split(":");
            return Long.parseLong(parts[2]);
        } catch (Exception e) {
            logger.error("Failed to extract groupId from key: {}", key, e);
            return null;
        }
    }
}
