package ru.yandex.practicum.commerce.interaction.exception;

import lombok.Getter;

@Getter
public class IncorrectCategoryException extends RuntimeException {
    private final String userMessage;

    public IncorrectCategoryException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }
}