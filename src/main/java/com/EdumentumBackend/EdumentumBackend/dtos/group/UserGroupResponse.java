package com.EdumentumBackend.EdumentumBackend.dtos.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserGroupResponse {
    private Long id;
    private String username;
    private String imageUrl;
}
