package com.parentportal.payment_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentId; // ID of the student the payment is for (from Student Performance Service)
    private Long parentUserId; // ID of the parent making the payment (from User Management Service)
    private BigDecimal amount;
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // e.g., SUCCESS, PENDING, FAILED

    private String transactionId; // Unique ID from payment gateway
    private String description; // e.g., "School Fees - Q1 2024"
}
