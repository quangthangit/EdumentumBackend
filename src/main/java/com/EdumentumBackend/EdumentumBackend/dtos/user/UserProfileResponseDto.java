package com.EdumentumBackend.EdumentumBackend.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDto {
    private String username;
    private String imageUrl;
    private String bannerUrl;
}
