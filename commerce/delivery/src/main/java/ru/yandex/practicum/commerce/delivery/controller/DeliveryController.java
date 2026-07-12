package ru.yandex.practicum.commerce.delivery.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.delivery.service.DeliveryService;
import ru.yandex.practicum.commerce.interaction.client.DeliveryClient;
import ru.yandex.practicum.commerce.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
@Validated
public class DeliveryController implements DeliveryClient {
    private final DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto planDelivery(@Valid @RequestBody DeliveryDto deliveryDto) {
        return deliveryService.planDelivery(deliveryDto);
    }

    @PostMapping("/picked")
    public void deliveryPicked(@RequestBody @NotNull UUID orderId) {
        deliveryService.deliveryPicked(orderId);
    }

    @PostMapping("/successful")
    public void deliverySuccessful(@RequestBody @NotNull UUID orderId) {
        deliveryService.deliverySuccessful(orderId);
    }

    @PostMapping("/failed")
    public void deliveryFailed(@RequestBody @NotNull UUID orderId) {
        deliveryService.deliveryFailed(orderId);
    }

    @PostMapping("/cost")
    public BigDecimal deliveryCost(@Valid @RequestBody OrderDto orderDto) {
        return deliveryService.calculateDeliveryCost(orderDto);
    }
}