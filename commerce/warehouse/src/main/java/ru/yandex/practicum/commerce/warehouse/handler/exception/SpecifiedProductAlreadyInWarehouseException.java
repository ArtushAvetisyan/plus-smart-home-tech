package ru.yandex.practicum.commerce.warehouse.handler.exception;

import lombok.Getter;

@Getter
public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {
    private final String userMessage;

    public SpecifiedProductAlreadyInWarehouseException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}