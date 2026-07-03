package ru.yandex.practicum.commerce.shopping.store.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.shopping.store.handler.exception.IncorrectCategoryException;
import ru.yandex.practicum.commerce.shopping.store.handler.exception.ProductNotFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProductNotFoundException(ProductNotFoundException exception) {
        log.warn("Ошибка: Продукт не найден: {}", exception.getMessage());

        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage(exception.getUserMessage())
                .httpStatus(HttpStatus.NOT_FOUND.name())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectCategoryException(IncorrectCategoryException exception) {
        log.warn("Ошибка: Незвестная категория продукта: {}", exception.getMessage());

        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage(exception.getUserMessage())
                .httpStatus(HttpStatus.BAD_REQUEST.name())
                .build();
    }
}