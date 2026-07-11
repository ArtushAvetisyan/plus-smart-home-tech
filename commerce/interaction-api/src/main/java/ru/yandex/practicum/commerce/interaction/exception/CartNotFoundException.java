package ru.yandex.practicum.commerce.interaction.exception;

import lombok.Getter;

@Getter
public class CartNotFoundException extends RuntimeException {
    private final String userMessage;

    public CartNotFoundException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}