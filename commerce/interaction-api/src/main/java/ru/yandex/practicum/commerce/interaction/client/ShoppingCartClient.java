package ru.yandex.practicum.commerce.interaction.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.interaction.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.interaction.dto.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient {

    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam("username")
                                    @NotBlank(message = "Имя пользователя не может быть пустым")
                                    String username);

    @PutMapping
    ShoppingCartDto addProductToShoppingCart(@RequestParam("username")
                                             @NotBlank(message = "Имя пользователя не может быть пустым")
                                             String username,
                                             @Valid @RequestBody Map<UUID, @NotNull @Positive Integer> products);

    @DeleteMapping
    void deactivateCurrentShoppingCart(@RequestParam("username")
                                       @NotBlank(message = "Имя пользователя не может быть пустым")
                                       String username);

    @PostMapping("/remove")
    ShoppingCartDto removeFromShoppingCart(@RequestParam("username")
                                           @NotBlank(message = "Имя пользователя не может быть пустым")
                                           String username,
                                           @Valid @RequestBody @NotEmpty(message = "Список товаров не может быть пустым")
                                           List<UUID> productIds);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestParam("username")
                                          @NotBlank(message = "Имя пользователя не может быть пустым")
                                          String username,
                                          @Valid @RequestBody @NotNull ChangeProductQuantityRequest changeRequest);
}