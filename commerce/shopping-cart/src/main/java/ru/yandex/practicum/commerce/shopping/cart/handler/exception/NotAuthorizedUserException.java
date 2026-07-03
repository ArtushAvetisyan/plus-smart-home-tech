package ru.yandex.practicum.commerce.shopping.cart.handler.exception;

import lombok.Getter;

@Getter
public class NotAuthorizedUserException extends RuntimeException {
    private final String userMessage;

    public NotAuthorizedUserException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}