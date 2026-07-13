package ru.yandex.practicum.commerce.shopping.store.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.interaction.exception.response.ErrorResponse;
import ru.yandex.practicum.commerce.interaction.exception.IncorrectCategoryException;
import ru.yandex.practicum.commerce.interaction.exception.ProductNotFoundException;

@RestControllerAdvice
public class ShoppingStoreExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProductNotFoundException(ProductNotFoundException exception) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage(exception.getUserMessage())
                .httpStatus(HttpStatus.NOT_FOUND.name())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectCategoryException(IncorrectCategoryException exception) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage(exception.getUserMessage())
                .httpStatus(HttpStatus.BAD_REQUEST.name())
                .build();
    }
}