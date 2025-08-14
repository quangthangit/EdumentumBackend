package com.EdumentumBackend.EdumentumBackend.enums;

import lombok.Getter;

@Getter
public enum GroupTier {
    BRONZE(0, 500),       // 0 POINT → 500 MB
    SILVER(100, 1024),    // 100 POINT → 1 GB
    GOLD(500, 5120),      // 500 POINT → 5 GB
    PLATINUM(1000, 10240),// 1000 POINT → 10 GB
    DIAMOND(2000, 51200); // 2000 POINT → 50 GB

    private final int minContribution;
    private final int maxStorageMB;

    GroupTier(int minContribution, int maxStorageMB) {
        this.minContribution = minContribution;
        this.maxStorageMB = maxStorageMB;
    }
}
