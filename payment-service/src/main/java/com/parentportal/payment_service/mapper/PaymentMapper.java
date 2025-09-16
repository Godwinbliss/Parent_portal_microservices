package com.parentportal.payment_service.mapper;

import com.parentportal.payment_service.dto.PaymentCreateDto;
import com.parentportal.payment_service.dto.PaymentDto;
import com.parentportal.payment_service.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    PaymentDto paymentToPaymentDto(Payment payment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentDate", ignore = true) // Date will be set by service
    @Mapping(target = "transactionId", ignore = true) // Transaction ID will be generated/set by service
    Payment paymentCreateDtoToPayment(PaymentCreateDto paymentCreateDto);

    List<PaymentDto> paymentListToPaymentDtoList(List<Payment> payments);
}
