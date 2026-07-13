package ru.yandex.practicum.commerce.interaction.exception;

import lombok.Getter;

@Getter
public class ProductInShoppingCartLowQuantityInWarehouse extends RuntimeException {
    private final String userMessage;

    public ProductInShoppingCartLowQuantityInWarehouse(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}