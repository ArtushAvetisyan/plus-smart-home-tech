package ru.yandex.practicum.commerce.shopping.cart.service;

import ru.yandex.practicum.commerce.interaction.dto.cart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart(String username);
}