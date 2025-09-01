package com.EdumentumBackend.EdumentumBackend.dtos.user;

import lombok.Data;

@Data
public class UserProfileRequestDto {
    private String username;
    private String imageUrl;
    private String bannerUrl;
}
