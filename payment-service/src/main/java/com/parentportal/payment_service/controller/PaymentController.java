package com.parentportal.payment_service.controller;

import com.parentportal.payment_service.dto.PaymentCreateDto;
import com.parentportal.payment_service.dto.PaymentDto;
import com.parentportal.payment_service.dto.PaymentUpdateStatusDto;
import com.parentportal.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Initiates a new payment. This will validate student and parent IDs
     * before creating a payment record with PENDING status.
     * @param paymentCreateDto DTO containing payment creation details.
     * @return The created payment as a DTO with HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@Valid @RequestBody PaymentCreateDto paymentCreateDto) {
        try {
            PaymentDto createdPayment = paymentService.createPayment(paymentCreateDto);
            return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            // This could be due to parent or student not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            // For other validation errors, e.g., invalid amount
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            // Catching generic runtime exceptions from WebClient calls
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a payment by its ID.
     * @param id The ID of the payment.
     * @return The payment as a DTO if found, or HTTP status 404 (Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id) {
        try {
            PaymentDto payment = paymentService.getPaymentById(id);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves all payment records.
     * @return A list of all payments as DTOs.
     */
    @GetMapping
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        List<PaymentDto> payments = paymentService.getAllPayments();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    /**
     * Retrieves payment records associated with a specific parent user ID.
     * @param parentUserId The ID of the parent user.
     * @return A list of payments as DTOs associated with the parent.
     */
    @GetMapping("/byParent/{parentUserId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByParentUserId(@PathVariable Long parentUserId) {
        List<PaymentDto> payments = paymentService.getPaymentsByParentUserId(parentUserId);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    /**
     * Retrieves payment records associated with a specific student ID.
     * @param studentId The ID of the student.
     * @return A list of payments as DTOs associated with the student.
     */
    @GetMapping("/byStudent/{studentId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByStudentId(@PathVariable Long studentId) {
        List<PaymentDto> payments = paymentService.getPaymentsByStudentId(studentId);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    /**
     * Updates the status of an existing payment.
     * @param id The ID of the payment to update.
     * @param updateStatusDto DTO containing the new status and optional transaction ID.
     * @return The updated payment as a DTO if found, or HTTP status 404 (Not Found).
     */
    @PatchMapping("/{id}/status") // Using PATCH for partial update (status only)
    public ResponseEntity<PaymentDto> updatePaymentStatus(@PathVariable Long id, @Valid @RequestBody PaymentUpdateStatusDto updateStatusDto) {
        try {
            PaymentDto updatedPayment = paymentService.updatePaymentStatus(id, updateStatusDto);
            return new ResponseEntity<>(updatedPayment, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a payment record by ID.
     * @param id The ID of the payment to delete.
     * @return HTTP status 204 (No Content) on successful deletion, or 404 (Not Found) if payment does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePayment(@PathVariable Long id) {
        try {
            paymentService.deletePayment(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
