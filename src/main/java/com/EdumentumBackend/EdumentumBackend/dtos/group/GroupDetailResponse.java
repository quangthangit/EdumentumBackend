package com.EdumentumBackend.EdumentumBackend.dtos.group;

import com.EdumentumBackend.EdumentumBackend.enums.GroupTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDetailResponse {
    private String publicId;
    private Long ownerId;
    private String ownerName;
    private int memberCount;
    private int memberLimit;
    private String key;
    private String name;
    private String description;
    private GroupTier groupTier;
    private List<UserGroupResponse> userGroupResponseList;
    private int contributionPoints;
    private LocalDateTime createdAt;
}
