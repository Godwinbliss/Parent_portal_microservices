package com.parent.portal.Notification.service.entity;

public enum NotificationType {

    PAYMENT_CONFIRMATION,
    NEW_MESSAGE,
    ANNOUNCEMENT,
    ATTENDANCE_ALERT,
    GRADE_UPDATE;

    public String getDisplayName() {
        return this.name().replace("_", " ").toLowerCase();
    }
}
