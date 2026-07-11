package ru.yandex.practicum.commerce.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.interaction.client.OrderClient;
import ru.yandex.practicum.commerce.interaction.client.ShoppingStoreClient;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.payment.PaymentDto;
import ru.yandex.practicum.commerce.interaction.dto.payment.PaymentStatus;
import ru.yandex.practicum.commerce.interaction.exception.NoOrderFoundException;
import ru.yandex.practicum.commerce.interaction.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.commerce.payment.entity.Payment;
import ru.yandex.practicum.commerce.payment.mapper.PaymentMapper;
import ru.yandex.practicum.commerce.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final ShoppingStoreClient storeClient;
    private final OrderClient orderClient;
    private final PaymentMapper mapper;

    @Override
    public PaymentDto createPayment(OrderDto orderDto) {
        if (orderDto == null) {
            throw new NoOrderFoundException("Данные заказа отсутствуют",
                    "Данные заказа отсутствуют");
        }

        BigDecimal productCost = calculateProductCost(orderDto);
        BigDecimal vat = productCost.multiply(BigDecimal.valueOf(0.10));
        if (orderDto.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Не указана стоимость доставки",
                    "Не указана стоимость доставки");
        }
        BigDecimal deliveryPrice = BigDecimal.valueOf(orderDto.getDeliveryPrice());
        BigDecimal totalCost = productCost.add(vat).add(deliveryPrice);

        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID());
        payment.setOrderId(orderDto.getOrderId());
        payment.setProductPrice(productCost);
        payment.setDeliveryTotal(BigDecimal.valueOf(orderDto.getDeliveryPrice()));
        payment.setFeeTotal(vat);
        payment.setTotalPayment(totalCost);
        payment.setStatus(PaymentStatus.PENDING);

        paymentRepository.save(payment);
        return mapper.toPaymentDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        if (orderDto.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Не указана стоимость доставки",
                    "Не указана стоимость доставки");
        }

        BigDecimal productCost = calculateProductCost(orderDto);
        BigDecimal vat = productCost.multiply(BigDecimal.valueOf(0.10));
        BigDecimal productWithVat = productCost.add(vat);
        return productWithVat.add(BigDecimal.valueOf(orderDto.getDeliveryPrice()));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        if (orderDto.getProducts() == null || orderDto.getProducts().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("В заказе отсутствуют товары для расчета",
                    "В заказе отсутствуют товары для расчета");
        }

        return orderDto.getProducts().entrySet().stream()
                .map(entry -> {
                    UUID productId = entry.getKey();
                    int quantity = entry.getValue();

                    BigDecimal price = storeClient.getProduct(productId).getPrice();
                    return price.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        Payment payment = getPaymentByIdOrThrow(paymentId);
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        orderClient.paymentSuccess(payment.getOrderId());
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        Payment payment = getPaymentByIdOrThrow(paymentId);
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        orderClient.paymentFailed(payment.getOrderId());
    }

    private Payment getPaymentByIdOrThrow(UUID paymentId) {
        return paymentRepository.findById(paymentId).orElseThrow(() ->
                new NoOrderFoundException("Оплата с ID " + paymentId + " не найдена",
                        "Оплата с ID " + paymentId + " не найдена"));
    }
}