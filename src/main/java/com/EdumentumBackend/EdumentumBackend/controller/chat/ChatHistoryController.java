package com.EdumentumBackend.EdumentumBackend.controller.chat;

import com.EdumentumBackend.EdumentumBackend.dtos.chat.ChatMessageDto;
import com.EdumentumBackend.EdumentumBackend.service.ChatRedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/chat")
public class ChatHistoryController {

    private final ChatRedisService chatRedisService;
    private final RedisTemplate<String, String> redisTemplate;

    public ChatHistoryController(ChatRedisService chatRedisService,RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.chatRedisService = chatRedisService;
    }

    @GetMapping("/groups/{groupId}/messages")
    public ResponseEntity<?> getMessages(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        List<ChatMessageDto> pagedMessages = chatRedisService.getRecentMessages(groupId, page, size);

        Long totalRecords = redisTemplate.opsForList().size("chat:group:" + groupId);
        int totalPage = (int) Math.ceil((double) totalRecords / size);

        return ResponseEntity.ok(Map.of(
                "data", pagedMessages,
                "pageSize", size,
                "pageNumber", page,
                "totalRecords", totalRecords,
                "totalPage", totalPage,
                "hasNext", page < totalPage - 1,
                "hasPrevious", page > 0
        ));
    }
}
