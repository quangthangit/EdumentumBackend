package com.EdumentumBackend.EdumentumBackend.dtos.chat;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChanelDto {
    private String id;
    private String time;
    private String name;
    private String groupId;
    private String lastMessage;
}
