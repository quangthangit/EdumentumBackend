package com.EdumentumBackend.EdumentumBackend.enums;

public enum ResourceType {
    PDF("PDF Document"),
    VIDEO("Video"),
    LINK("External Link"),
    IMAGE("Image"),
    AUDIO("Audio");
    
    private final String displayName;
    
    ResourceType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}