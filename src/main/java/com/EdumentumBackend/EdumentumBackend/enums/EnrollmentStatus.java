package com.EdumentumBackend.EdumentumBackend.enums;

public enum EnrollmentStatus {
    ENROLLED("Enrolled"),
    COMPLETED("Completed"),
    DROPPED("Dropped");
    
    private final String displayName;
    
    EnrollmentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
