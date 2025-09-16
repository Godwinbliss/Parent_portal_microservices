package com.parentportal.payment_service.repository;

import com.parentportal.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByParentUserId(Long parentUserId);
    List<Payment> findByStudentId(Long studentId);
    Optional<Payment> findByTransactionId(String transactionId);
}
