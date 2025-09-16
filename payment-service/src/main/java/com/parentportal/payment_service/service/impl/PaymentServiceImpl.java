package com.parentportal.payment_service.service.impl;

import com.parentportal.payment_service.dto.*;
import com.parentportal.payment_service.entity.Payment;
import com.parentportal.payment_service.entity.PaymentStatus;
import com.parentportal.payment_service.mapper.PaymentMapper;
import com.parentportal.payment_service.repository.PaymentRepository;
import com.parentportal.payment_service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID; // For generating transaction IDs

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final WebClient webClient;

    @Value("${user-management-service.url}")
    private String userManagementServiceUrl;

    @Value("${student-performance-service.url}")
    private String studentPerformanceServiceUrl;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              PaymentMapper paymentMapper,
                              WebClient.Builder webClientBuilder) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.webClient = webClientBuilder.build();
    }

    @Override
    @Transactional
    public PaymentDto createPayment(PaymentCreateDto paymentCreateDto) {
        // Step 1: Validate parentUserId with User Management Service
        Mono<UserDto> parentUserMono = webClient.get()
                .uri("lb://" + userManagementServiceUrl + "/api/users/{id}", paymentCreateDto.getParentUserId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new NoSuchElementException("Parent user not found or invalid ID: " + paymentCreateDto.getParentUserId())))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException("User service error during parent validation: " + clientResponse.statusCode())))
                .bodyToMono(UserDto.class);

        // Step 2: Validate studentId with Student Performance Service
        Mono<StudentDto> studentMono = webClient.get()
                .uri("lb://" + studentPerformanceServiceUrl + "/api/students/{id}", paymentCreateDto.getStudentId())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new NoSuchElementException("Student not found or invalid ID: " + paymentCreateDto.getStudentId())))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException("Student service error during student validation: " + clientResponse.statusCode())))
                .bodyToMono(StudentDto.class);

        // Combine both Mono and block until both validations are complete
        // In a fully reactive scenario, you would chain these without blocking
        Mono.zip(parentUserMono, studentMono)
                .block(); // Blocks until both calls complete

        // If we reach here, both parent and student IDs are valid.
        Payment payment = paymentMapper.paymentCreateDtoToPayment(paymentCreateDto);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PENDING); // Initial status
        payment.setTransactionId(UUID.randomUUID().toString()); // Simulate a gateway transaction ID

        Payment savedPayment = paymentRepository.save(payment);

        // In a real application, you would integrate with a payment gateway here.
        // Upon successful payment gateway interaction, you would update the status to SUCCESS.
        // For now, we'll simulate it as PENDING and then allow update via another endpoint.

        // Optionally, publish a Kafka event for payment initiation
        // kafkaTemplate.send("payment-events", "payment-initiated", paymentMapper.paymentToPaymentDto(savedPayment));

        return paymentMapper.paymentToPaymentDto(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Payment not found with ID: " + id));
        return paymentMapper.paymentToPaymentDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return paymentMapper.paymentListToPaymentDtoList(payments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByParentUserId(Long parentUserId) {
        List<Payment> payments = paymentRepository.findByParentUserId(parentUserId);
        return paymentMapper.paymentListToPaymentDtoList(payments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByStudentId(Long studentId) {
        List<Payment> payments = paymentRepository.findByStudentId(studentId);
        return paymentMapper.paymentListToPaymentDtoList(payments);
    }

    @Override
    @Transactional
    public PaymentDto updatePaymentStatus(Long id, PaymentUpdateStatusDto updateStatusDto) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Payment not found with ID: " + id));
        existingPayment.setStatus(updateStatusDto.getStatus());
        if (updateStatusDto.getTransactionId() != null && !updateStatusDto.getTransactionId().isEmpty()) {
            existingPayment.setTransactionId(updateStatusDto.getTransactionId());
        }
        Payment updatedPayment = paymentRepository.save(existingPayment);

        // Optionally, publish a Kafka event for payment status update
        // kafkaTemplate.send("payment-events", "payment-status-updated", paymentMapper.paymentToPaymentDto(updatedPayment));

        return paymentMapper.paymentToPaymentDto(updatedPayment);
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new NoSuchElementException("Payment not found with ID: " + id);
        }
        paymentRepository.deleteById(id);
    }
}
