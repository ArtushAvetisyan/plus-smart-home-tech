package ru.yandex.practicum.commerce.shopping.cart.handler;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.interaction.exception.CartNotFoundException;
import ru.yandex.practicum.commerce.interaction.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.interaction.exception.NotAuthorizedUserException;
import ru.yandex.practicum.commerce.interaction.exception.response.ErrorResponse;

@RestControllerAdvice
public class ShoppingCartExceptionHandler {

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

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleCircuitBreakerOpen(CallNotPermittedException exception) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage("Сервис склада временно недоступен из-за частых сбоев. Пожалуйста, попробуйте оформить заказ позже.")
                .httpStatus(HttpStatus.SERVICE_UNAVAILABLE.name())
                .build();
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse handleFeignException(FeignException exception) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .userMessage("Не удалось связаться с сервисом склада. Проверьте параметры заказа или повторите попытку.")
                .httpStatus(HttpStatus.BAD_GATEWAY.name())
                .build();
    }
}