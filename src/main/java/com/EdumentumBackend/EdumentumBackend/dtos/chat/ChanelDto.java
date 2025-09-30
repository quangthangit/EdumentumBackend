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
    private String name;
    private String groupId;
}
