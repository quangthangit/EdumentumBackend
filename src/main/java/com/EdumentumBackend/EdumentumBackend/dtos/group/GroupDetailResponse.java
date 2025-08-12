package com.EdumentumBackend.EdumentumBackend.dtos.group;

import lombok.Data;

import java.util.List;

@Data
public class GroupDetailResponse {
    private Long id;
    private Long ownerId;
    private String ownerName;
    private int memberCount;
    private int memberLimit;
    private String key;
    private String name;
    private String description;
    private List<UserGroupResponse> userGroupResponseList;
}
