package ru.yandex.practicum.commerce.interaction.exception.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private final String message;
    private final String userMessage;
    private final String httpStatus;
}