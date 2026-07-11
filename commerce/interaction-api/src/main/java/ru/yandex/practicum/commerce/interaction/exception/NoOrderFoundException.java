package ru.yandex.practicum.commerce.interaction.exception;

import lombok.Getter;

@Getter
public class NoOrderFoundException extends RuntimeException {
    private final String userMessage;

    public NoOrderFoundException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}