package ru.yandex.practicum.commerce.interaction.client;

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
    ShoppingCartDto getShoppingCart(@RequestParam("username") String username);

    @PutMapping
    ShoppingCartDto addProductToShoppingCart(@RequestParam("username") String username,
                                             @RequestBody Map<UUID, Integer> products);

    @DeleteMapping
    void deactivateCurrentShoppingCart(@RequestParam("username") String username);

    @PostMapping("/remove")
    ShoppingCartDto removeFromShoppingCart(@RequestParam("username") String username,
                                           @RequestBody List<UUID> productIds);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestParam("username") String username,
                                          @RequestBody ChangeProductQuantityRequest changeRequest);
}