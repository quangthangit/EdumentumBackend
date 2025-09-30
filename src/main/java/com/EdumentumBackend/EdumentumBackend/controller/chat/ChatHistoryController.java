package com.EdumentumBackend.EdumentumBackend.controller.chat;

import com.EdumentumBackend.EdumentumBackend.dtos.chat.ChanelRequestDto;
import com.EdumentumBackend.EdumentumBackend.dtos.chat.ChatMessageDto;
import com.EdumentumBackend.EdumentumBackend.service.redis.ChatRedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/chanel/{groupId}")
    public ResponseEntity<?> createChanel(@PathVariable String groupId,
                                          @RequestBody String name
                                          ) {
        return ResponseEntity.ok(Map.of(
                "data", chatRedisService.createChanel(groupId,name)
        ));
    }

    @GetMapping("/chanel/{groupId}")
    public ResponseEntity<?> getChanel(@PathVariable String groupId) {
        return ResponseEntity.ok(Map.of(
                "data", chatRedisService.getChannel(groupId)
        ));
    }

    @GetMapping("/groups/{groupId}/{channelId}/messages")
    public ResponseEntity<?> getMessages(
            @PathVariable String groupId,
            @PathVariable String channelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        List<ChatMessageDto> pagedMessages = chatRedisService.getRecentMessages(groupId, channelId, page, size);

        String messageKey = "chat:group:" + groupId + ":messages:" + channelId;
        Long totalRecords = redisTemplate.opsForList().size(messageKey);
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
