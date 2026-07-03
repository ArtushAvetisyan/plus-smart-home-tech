package ru.yandex.practicum.commerce.shopping.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.commerce.shopping.cart.entity.Cart;
import ru.yandex.practicum.commerce.shopping.cart.handler.exception.CartNotFoundException;
import ru.yandex.practicum.commerce.shopping.cart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.commerce.shopping.cart.repository.ShoppingCartRepository;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository cartRepository;
    private final ShoppingCartMapper cartMapper;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        Cart cart = cartRepository.findByUsername(username).orElseThrow(() ->
                new CartNotFoundException("Корзина пользователя " + username + " отсуствует",
                        "Корзина пользователя " + username + " не найдена"));

        return cartMapper.toCartDto(cart);
    }
}