package com.parentportal.payment_service.dto;

import com.parentportal.payment_service.entity.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUpdateStatusDto {
    @NotNull(message = "Payment status is required")
    private PaymentStatus status;
    private String transactionId; // Optional: to link to external gateway ID
}
