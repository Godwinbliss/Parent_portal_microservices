package com.parentportal.payment_service.service;

import com.parentportal.payment_service.dto.PaymentCreateDto;
import com.parentportal.payment_service.dto.PaymentDto;
import com.parentportal.payment_service.dto.PaymentUpdateStatusDto;

import java.util.List;

public interface PaymentService {

    PaymentDto createPayment(PaymentCreateDto paymentCreateDto);
    PaymentDto getPaymentById(Long id);
    List<PaymentDto> getAllPayments();
    List<PaymentDto> getPaymentsByParentUserId(Long parentUserId);
    List<PaymentDto> getPaymentsByStudentId(Long studentId);
    PaymentDto updatePaymentStatus(Long id, PaymentUpdateStatusDto updateStatusDto);
    void deletePayment(Long id);
}
