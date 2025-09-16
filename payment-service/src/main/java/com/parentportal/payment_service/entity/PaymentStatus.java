package com.parentportal.payment_service.entity;

public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED;

    public String getDisplayName() {
        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }
}
