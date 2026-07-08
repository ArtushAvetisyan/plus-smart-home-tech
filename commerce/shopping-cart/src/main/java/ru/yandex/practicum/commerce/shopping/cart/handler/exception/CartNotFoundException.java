package ru.yandex.practicum.commerce.shopping.cart.handler.exception;

import lombok.Getter;

@Getter
public class CartNotFoundException extends RuntimeException {
    private final String userMessage;

    public CartNotFoundException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}