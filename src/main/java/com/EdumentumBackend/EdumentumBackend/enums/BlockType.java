package com.EdumentumBackend.EdumentumBackend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BlockType {
    // Text & Heading
    PARAGRAPH("paragraph"),
    HEADING_1("heading_1"),
    HEADING_2("heading_2"),
    HEADING_3("heading_3"),

    // List & Todo
    BULLETED_LIST_ITEM("bulleted_list_item"),
    NUMBERED_LIST_ITEM("numbered_list_item"),
    TO_DO("to_do"),
    TOGGLE("toggle"),

    // Formatting
    QUOTE("quote"),
    CALLOUT("callout"),
    DIVIDER("divider"),
    CODE("code"),

    // Media & Data
    TABLE("table"),
    TABLE_ROW("table_row"),
    IMAGE("image"),
    VIDEO("video"),
    FILE("file"),
    BOOKMARK("bookmark"),
    EMBED("embed"),

    // Advanced / Database
    PAGE("page"),
    DATABASE_TABLE("database_table"),
    DATABASE_BOARD("database_board"),
    DATABASE_CALENDAR("database_calendar"),
    DATABASE_GALLERY("database_gallery"),

    // Inline Embed
    EQUATION("equation"),
    MENTION_USER("mention_user"),
    MENTION_PAGE("mention_page"),
    MENTION_DATE("mention_date");

    private final String value;

    BlockType(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static BlockType fromValue(String value) {
        for (BlockType t : values()) {
            if (t.value.equalsIgnoreCase(value)) return t;
        }
        throw new IllegalArgumentException("Unknown BlockType: " + value);
    }
}


