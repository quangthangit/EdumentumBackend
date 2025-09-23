package com.EdumentumBackend.EdumentumBackend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NoteType {
    MARKDOWN("markdown"),
    BLOCK("block");

    private final String value;

    NoteType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static NoteType fromValue(String value) {
        for (NoteType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown NoteType: " + value);
    }
}
