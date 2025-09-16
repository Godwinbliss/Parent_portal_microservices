package com.parentportal.payment_service.dto;

import com.parentportal.payment_service.entity.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateDto {
    @NotNull(message = "Student ID is required")
    private Long studentId;
    @NotNull(message = "Parent User ID is required")
    private Long parentUserId;
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    @NotBlank(message = "Description is required")
    private String description;

    // Default status for new payments will be PENDING
    private PaymentStatus status = PaymentStatus.PENDING;
}
