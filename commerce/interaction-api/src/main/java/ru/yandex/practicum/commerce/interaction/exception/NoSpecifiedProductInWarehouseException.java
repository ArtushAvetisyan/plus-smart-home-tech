package ru.yandex.practicum.commerce.interaction.exception;

import lombok.Getter;

@Getter
public class NoSpecifiedProductInWarehouseException extends RuntimeException {
    private final String userMessage;

    public NoSpecifiedProductInWarehouseException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}