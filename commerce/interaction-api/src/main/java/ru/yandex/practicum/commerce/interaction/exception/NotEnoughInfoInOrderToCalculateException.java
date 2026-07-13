package ru.yandex.practicum.commerce.interaction.exception;

import lombok.Getter;

@Getter
public class NotEnoughInfoInOrderToCalculateException extends RuntimeException {
    private final String userMessage;

    public NotEnoughInfoInOrderToCalculateException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}