package ru.yandex.practicum.commerce.order.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.client.OrderClient;
import ru.yandex.practicum.commerce.interaction.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.order.ProductReturnRequest;
import ru.yandex.practicum.commerce.order.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Validated
public class OrderController implements OrderClient {
    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getClientOrders(
            @RequestParam("username")
            @NotBlank(message = "Имя пользователя не может быть пустым")
            String username) {
        return orderService.getClientOrders(username);
    }

    @PutMapping
    public OrderDto createNewOrder(@RequestParam("username")
                                   @NotBlank(message = "Имя пользователя не может быть пустым")
                                   String username,
                                   @Valid @RequestBody CreateNewOrderRequest request) {
        return orderService.createNewOrder(username, request);
    }

    @PostMapping("/return")
    public OrderDto productReturn(@Valid @RequestBody ProductReturnRequest request) {
        return orderService.productReturn(request);
    }

    @PostMapping("/payment")
    public OrderDto paymentSuccess(@RequestBody @NotNull UUID orderId) {
        return orderService.paymentSuccess(orderId);
    }

    @PostMapping("/payment/failed")
    public void paymentFailed(@RequestBody @NotNull UUID orderId) {
        orderService.paymentFailed(orderId);
    }

    @PostMapping("/delivery")
    public OrderDto delivery(@RequestBody @NotNull UUID orderId) {
        return orderService.delivery(orderId);
    }

    @PostMapping("/delivery/failed")
    public void deliveryFailed(@RequestBody @NotNull UUID orderId) {
        orderService.deliveryFailed(orderId);
    }

    @PostMapping("/completed")
    public OrderDto complete(@RequestBody @NotNull UUID orderId) {
        return orderService.complete(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotalCost(@RequestBody @NotNull UUID orderId) {
        return orderService.calculateTotalCost(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryCost(@RequestBody @NotNull UUID orderId) {
        return orderService.calculateDeliveryCost(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto assembly(@RequestBody @NotNull UUID orderId) {
        return orderService.assembly(orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailed(@RequestBody @NotNull UUID orderId) {
        return orderService.assemblyFailed(orderId);
    }
}