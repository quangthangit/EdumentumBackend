package com.EdumentumBackend.EdumentumBackend.controller.chat;

import com.EdumentumBackend.EdumentumBackend.dtos.ChatMessageDto;
import com.EdumentumBackend.EdumentumBackend.service.ChatRedisService;
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

    public ChatHistoryController(ChatRedisService chatRedisService) {
        this.chatRedisService = chatRedisService;
    }

    @GetMapping("/groups/{groupId}/messages")
    public ResponseEntity<?> getMessages(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        List<ChatMessageDto> allMessages = chatRedisService.getRecentMessages(groupId, 1000);
        int totalRecords = allMessages.size();
        int totalPage = (int) Math.ceil((double) totalRecords / size);

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalRecords);

        List<ChatMessageDto> pagedMessages = allMessages.subList(fromIndex, toIndex);

        return ResponseEntity.ok(Map.of(
                "data", pagedMessages,
                "pageSize", size,
                "pageNumber", page,
                "totalRecords" , totalRecords,
                "totalPage", totalPage,
                "hasNext", page < totalPage - 1,
                "hasPrevious" , page > 0
        ));
    }
}
