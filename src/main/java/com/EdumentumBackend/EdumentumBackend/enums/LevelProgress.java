package com.EdumentumBackend.EdumentumBackend.enums;

import lombok.Getter;

@Getter
public enum LevelProgress {

    LEVEL_1(0),
    LEVEL_2(120),
    LEVEL_3(240),
    LEVEL_4(480),
    LEVEL_5(960),
    LEVEL_6(1920),
    LEVEL_7(3840),
    LEVEL_8(7680),
    LEVEL_9(15360),
    LEVEL_10(30720);

    private final int level;

    LevelProgress(int level) {
        this.level = level;
    }
}
