package com.parentportal.student_performance_service.entity;

import org.springframework.stereotype.Component;

public enum AttendanceStatus {
    PRESENT,
    ABSENT,
    LATE;

    // You can add a method to get a more user-friendly display name if needed
    public String getDisplayName() {
        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }
}
