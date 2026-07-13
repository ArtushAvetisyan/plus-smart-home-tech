package ru.yandex.practicum.commerce.interaction.exception;

import lombok.Getter;

@Getter
public class NoDeliveryFoundException extends RuntimeException {
    private final String userMessage;

    public NoDeliveryFoundException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}