
package com.EdumentumBackend.EdumentumBackend.enums;

public enum CourseLevel {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"), 
    ADVANCED("Advanced");
    
    private final String displayName;
    
    CourseLevel(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}