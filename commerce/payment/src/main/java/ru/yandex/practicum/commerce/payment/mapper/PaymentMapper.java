package ru.yandex.practicum.commerce.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.interaction.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.interaction.dto.payment.PaymentStatus;
import ru.yandex.practicum.commerce.payment.entity.Payment;

import java.math.BigDecimal;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {UUID.class, PaymentStatus.class})
public interface PaymentMapper {

    @Mapping(target = "paymentId", expression = "java(UUID.randomUUID())")
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "productPrice", source = "productCost")
    @Mapping(target = "deliveryTotal", source = "deliveryPrice")
    @Mapping(target = "feeTotal", source = "vat")
    @Mapping(target = "totalPayment", source = "totalCost")
    @Mapping(target = "status", expression = "java(PaymentStatus.PENDING)")
    Payment toPayment(UUID orderId, BigDecimal productCost, BigDecimal deliveryPrice, BigDecimal vat, BigDecimal totalCost);

    PaymentDto toPaymentDto(Payment payment);
}