package ru.yandex.practicum.commerce.interaction.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.interaction.dto.order.OrderDto;
import ru.yandex.practicum.commerce.interaction.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient {

    @GetMapping()
    List<OrderDto> getClientOrders(@RequestParam("username")
                                   @NotBlank(message = "Имя пользователя не может быть пустым")
                                   String username);

    @PutMapping()
    OrderDto createNewOrder(@RequestParam("username")
                            @NotBlank(message = "Имя пользователя не может быть пустым")
                            String username,
                            @Valid @RequestBody CreateNewOrderRequest request);

    @PostMapping("/return")
    OrderDto productReturn(@Valid @RequestBody ProductReturnRequest request);

    @PostMapping("/payment")
    OrderDto paymentSuccess(@RequestBody @NotNull UUID orderId);

    @PostMapping("/payment/failed")
    void paymentFailed(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery")
    OrderDto delivery(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery/failed")
    void deliveryFailed(@RequestBody @NotNull UUID orderId);

    @PostMapping("/completed")
    OrderDto complete(@RequestBody @NotNull UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotalCost(@RequestBody @NotNull UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryCost(@RequestBody @NotNull UUID orderId);

    @PostMapping("/assembly")
    OrderDto assembly(@RequestBody @NotNull UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto assemblyFailed(@RequestBody @NotNull UUID orderId);
}