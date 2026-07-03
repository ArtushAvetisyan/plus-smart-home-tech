package ru.yandex.practicum.commerce.shopping.cart.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.shopping.cart.handler.exception.CartNotFoundException;
import ru.yandex.practicum.commerce.shopping.cart.handler.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.shopping.cart.handler.exception.NotAuthorizedUserException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCartNotFoundException(CartNotFoundException exception) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage(exception.getUserMessage())
                .httpStatus(HttpStatus.NOT_FOUND.name())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoProductsInShoppingCartException(NoProductsInShoppingCartException exception) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage(exception.getUserMessage())
                .httpStatus(HttpStatus.NOT_FOUND.name())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleNotAuthorizedUserException(NotAuthorizedUserException exception) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage(exception.getUserMessage())
                .httpStatus(HttpStatus.UNAUTHORIZED.name())
                .build();
    }
}