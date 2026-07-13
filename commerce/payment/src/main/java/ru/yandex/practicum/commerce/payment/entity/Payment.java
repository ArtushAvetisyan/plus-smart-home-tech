package ru.yandex.practicum.commerce.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.commerce.interaction.dto.payment.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @Column(name = "payment_id", nullable = false, updatable = false)
    private UUID paymentId;

    @Column(name = "order_id", nullable = false, updatable = false)
    private UUID orderId;

    @Column(name = "product_price")
    private BigDecimal productPrice;

    @Column(name = "delivery_total")
    private BigDecimal deliveryTotal;

    @Column(name = "fee_total")
    private BigDecimal feeTotal;

    @Column(name = "total_payment")
    private BigDecimal totalPayment;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}