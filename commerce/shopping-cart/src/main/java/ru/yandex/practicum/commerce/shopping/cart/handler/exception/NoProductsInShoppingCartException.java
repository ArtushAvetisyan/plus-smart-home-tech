package ru.yandex.practicum.commerce.shopping.cart.handler.exception;

import lombok.Getter;

@Getter
public class NoProductsInShoppingCartException extends RuntimeException {
    private final String userMessage;

    public NoProductsInShoppingCartException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}