package com.EdumentumBackend.EdumentumBackend.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChatMessageDto {
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String imageUrl;
    private String content;
    private String timestamp;
}